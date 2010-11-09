package org.test;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;


public class ProxyInvokeTest implements InvocationHandler {
	@Test
	public void shouldCorrectlyPlaceMarkersWhenPofIsHardToFind() {
		// TODO Flipping these fail() calls back and forth can create some interesting situations
		//fail();
		Runnable proxyRunnable = (Runnable) newProxyInstance(getClass().getClassLoader(), new Class[]{Runnable.class}, this);
		proxyRunnable.run();
	}

	public Object invoke(Object proxy, Method method, Object[] args) {
		//fail();
		return null;
	}
}
