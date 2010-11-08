Notes for new developers
=========================

We do TDD here. If you're not comfortable with TDD, find something else to work on. Thanks.

Getting Started
---------------

You need [maven](http://maven.apache.org/download.html). Did I mention that?

Finding your way around
------------------------

`infinitest-core` and `infinitest-runner` are included in every plugin. `infinitest-intellij` is the IntelliJ plugin, while `infinitest-eclipse` is a multi-module project that contains 3 sub-projects necessary for the eclipse plugin. `eclipse-feature` and `eclipse-site` just include metadata necessary for creating and packaging the plugin.

`testWorkspace` is a collection of sample projects that we use for exploratory testing. If you find something in the wild that makes Infinitest break, it's a good idea to reproduce it in testWorkspace, fix it, and then try it again to be sure you've really fixed it.

Doing a release
----------------

When you finish something, release it. Also, make sure you don't push anything to github that's not in a releasable state. It's OK to temporarily disable stuff...just make sure that work in progress isn't breaking anything else.

The `publish.sh` script will build a new version of the plugin, incrementing the version number, and copy it to the server hosting the update site. This copy will fail if Ben hasn't added your public ssh key to the server (See above). We like developers to do a release after implementing every feature.

Running `publish.sh` copies the plugin to the experimental update site (http://update.improvingworks.com/experimental). It's suggested that you publish and then test the plugin before promoting it to the main update site (http://update.improvingworks.com). You do this using the `promote.sh` script.

Running and Debugging Infinitest in Eclipse
--------------------------------------------

The "Eclipse Application" Runtime Configuration in eclipse allows you to fire up another Eclipse instance with the Infinitest plugin loaded into it. You can also debug this instance (which can be a very effective way to explore what the Eclipse SDK actually does). 

Keep in mind that while you can use Infinitest to test Infinitest, some weird things happen when you change anything in the `infinitest-runner` project, because that project is actually used to run the tests, so any changes you make may conflict with the classes in whatever version of the plugin you have installed. As a result, we try to keep `infinitest-runner` as thin as we can get it.

