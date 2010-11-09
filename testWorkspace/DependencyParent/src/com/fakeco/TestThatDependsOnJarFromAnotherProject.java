package com.fakeco;

import static org.apache.commons.io.IOUtils.toInputStream;
import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class TestThatDependsOnJarFromAnotherProject {
	@Test
	public void shouldBeAbleToFindCommonsIo() throws IOException {
		assertEquals("Hello", IOUtils.toString(toInputStream("Hello")));
	}
}
