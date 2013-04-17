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
package org.infinitest;

import static com.google.common.collect.Maps.*;
import static java.lang.System.*;
import static java.lang.reflect.Proxy.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.lang.reflect.*;
import java.util.*;

import org.infinitest.testrunner.*;

// Refactoring attempts to date: 4
//
// DEBT This class is totally insane. One of these days, I'm going to find a way to do this better.
class EventNormalizer {
	private final EventQueue eventQueue;

	// The proxy cache is a suck. Boo, Java.
	//
	// Because there's no way to detect a proxy object when doing an equality
	// comparison,
	// and because even if you could detect it, there's no way to get the
	// decorated listener,
	// it's impossible to compare decorated listeners when (for example)
	// removing them from a
	// listener
	// list.
	//
	// To address this, we map all the decorated listeners to their proxies, and
	// always return
	// the same proxy for a given listener. Then we can fall back on regular
	// object equality.
	private final Map<Pair, Object> proxyCache;

	// preloaded Method objects for the methods in java.lang.Object
	private static Method hashCodeMethod;
	private static Method equalsMethod;

	static {
		try {
			hashCodeMethod = Object.class.getMethod("hashCode");
			equalsMethod = Object.class.getMethod("equals", Object.class);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}

	// The pair is necessary because if we simply use the listener as a key,
	// objects that implement
	// multiple interfaces will overwrite the proxy values in the map.
	private static class Pair {
		private final Object listener;
		private final Class<?> clazz;

		public Pair(Class<?> clazz, Object listener) {
			this.clazz = clazz;
			this.listener = listener;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Pair) {
				Pair other = (Pair) obj;
				return other.listener.equals(listener) && other.clazz.equals(clazz);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return clazz.hashCode() ^ listener.hashCode();
		}
	}

	public EventNormalizer(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
		proxyCache = newHashMap();
	}

	public TestQueueListener testQueueNormalizer(TestQueueListener listener) {
		return createProxy(listener, TestQueueListener.class);
	}

	public TestResultsListener testEventNormalizer(TestResultsListener listener) {
		return createProxy(listener, TestResultsListener.class);
	}

	public ConsoleOutputListener consoleEventNormalizer(ConsoleOutputListener listener) {
		return createProxy(listener, ConsoleOutputListener.class);
	}

	private <T> T createProxy(T listener, Class<T> proxyInterface) {
		Pair key = new Pair(proxyInterface, listener);
		if (!proxyCache.containsKey(key)) {
			ClassLoader classLoader = getClass().getClassLoader();
			Class<?>[] interfaces = { proxyInterface };
			proxyCache.put(key, newProxyInstance(classLoader, interfaces, createHandler(listener)));
		}
		return (T) proxyCache.get(key);
	}

	private <T> InvocationHandler createHandler(final T listener) {
		return new InvocationHandler() {
			public Object invoke(Object proxy, final Method method, final Object[] args) {
				if (method.equals(hashCodeMethod)) {
					return proxyHashCode(proxy);
				} else if (method.equals(equalsMethod)) {
					return proxyEquals(proxy, args[0]);
				}

				eventQueue.pushNamed(new NamedRunnable("Processing Results") {
					public void run() {
						try {
							method.invoke(listener, args);
						} catch (IllegalAccessException e) {
							log("Illegal Access in event normalizer", e);
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							log("Error in event normalizer", e);
							throw new RuntimeException(e);
						}
					}
				});

				return null;
			}
		};
	}

	private Integer proxyHashCode(Object proxy) {
		return new Integer(identityHashCode(proxy));
	}

	private Boolean proxyEquals(Object proxy, Object other) {
		return proxy == other;
	}
}
