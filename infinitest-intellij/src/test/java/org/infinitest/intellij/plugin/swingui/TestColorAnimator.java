/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.intellij.plugin.swingui;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.FAILING_COLOR;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.PASSING_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestColorAnimator {
	private ColorAnimator animator;

	@BeforeEach
	void inContext() {
		animator = new ColorAnimator();
	}

	@AfterEach
	void cleanup() {
		animator = null;
	}

	@Test
	void shouldTurnGreenToGray() {
		animator.setAngerLevel(0);
		assertEquals(PASSING_COLOR, animator.shiftColorOnAnimationTick(PASSING_COLOR));

		animator.setAngerLevel(10);
		assertEquals(Color.GRAY, animator.shiftColorOnAnimationTick(Color.GREEN));
	}

	@Test
	void shouldAdjustAnimationWhenAngerLevelChanges() {
		animator.setAngerLevel(1);
		for (int i = 0; i < 8; i++) {
			animator.tick();
		}

		animator.setAngerLevel(10);
		assertEquals(Color.BLACK, animator.shiftColorOnAnimationTick(FAILING_COLOR));
	}

	private void verifyColorDeltaForAnimationTicks(int tickSteps, double colorDelta) {
		double colorShiftForASingleAnimationTick;
		skipTicks(tickSteps);
		colorShiftForASingleAnimationTick = animator.getColorShiftForAnimationTick();
		assertEquals(colorDelta, colorShiftForASingleAnimationTick, Double.MIN_VALUE);
		verifyColor(colorShiftForASingleAnimationTick);
	}

	@Test
	void shouldBeAbleToSetAngerLevelBasedOnTime() {
		int second = 1000;
		int minute = second * 60;
		animator.setAngerBasedOnTime(minute - 1);
		assertEquals(0, animator.getAngerLevel());

		animator.setAngerBasedOnTime(minute);
		assertEquals(1, animator.getAngerLevel());

		animator.setAngerBasedOnTime(2 * minute);
		assertEquals(2, animator.getAngerLevel());

		animator.setAngerBasedOnTime(3 * minute);
		assertEquals(3, animator.getAngerLevel());

		animator.setAngerBasedOnTime(5 * minute);
		assertEquals(4, animator.getAngerLevel());

		animator.setAngerBasedOnTime(8 * minute);
		assertEquals(5, animator.getAngerLevel());

		animator.setAngerBasedOnTime(13 * minute);
		assertEquals(6, animator.getAngerLevel());

		animator.setAngerBasedOnTime(21 * minute);
		assertEquals(7, animator.getAngerLevel());

		animator.setAngerBasedOnTime(34 * minute);
		assertEquals(8, animator.getAngerLevel());

		animator.setAngerBasedOnTime(55 * minute);
		assertEquals(9, animator.getAngerLevel());

		animator.setAngerBasedOnTime(89 * minute);
		assertEquals(10, animator.getAngerLevel());
	}

	@Test
	void shouldThrowExceptionIfAngerIsTooHigh() {
		int maxAngerLevel = ColorAnimator.getMaxAngerLevel();
		assertThatThrownBy(() -> animator.setAngerLevel(maxAngerLevel + 1)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrowExceptionIfAngerLevelIsBelowZero() {
		assertThatThrownBy(() -> animator.setAngerLevel(-1)).isInstanceOf(IllegalArgumentException.class);
	}

	private void verifyColor(double colorShiftForASingleAnimationTick) {
		Color darkerRed = new Color((int) (255.0d * colorShiftForASingleAnimationTick), 0, 0);
		assertEquals(darkerRed, animator.shiftColorOnAnimationTick(FAILING_COLOR));
	}

	private void skipTicks(int ticks) {
		for (int i = 0; i < ticks; i++) {
			animator.tick();
		}
	}

	@Test
	void shouldStayConstantColorWhenNotAngry() {
		animator.setAngerLevel(0);
		verifyColorDeltaForAnimationTicks(0, 1.0);
		verifyColorDeltaForAnimationTicks(1, 1.0);
		verifyColorDeltaForAnimationTicks(1, 1.0);
		assertEquals(Color.RED, animator.shiftColorOnAnimationTick(Color.RED));
	}

	@Test
	void shouldSlowlyFlashInAngerLevelOne() {
		animator.setAngerLevel(1);
		verifyColorDeltaForAnimationTicks(0, 1.0);
		verifyColorDeltaForAnimationTicks(1, 0.99);
		verifyColorDeltaForAnimationTicks(1, 0.98);
		verifyColorDeltaForAnimationTicks(8, 0.90);
		verifyColorDeltaForAnimationTicks(5, 0.95);
		verifyColorDeltaForAnimationTicks(5, 1.0);
	}

	@Test
	void shouldFlashFasterAndDarkerInAngerLevelFive() {
		animator.setAngerLevel(5);
		verifyColorDeltaForAnimationTicks(0, 1.0);
		verifyColorDeltaForAnimationTicks(3, 0.75);
		verifyColorDeltaForAnimationTicks(3, 0.50);
		verifyColorDeltaForAnimationTicks(3, 0.75);
		verifyColorDeltaForAnimationTicks(3, 1.0);
	}

	@Test
	void shouldBlinkToBlackEveryOtherTickInAngerLevelTen() {
		animator.setAngerLevel(ColorAnimator.getMaxAngerLevel());
		verifyColorDeltaForAnimationTicks(0, 1.0);
		verifyColorDeltaForAnimationTicks(1, 0.0);
		verifyColorDeltaForAnimationTicks(1, 1.0);
		verifyColorDeltaForAnimationTicks(1, 0.0);
	}
}
