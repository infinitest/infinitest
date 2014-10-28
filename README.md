Notes for new developers
=========================

We do TDD here. If you're not comfortable with TDD, find something else to work on. Thanks.

- Code Style
- FIXME comments
- DEBT comments

Get the source
--------------

Fork the project source code on [github](https://github.com/infinitest/infinitest):

	git clone git://github.com/infinitest/infinitest.git

You can pull only a shallow clone by specifying the number of commits you want with `--depth`:

	git clone git://github.com/infinitest/infinitest.git --depth 10

Build
-----

You need [Maven](http://maven.apache.org/download.html). To run a reactor build in the repository root: 

	mvn clean install

This will compile, test and package all modules of the project in a single operation. 

Finding your way around
------------------------

`infinitest-lib` and `infinitest-runner` are included in every plugin. `infinitest-intellij` is the IntelliJ plugin, while `infinitest-eclipse` is the eclipse plugin.

`testWorkspace` is a collection of sample projects that we use for exploratory testing. If you find something in the wild that makes Infinitest break, it's a good idea to reproduce it in testWorkspace, fix it, and then try it again to be sure you've really fixed it.

Publishing a release
--------------------

When you finish something, release it. Also, make sure you don't push anything to github that's not in a releasable state. It's OK to temporarily disable stuff...just make sure that work in progress isn't breaking anything else.

The `release.sh` script will build a new version of the plugin, incrementing the version number, and copy it to the server hosting the update site. This copy will fail if Ben hasn't added your public ssh key to the server (See above). We like developers to publish a release after implementing every feature.

Running `release.sh` copies the plugin to the experimental [update site](http://update.improvingworks.com/experimental). It's suggested that you publish and then test the plugin before promoting it to the main [update site](http://update.improvingworks.com). You do this using the `promote.sh` script.

Open Infinitest in Eclipse
--------------------------------------------

First, tell maven to generate eclipse project files (.project and .classpath):

	mvn eclipse:eclipse

Right-click in the Eclipse Package Explorer -> Import -> Existing Projects info Workspace -> Choose the root of your infinitest-repository -> Your maven-built-projects are displayed -> DON'T copy into workspace -> Finish.
The "Eclipse Application" Runtime Configuration in eclipse allows you to fire up another Eclipse instance with the Infinitest plugin loaded into it. You can also debug this instance (which can be a very effective way to explore what the Eclipse SDK actually does): 

Running and Debugging Infinitest in Eclipse
--------------------------------------------

	mvn eclipse:eclipse install -Dmaven.test.skip=true

Right-click plugin.xml -> Open With -> Plug-in Manifest Editor
Tab Overview -> Launch/Debug an Eclipse application

Keep in mind that while you can use Infinitest to test Infinitest, some weird things happen when you change anything in the `infinitest-runner` project, because that project is actually used to run the tests, so any changes you make may conflict with the classes in whatever version of the plugin you have installed. As a result, we try to keep `infinitest-runner` as thin as we can get it.

License
-------

Infinitest is distributed under [MIT License](http://opensource.org/licenses/MIT).

Want to participate?
--------------------

You might want to read [this](https://github.com/infinitest/infinitest/wiki/Want-to-participate%3F)

Credits
-------

[![Yourkit](http://infinitest.github.io/assets/img/yourkit.png)](http://www.yourkit.com/java/profiler/index.jsp)
is kindly supporting Infinitest open source project with its full-featured Java Profiler.
