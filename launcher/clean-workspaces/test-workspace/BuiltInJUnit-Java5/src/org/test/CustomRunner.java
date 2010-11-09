package org.test;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

// TODO Changing this class should cause the CustomRunnerTest to run
// FIXME But it doesn't :-(
public class CustomRunner extends Runner {

	public CustomRunner(Class<?> clazz) {
	}

	@Override
	public Description getDescription() {
		return null;
	}

	@Override
	public void run(RunNotifier notifier) {
	}
}
