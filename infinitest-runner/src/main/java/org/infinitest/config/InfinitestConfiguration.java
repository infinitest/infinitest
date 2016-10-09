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
package org.infinitest.config;

import java.util.Collection;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@AutoValue
public abstract class InfinitestConfiguration {

	public abstract ImmutableSet<String> includedPatterns();

	public abstract ImmutableSet<String> excludedPatterns();

	public abstract ImmutableSet<String> includedGroups();

	public abstract ImmutableSet<String> excludedGroups();

	public abstract ImmutableSet<String> testngListeners();

	@AutoValue.Builder
	public static abstract class Builder {

		public abstract Builder includedPatterns(Collection<String> includedPatterns);
		public abstract Builder includedPatterns(String... includedPatterns);

		public abstract Builder excludedPatterns(Collection<String> excludedPatterns);
		public abstract Builder excludedPatterns(String... excludedPatterns);
		
		public abstract Builder includedGroups(Collection<String> includedGroups);
		public abstract Builder includedGroups(String... includedGroups);

		public abstract Builder excludedGroups(Collection<String> excludedGroups);
		public abstract Builder excludedGroups(String... excludedGroups);

		public abstract Builder testngListeners(Collection<String> testngListeners);
		public abstract Builder testngListeners(String... testngListeners);

		public abstract InfinitestConfiguration build();
	}

	public static Builder builder() {
		return new AutoValue_InfinitestConfiguration.Builder()
				.excludedPatterns(ImmutableList.<String>of())
				.includedPatterns(ImmutableList.<String>of())
				.includedGroups(ImmutableList.<String>of())
				.excludedGroups(ImmutableList.<String>of())
				.testngListeners(ImmutableList.<String>of());
	}
	
	public static InfinitestConfiguration empty() {
		return builder().build();
	}

	public abstract Builder toBuilder();
}
