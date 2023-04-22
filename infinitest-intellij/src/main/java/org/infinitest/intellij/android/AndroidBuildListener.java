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
package org.infinitest.intellij.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;

import org.infinitest.intellij.IntelliJCompilationListener;
import org.infinitest.util.InfinitestUtils;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

public class AndroidBuildListener extends IntelliJCompilationListener implements InvocationHandler {
	private final Project project;

	/**
	 * @param project Injected by the platform
	 */
	public AndroidBuildListener(Project project) {
		this.project = project;
		
		try {
			initialize();
		} catch (Exception e) {
			InfinitestUtils.log(Level.SEVERE, e + " " + e.getMessage());
		}
	}

	public void initialize() throws Exception {
		Class serviceClass = Class.forName("com.android.tools.idea.projectsystem.ProjectSystemService");
		Class listenerClass = Class.forName("com.android.tools.idea.projectsystem.ProjectSystemBuildManager$BuildListener");
		
		Object listener = Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class[] {listenerClass}, this);
		
		Object projectSystemService = project.getService(serviceClass);
		Object androidProjectSystem = serviceClass.getDeclaredMethod("getProjectSystem").invoke(projectSystemService);
		Object projectSystemBuildManager = androidProjectSystem.getClass().getMethod("getBuildManager").invoke(androidProjectSystem);
		
		projectSystemBuildManager.getClass().getDeclaredMethod("addBuildListener", Disposable.class, listenerClass).invoke(projectSystemBuildManager, project, listener);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if ("buildCompleted".equals(method.getName())) {
			doRunTests(project);
		}
		
		return null;
	}
}
