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
import static org.infinitest.util.InfinitestTestUtils.createBackup;
import static org.infinitest.util.InfinitestTestUtils.getFileForClass;
import static org.infinitest.util.InfinitestTestUtils.restoreFromBackup;
import static org.infinitest.util.InfinitestUtils.setify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Set;

import org.infinitest.environment.FakeEnvironments;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.TestAlmostNotATest;

import javassist.ClassPool;
import javassist.CtClass;

class WhenClassesChange extends DependencyGraphTestBase {
  private File backup;

  @BeforeEach
  void inContext() throws Exception {
    backup = createBackup(TestAlmostNotATest.class.getName());
  }

  @AfterEach
  void cleanupContext() {
    restoreFromBackup(backup);
  }

  @Test
  void shouldRecognizeWhenTestsAreChangedToRegularClasses() throws Exception {
    Class<?> testClass = TestAlmostNotATest.class;
    JavaClass javaClass = runTest(testClass);
    assertTrue(javaClass.isATest(), "Inital state is incorrect");

    untestify();
    updateGraphWithChangedClass(testClass);

    assertThat(getGraph().getCurrentTests()).isEmpty();

    javaClass = getGraph().findJavaClass(testClass.getName());
    assertFalse(javaClass.isATest(), "Class was not reloaded");
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

  private void untestify() throws Exception {
    ClassPool pool = ClassPool.getDefault();
    CtClass cc = pool.makeClass(TestAlmostNotATest.class.getName());
    cc.setSuperclass(pool.get(Object.class.getName()));
    cc.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
  }
}
