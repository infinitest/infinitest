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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;

/**
 * Parses the content of the infinitest.filters file
 * @author sarod
 *
 */
public class InfinitestConfigurationParser {

	private static final String LEGACY_SUFFIX = "\\s?=\\s?(.+)";
	private static final String LEGACY_PREFIX = "^\\s*#+\\s?";

	private static final Splitter ON_COMMA_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

	private interface LineParser {

		/**
		 * Process the line if relevant
		 * 
		 * @param line
		 * @return true if the line was sucssefully parsed and processed by this
		 *         parser
		 */
		boolean maybeProcessLine(String line);
	}

	private static class PatternLineParser implements LineParser {

		private final Pattern linePattern;
		private final List<String> listToFillWithMatches;

		private PatternLineParser(Pattern linePattern, List<String> listToFillWithMatches) {
			this.listToFillWithMatches = listToFillWithMatches;
			this.linePattern = linePattern;
		}

		@Override
		public boolean maybeProcessLine(String line) {
			Matcher excludeMatcher = linePattern.matcher(line);
			if (excludeMatcher.matches()) {
				String lineContent = excludeMatcher.group(1);
				listToFillWithMatches.addAll(ON_COMMA_SPLITTER.splitToList(lineContent));
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public String toString() {
			return linePattern.pattern();
		}
	}

	private static class CommentLineParser implements LineParser {

		@Override
		public boolean maybeProcessLine(String line) {
			return line.startsWith("!") || line.startsWith("#");
		}

	}

	public InfinitestConfiguration parseFileContent(CharSource textConfiguration) throws IOException {

		List<String> excludedPatterns = new ArrayList<String>();
		List<String> includedPatterns = new ArrayList<String>();
		List<String> includedGroups = new ArrayList<String>();
		List<String> excludedGroups = new ArrayList<String>();
		List<String> testngListeners = new ArrayList<String>();

		// Be careful the order of the parsers below is important as the lousy
		// parsers are put at the end
		ImmutableList<LineParser> orderedLineParsers = ImmutableList.<LineParser> builder()
				.add(new PatternLineParser(linePattern("include"), includedPatterns))
				.add(new PatternLineParser(linePattern("exclude"), excludedPatterns))
				.add(new PatternLineParser(linePattern("includeGroups"), includedGroups))
				.add(new PatternLineParser(linePattern("excludeGroups"), excludedGroups))
				.add(new PatternLineParser(linePattern("testngListeners"), testngListeners))
				
				
				.add(new PatternLineParser(legacySyntaxLinePattern("groups"), includedGroups))
				.add(new PatternLineParser(legacySyntaxLinePattern("excluded-groups"), excludedGroups))
				.add(new PatternLineParser(legacySyntaxLinePattern("listeners"), testngListeners))
				
				.add(new CommentLineParser())
				// Catch all remaining lines as legacy excluded patterns
				.add(new PatternLineParser(Pattern.compile("(.*)"), excludedPatterns))
				.build();

		for (String line : textConfiguration.readLines()) {
			for (LineParser lineParser : orderedLineParsers) {
				if (lineParser.maybeProcessLine(line)) {
					break;
				}
			}
		}
		return InfinitestConfiguration.builder().excludedPatterns(excludedPatterns).includedPatterns(includedPatterns)
				.excludedGroups(excludedGroups).includedGroups(includedGroups).testngListeners(testngListeners).build();
	}

	private static Pattern legacySyntaxLinePattern(String key) {
		return Pattern.compile(LEGACY_PREFIX + Pattern.quote(key) + LEGACY_SUFFIX);

	}

	private static final Pattern linePattern(String key) {
		return Pattern.compile(Pattern.quote(key) + "\\s+(.+)");
	}

}
