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
package org.infinitest.intellij.idea;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.openapi.project.Project;

/**
 * ProgramParametersConfigurator.getWorkingDir only calls getWorkingDirectory
 * The interface is implemented but it does not seem to be necessary in the current version of IDEA
 * 
 * In a future version it might be good to actually implement a run configuration and let the user configure the
 * runner from there
 */
public class InfinitestRunConfigurationParameters implements CommonProgramRunConfigurationParameters {
	private String programParameters;
	private String workingDirectory;
	private Map<String, String> envs;
	private boolean passParentEnvs;
	
	@Override
	public Project getProject() {
		return null;
	}

	@Override
	public void setProgramParameters(@Nullable String value) {
		this.programParameters = value;
	}

	@Override
	public @Nullable String getProgramParameters() {
		return programParameters;
	}

	@Override
	public void setWorkingDirectory(@Nullable String value) {
		this.workingDirectory = value;
	}

	@Override
	public @Nullable String getWorkingDirectory() {
		return workingDirectory;
	}

	@Override
	public void setEnvs(@NotNull Map<String, String> envs) {
		this.envs = envs;
	}

	@Override
	public @NotNull Map<String, String> getEnvs() {
		return envs;
	}

	@Override
	public void setPassParentEnvs(boolean passParentEnvs) {
		this.passParentEnvs = passParentEnvs;
	}

	@Override
	public boolean isPassParentEnvs() {
		return passParentEnvs;
	}
}
