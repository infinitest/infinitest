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
package org.infinitest.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javassist.*;

import org.infinitest.util.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenClassesChange extends DependencyGraphTestBase {
  private File backup;

  @Before
  public void inContext() throws Exception {
    backup = createBackup(TestAlmostNotATest.class.getName());
  }

  @After
  public void cleanupContext() {
    restoreFromBackup(backup);
  }

  @Test
  public void shouldRecognizeWhenTestsAreChangedToRegularClasses() throws Exception {
    Class<?> testClass = TestAlmostNotATest.class;
    JavaClass javaClass = runTest(testClass);
    assertTrue("Inital state is incorrect", javaClass.isATest());

    untestify(TestAlmostNotATest.class, "testANothing", "()V");
    updateGraphWithChangedClass(testClass);

    assertThat(getGraph().getCurrentTests()).isEmpty();

    javaClass = getGraph().findJavaClass(testClass.getName());
    assertFalse("Class was not reloaded", javaClass.isATest());
  }

  private JavaClass runTest(Class<?> testClass) {
    Set<JavaClass> classes = updateGraphWithChangedClass(testClass);
    assertEquals(1, classes.size());
    return classes.iterator().next();
  }

  private Set<JavaClass> updateGraphWithChangedClass(Class<?> testClass) {
    File classFile = getFileForClass(testClass);
    return getGraph().findTestsToRun(setify(classFile));
  }

  private void untestify(final Class testClass, final String name, final String desc) throws Exception {
    ClassPool pool = ClassPool.getDefault();
    CtClass cc = pool.getCtClass(testClass.getName());
    cc.getMethod(name, desc).getMethodInfo().getAttributes().clear();
    cc.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
  }
}
