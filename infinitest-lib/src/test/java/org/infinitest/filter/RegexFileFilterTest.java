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
package org.infinitest.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.InfinitestConfigurationParser;
import org.infinitest.config.InfinitestConfigurationSource;
import org.infinitest.config.MemoryInfinitestConfigurationSource;
import org.infinitest.parser.JavaClass;
import org.junit.jupiter.api.Test;

import com.google.common.base.Joiner;
import com.google.common.io.CharSource;

class RegexFileFilterTest {

	@Test
	void testFilterWithoutPatterns() throws Exception {

		InfinitestConfigurationSource configSource = configSource(InfinitestConfiguration.builder().build());
		RegexFileFilter filter = new RegexFileFilter(configSource);

		assertThat(filter.match(mockClass("SomeTest"))).isFalse();
	}

	@Test
	void matchShouldReturnTrueWhenClassMatchesAnExcludedPattern() throws Exception {

		InfinitestConfigurationSource configSource = configSource(excluding("SomeTest"));
		RegexFileFilter filter = new RegexFileFilter(configSource);

		assertThat(filter.match(mockClass("SomeTest"))).isTrue();
	}

	@Test
	void matchShouldReturnFalseWhenClassDoesntMatchTheExcludedPattern() throws Exception {
		InfinitestConfigurationSource configSource = configSource(
				InfinitestConfiguration.builder().excludedPatterns("SomeTest").build());
		RegexFileFilter filter = new RegexFileFilter(configSource);

		assertThat(filter.match(mockClass("OtherTest"))).isFalse();
	}

	@Test
	void testFilterContentContainsSimpleIncludesEntry() throws Exception {

		InfinitestConfigurationSource configSource = configSource(
				InfinitestConfiguration.builder().includedPatterns("SomeTest").build());
		RegexFileFilter filter = new RegexFileFilter(configSource);

		assertThat(filter.match(mockClass("SomeTest"))).isFalse();
		assertThat(filter.match(mockClass("OtherTest"))).isTrue();
	}

	@Test
	void testFilterContentContainsIncludesExcludesEntries() throws Exception {

		InfinitestConfigurationSource configSource = configSource(
				InfinitestConfiguration.builder().includedPatterns("SomeTest").excludedPatterns(".*Test").build());
		RegexFileFilter filter = new RegexFileFilter(configSource);

		assertThat(filter.match(mockClass("SomeTest"))).isTrue();
		assertThat(filter.match(mockClass("OtherTest"))).isTrue();
		assertThat(filter.match(mockClass("FooTest"))).isTrue();

	}

	@Test
	void testFilterContentContainsComplexEntries() throws Exception {

		InfinitestConfigurationSource configSource = configSource(
				InfinitestConfiguration.builder()
				.includedPatterns(
						"com\\.mycompany\\.mypackage1\\..*",
						"com\\.mycompany\\.mypackage2\\..*"
						)
				.excludedPatterns(
						".*IT",
						".*SlowTest"
						).build());
		RegexFileFilter filter = new RegexFileFilter(configSource);

		assertThat(filter.match(mockClass("com.mycompany.mypackage1.SomeTest"))).isFalse();
		assertThat(filter.match(mockClass("com.mycompany.mypackage2.SomeTest"))).isFalse();
		assertThat(filter.match(mockClass("com.mycompany.mypackage3.SomeTest"))).isTrue();
		assertThat(filter.match(mockClass("com.mycompany.mypackage1.SomeIT"))).isTrue();
		assertThat(filter.match(mockClass("com.mycompany.mypackage2.OtherIT"))).isTrue();
		assertThat(filter.match(mockClass("com.mycompany.mypackage1.SlowTest"))).isTrue();
		assertThat(filter.match(mockClass("com.mycompany.mypackage2.OtherTest"))).isFalse();
		assertThat(filter.match(mockClass("OtherTest"))).isTrue();
		assertThat(filter.match(mockClass("SomeTest"))).isTrue();
	}

	@Test
	void testExcludeWithLeadingWildcard() {
		TestFilter filter = new RegexFileFilter(configSource(excluding(".*TestFilterList")));

		assertTrue(filter.match(mockClass(org.infinitest.TestFilterList.class)));
	}

	@Test
	void testExcludeWithTrailingWildcard() {
		TestFilter filter = new RegexFileFilter(configSource(excluding("org\\..*")));

		assertTrue(filter.match(mockClass(org.infinitest.TestFilterList.class)));
		assertFalse(filter.match(mockClass(com.fakeco.fakeproduct.TestFakeProduct.class)));
		
	}

	@Test
	void testExcludeSingleTest() {
		TestFilter filter = new RegexFileFilter(configSource(excluding("org\\.infinitest\\.TestFilterList")));

		assertTrue(filter.match(mockClass(org.infinitest.TestFilterList.class)));
		assertFalse(filter.match(mockClass(com.fakeco.fakeproduct.TestFakeProduct.class)));		
	}

	private InfinitestConfigurationSource createConfigSource(String... lines) throws Exception {
		String content = Joiner.on("\n").join(lines);
		InfinitestConfiguration config = new InfinitestConfigurationParser()
				.parseFileContent(CharSource.wrap(content));
		return configSource(config);
	}

	private InfinitestConfiguration excluding(String... excludedPatterns) {
		return InfinitestConfiguration.builder().excludedPatterns(excludedPatterns).build();
	}

	private InfinitestConfigurationSource configSource(InfinitestConfiguration config) {
		return new MemoryInfinitestConfigurationSource(config);
	}

	private JavaClass mockClass(Class<?> classToMatch) {
		return mockClass(classToMatch.getName());
	}

	private JavaClass mockClass(String name) {
		JavaClass javaClass = mock(JavaClass.class);
		when(javaClass.getName()).thenReturn(name);
		return javaClass;
	}

}
