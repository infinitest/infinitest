/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;

import java.awt.Color;

class ColorAnimator
{
    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private int angerLevel;
    private int animationCounter;
    private int animationStep = 1;
    private static final int[] ANGER_LEVEL_TIME_CUTOFFS = new int[] { MINUTE, MINUTE * 2, MINUTE * 3, MINUTE * 5,
                    MINUTE * 8, MINUTE * 13, MINUTE * 21, MINUTE * 34, MINUTE * 55, MINUTE * 89 };

    int getAngerLevel()
    {
        return angerLevel;
    }

    void setAngerLevel(int level)
    {
        if (level > getMaxAngerLevel())
        {
            throw new IllegalArgumentException("Level must be less than or equal to 10, was " + level);
        }
        if (level < 0)
        {
            throw new IllegalArgumentException("Level must be greater than zero, was " + level);
        }
        angerLevel = level;
        updateStep();
    }

    static int getMaxAngerLevel()
    {
        return ANGER_LEVEL_TIME_CUTOFFS.length;
    }

    public Color shiftColorOnAnimationTick(Color color)
    {
        if (color.equals(FAILING_COLOR))
        {
            return shiftFailingColor(color);
        }
        return shiftPassingColor(color);
    }

    private Color shiftPassingColor(Color color)
    {
        int r = shiftChannel(color.getRed());
        int g = shiftChannel(color.getGreen());
        int b = shiftChannel(color.getBlue());
        return new Color(r, g, b);
    }

    private int shiftChannel(int channel)
    {
        double scaleFactor = getAngerLevel() / 10.0d;
        int grayChannel = 128;
        int shift = (int) (scaleFactor * (grayChannel - channel));
        return channel + shift;
    }

    private Color shiftFailingColor(Color color)
    {
        double shift = getColorShiftForAnimationTick();
        if (shift < 0)
        {
            throw new IllegalStateException("Shift cannot be less than zero! " + getAnimationLength() + " "
                            + getAngerLevel() + " " + animationCounter);
        }
        return new Color((int) (color.getRed() * shift), (int) (color.getGreen() * shift),
                        (int) (color.getBlue() * shift));
    }

    double getColorShiftForAnimationTick()
    {
        double maxDelta = getAngerLevel() * 0.1d;
        double delta = maxDelta * animationCounter / getAnimationLength();
        return 1.0d - delta;
    }

    private int getAnimationLength()
    {
        return getMaxAngerLevel() + 1 - getAngerLevel();
    }

    public int getAnimationRate()
    {
        return 250;
    }

    public void tick()
    {
        animationCounter += animationStep;
        updateStep();
    }

    private void updateStep()
    {
        if (animationCounter >= getAnimationLength())
        {
            animationStep = -1;
            animationCounter = getAnimationLength();
        }
        if (animationCounter == 0)
        {
            animationStep = 1;
        }
    }

    public void setAngerBasedOnTime(long timeSinceGreen)
    {
        int anger = 0;
        while (anger < ANGER_LEVEL_TIME_CUTOFFS.length && timeSinceGreen >= ANGER_LEVEL_TIME_CUTOFFS[anger])
        {
            anger++;
        }
        setAngerLevel(anger);
    }
}
