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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.CharSource;

class InfinitestConfigurationParserTest {

	private InfinitestConfigurationParser parser;

	@BeforeEach
	void before() {
		parser = new InfinitestConfigurationParser();
	}

	@Test
	void shouldRecognizeIncludeLines() throws IOException {
		InfinitestConfiguration actual = parser
				.parseFileContent(CharSource.wrap("include com\\.y\\.\\.*, com\\.x\\.\\.*"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder()
				.includedPatterns("com\\.y\\.\\.*", "com\\.x\\.\\.*").build();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldRecognizeExcludeLines() throws IOException {
		InfinitestConfiguration actual = parser
				.parseFileContent(CharSource.wrap("exclude com\\.y\\.\\.*, com\\.x\\.\\.*"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder()
				.excludedPatterns("com\\.y\\.\\.*", "com\\.x\\.\\.*").build();
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldRecognizeIncludeGroupLines() throws IOException {
		InfinitestConfiguration actual = parser.parseFileContent(CharSource.wrap("includeGroups g1, g2"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder().includedGroups("g1", "g2").build();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldRecognizeExcludeGroupLines() throws IOException {
		InfinitestConfiguration actual = parser.parseFileContent(CharSource.wrap("excludeGroups g1, g2"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder().excludedGroups("g1", "g2").build();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldRecognizeTestngListenersLines() throws IOException {
		InfinitestConfiguration actual = parser.parseFileContent(
				CharSource.wrap("testngListeners com.myproject.MyTestNGListener1, com.myproject.MyTestNGListener2"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder()
				.testngListeners("com.myproject.MyTestNGListener1", "com.myproject.MyTestNGListener2").build();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldIgnoreCommentedLines() throws IOException {
		InfinitestConfiguration actual = parser
				.parseFileContent(CharSource.wrap("# include x\n! include y\ninclude z"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder().includedPatterns("z").build();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldIgnoreBlankLines() throws IOException {
		InfinitestConfiguration actual = parser.parseFileContent(CharSource.wrap("\n  \n\t\n"));
		InfinitestConfiguration expected = InfinitestConfiguration.builder().build();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldParseLegacySyntax() throws IOException {
		InfinitestConfiguration actual = parser.parseFileContent(CharSource.wrap(
				"#Tests that end in ITest:\n" +
						".*ITest\n" +
						"\n" +
						"# Inner Classes:\n" +
						".*\\$.*\n" +
						"\n" +
						"# Tests in a certain package:\n" +
						"com\\.mycompany\\.mypackage\\..*\n" +
						"\n" +
						"# All tests in this project:\n" +
						".*\n" +
						"## TestNG Configuration\n" +
						"# Tests with a group-annotation in excluded-groups are not executed:\n" +
						"## excluded-groups=manual,slow\n" +
						"## TestNG Configuration\n" +
						"# Tests with a group-annotation in groups are the only ones being executed (overridden by excluded-groups):\n"
						+
						"## groups=shouldbetested\n" +
						"## listeners=com.myproject.MyTestNGListener1, com.myproject.MyTestNGListener2"));

		InfinitestConfiguration expected = InfinitestConfiguration.builder()
				.excludedPatterns(".*ITest", ".*\\$.*", "com\\.mycompany\\.mypackage\\..*", ".*")
				.excludedGroups("manual", "slow")
				.includedGroups("shouldbetested")
				.testngListeners("com.myproject.MyTestNGListener1", "com.myproject.MyTestNGListener2")
				.build();

		assertThat(actual).isEqualTo(expected);

	}
}
