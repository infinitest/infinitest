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
package org.infinitest.testrunner;

import java.io.*;

/**
 * Holds temporal statistics about test method execution.
 */
public class MethodStats implements Serializable {
	private static final long serialVersionUID = -8853619641593524214L;

	private long startTime;
	private long stopTime;
	public final String methodName;

	public MethodStats(String methodName) {
		this.methodName = methodName;
	}
	
	public void start(long timestamp) {
		this.startTime = timestamp;
	}
	
	public long startTime() {
		return startTime;
	}
	
	public void stop(long timestamp) {
		this.stopTime = timestamp;
	}
	
	public long stopTime() {
		return stopTime;
	}

	public long duration() {
		return stopTime - startTime;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MethodStats{");
		sb.append("startTime=").append(startTime);
		sb.append(", stopTime=").append(stopTime);
		sb.append(", methodName='").append(methodName).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
