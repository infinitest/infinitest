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

import static com.google.common.base.Joiner.*;
import static com.google.common.collect.Iterables.*;
import static java.io.File.*;
import static java.util.Arrays.*;
import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.*;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class InfinitestUtils {
	private static List<LoggingListener> loggingListeners = new ArrayList<LoggingListener>();

	private static final String LINE_SEP = "\n";
	private static final int MAX_LINE_COUNT = 50;

	/**
	 * Takes an elapsed time (in milliseconds) and turns it into a stopwatch
	 * style time
	 */
	public static String formatTime(long time) {
		DateFormat dateFmt = new SimpleDateFormat("HH:mm:ss");
		dateFmt.setTimeZone(TimeZone.getTimeZone("GMT-0"));
		return dateFmt.format(new Date(time));
	}

	@SafeVarargs
	public static <T> Set<T> setify(T... items) {
		Set<T> set = new HashSet<>();
		set.addAll(asList(items));
		return set;
	}

	public static String stripPackageName(String className) {
		if (!className.contains(".")) {
			return className;
		}
		return className.substring(className.lastIndexOf('.') + 1);
	}

	public static String getResourceName(String classname) {
		return classname.replace('.', '/') + ".class";
	}

	public static void log(Level level, String logMsg) {
		fireLoggingEvent(level, logMsg);
	}

	public static void log(String logMsg) {
		log(getLogLevel(), logMsg);
	}

	public static void log(String message, Throwable e) {
		fireLoggingEvent(message, e);
	}

	private static void fireLoggingEvent(String message, Throwable throwable) {
		for (LoggingListener each : loggingListeners) {
			each.logError(message, throwable);
		}
	}

	private static void fireLoggingEvent(Level level, String logMsg) {
		if (level.intValue() >= InfinitestGlobalSettings.getLogLevel().intValue()) {
			for (LoggingListener each : loggingListeners) {
				each.logMessage(level, logMsg);
			}
		}
	}

	public static List<String> getClassNames(StackTraceElement[] currentStack) {
		ArrayList<String> list = new ArrayList<String>();
		for (StackTraceElement element : currentStack) {
			list.add(element.getClassName());
		}
		return list;
	}

	static String getExceptionMessage(Throwable e) {
		if (e.getMessage() == null) {
			return e.getClass().getName();
		}

		String msg = e.getMessage().replace(":", ":\n");
		if (msg.length() > 50) {
			return msg.substring(0, 50) + "...";
		}

		return msg;
	}

	private static String getClassFile(Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".class";
	}

	public static String findClasspathEntryFor(String systemClasspath, Class<?> clazz) {
		String classToLookFor = getClassFile(clazz);
		List<String> classpath = asList(systemClasspath.split(pathSeparator));
		for (String each : classpath) {
			if (isDirectory(each)) {
				if (fileExists(each + separatorChar + classToLookFor)) {
					return convertFromWindowsClassPath(each);
				}
			} else {
				JarFile jarFile = null;
				try {
					jarFile = new JarFile(each);
					if (jarFile.getJarEntry(classToLookFor) != null) {
						return convertFromWindowsClassPath(each);
					}
				} catch (IOException e) {
					log(WARNING, "Error reading jar file " + each + ": " + e.getMessage());
				} finally {
					if (jarFile != null) {
						try {
							jarFile.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
		}
		return null;
	}

	public static String convertFromWindowsClassPath(String path) {
		return path.replace("\\", "/");
	}

	private static boolean fileExists(String filename) {
		return new File(filename).exists();
	}

	private static boolean isDirectory(String filename) {
		return new File(filename).isDirectory();
	}

	public static void addLoggingListener(LoggingListener listener) {
		loggingListeners.add(listener);
	}

	public static String listToMultilineString(Collection<?> listOfStringableObjects) {
		StringBuilder trace = new StringBuilder();

		Iterable<?> selectedItems = limit(listOfStringableObjects, MAX_LINE_COUNT);
		trace.append(on(LINE_SEP).join(selectedItems));
		if (listOfStringableObjects.size() > MAX_LINE_COUNT) {
			trace.append(LINE_SEP);
			trace.append((listOfStringableObjects.size() - MAX_LINE_COUNT) + " more...");
		}
		return trace.toString();
	}
}
