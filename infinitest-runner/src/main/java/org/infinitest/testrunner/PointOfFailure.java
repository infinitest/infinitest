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
 * Represents the source code location where a test failed.
 */
public class PointOfFailure implements Serializable {
	private static final long serialVersionUID = -1L;

	private final String className;
	private final int lineNumber;
	private final String errorClassName;
	private final String message;

	public PointOfFailure(String className, int lineNumber, String errorClassName, String message) {
		this.className = removeInnerClasses(className);
		this.lineNumber = lineNumber;
		this.errorClassName = errorClassName;
		this.message = message;
	}

	private String removeInnerClasses(String className) {
		return className.split("\\$")[0];
	}

	public String getMessage() {
		return message;
	}

	public String getClassName() {
		return className;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public int hashCode() {
		int hashCode = className.hashCode() ^ lineNumber ^ errorClassName.hashCode();
		if (message != null) {
			hashCode = hashCode ^ message.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof PointOfFailure)) {
			return false;
		}

		PointOfFailure that = (PointOfFailure) other;
		return className.equals(that.className) && (lineNumber == that.lineNumber) && errorClassName.equals(that.errorClassName) && safeEquals(message, that.message);
	}

	private boolean safeEquals(Object orig, Object other) {
		if ((orig == null) && (other == null)) {
			return true;
		}
		if ((orig == null) || (other == null)) {
			return false;
		}

		return orig.equals(other);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(className).append(':').append(lineNumber).append(" - ").append(errorClassName);
		if (message != null) {
			builder.append('(').append(message).append(')');
		}
		return builder.toString();
	}
}
