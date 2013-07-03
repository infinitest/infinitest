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
package org.infinitest.eclipse.resolution;

import static org.fest.assertions.Assertions.assertThat;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.internal.resources.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.*;
import org.junit.*;

public class WhenGeneratingQuickFixesForErrorMarkers {
  private MarkerResolutionGenerator generator;
  private IMarker marker;

  @Before
  public void setUp() {
    generator = new MarkerResolutionGenerator();
    marker = mock(IMarker.class);
  }

  @Test
  public void shouldIncludePrintStackTraceIfMarkerHasAStackTrace() throws Exception {
    when(marker.getAttribute(TEST_NAME_ATTRIBUTE)).thenReturn("TestName");
    when(marker.getAttribute(METHOD_NAME_ATTRIBUTE)).thenReturn("methodName");

    assertTrue(generator.hasResolutions(marker));
    IMarkerResolution resolution = generator.getResolutions(marker)[0];

    assertThat(resolution).isInstanceOf(ErrorViewerResolution.class);
  }

  @Test
  public void shouldNotAddResolutionsToMarkersWithNoStackTrace() throws Exception {
    IMarker marker = mock(IMarker.class);
    when(marker.getAttribute(TEST_NAME_ATTRIBUTE)).thenReturn(null);

    assertFalse(generator.hasResolutions(marker));
    assertEquals(0, generator.getResolutions(marker).length);
  }

  @Test
  public void shouldNotHaveResolutionIfMarkerThrowsResourceException() throws CoreException {
    IMarker marker = mock(IMarker.class);
    ResourceException resourceException = new ResourceException(mock(IStatus.class));
    when(marker.getAttribute(TEST_NAME_ATTRIBUTE)).thenThrow(resourceException);

    assertFalse(generator.hasResolutions(marker));
  }
}
