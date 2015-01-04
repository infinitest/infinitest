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
package org.infinitest.util;

import static java.lang.Thread.*;
import static org.infinitest.util.FakeEnvironments.*;

import java.io.*;
import java.util.*;

import org.infinitest.testrunner.*;

import com.google.common.io.*;

public abstract class InfinitestTestUtils {
	private static final String BACKUP_EXT = ".infinitest_bak";

	public static File getFileForClass(Class<?> clazz) {
		return getFileForClass(clazz.getName());
	}

	public static File getFileForClass(String className) {
		for (File file : fakeBuildPaths()) {
			File fileForClass = getFileForClass(file, className);
			if (fileForClass.exists()) {
				return fileForClass;
			}
		}
		throw new IllegalArgumentException(className + " does not exist");
	}

	public static File createBackup(String className) throws Exception {
		File originalFile = getFileForClass(className);
		File backupFile = new File(originalFile.getAbsolutePath() + BACKUP_EXT);
		InfinitestTestUtils.copyFile(originalFile, backupFile);
		return backupFile;
	}

	public static void restoreFromBackup(File backup) {
		File originalFile = new File(backup.getAbsolutePath().replace(BACKUP_EXT, ""));
		String fileName = originalFile.getAbsolutePath();
		originalFile.delete();
		if (!backup.renameTo(new File(fileName))) {
			throw new IllegalStateException(originalFile + " could not be restored");
		}
	}

	public static File getFileForClass(File baseDir, String classname) {
		return new File(baseDir, classname.replace(".", "/") + ".class");
	}

	public static void copyFile(File in, File out) throws Exception {
		if (!out.exists()) {
			out.createNewFile();
		}

		if (!in.exists()) {
			throw new IllegalArgumentException(in + " does not exist");
		}

		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}

	public static List<String> emptyStringList() {
		return new ArrayList<String>();
	}

	public static boolean testIsBeingRunFromInfinitest() {
		StackTraceElement[] currentStack = currentThread().getStackTrace();
		List<String> classNames = InfinitestUtils.getClassNames(currentStack);
		return classNames.contains(InProcessRunner.class.getName()) || classNames.contains(JUnit4Runner.class.getName());
	}

	@SuppressWarnings("serial")
	public static Throwable throwableWithStack(final StackTraceElement... stack) {
		return new Throwable() {
			@Override
			public StackTraceElement[] getStackTrace() {
				return stack;
			}
		};
	}

	public static InputStream toInputStream(String text) {
		return new ByteArrayInputStream(text.getBytes());
	}

	public static String toString(InputStream stream) throws IOException {
		return new String(ByteStreams.toByteArray(stream));
	}
}
