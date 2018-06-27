# Contributing

We strongly believe in Test-Driven Development.

## Get the source

Fork the project source code on [github](https://github.com/infinitest/infinitest):

	git clone git://github.com/infinitest/infinitest.git

You can pull only a shallow clone by specifying the number of commits you want with `--depth`:

	git clone git://github.com/infinitest/infinitest.git --depth 10

## Build

You need [Maven](http://maven.apache.org/download.html). To run a reactor build in the repository root: 

	mvn clean install

This will compile, test and package all modules of the project in a single operation. 

**TODO** Describe where resulting artifacts are found.

## Finding your way around


`infinitest-intellij` is the IntelliJ platform plugin, while `infinitest-eclipse` is the Eclipse plugin.

Both plugins embed `infinitest-lib`, `infinitest-runner` and `infinitest-classloader`. 

To run tests `infinitest-lib` starts a dedicated `ìnfinitest-runner` process and communicates with it using a socket (see  NativeConnectionFactory).

`infinitest-classloader` is used as System classloader for `infinitest-runner` process to read classpath from a config file
 instead of reading it from environment/arguments which are too limited in length to allow arbitrary large classpaths.
 
`testWorkspace` is a collection of sample projects that we use for exploratory testing. If you find something in the wild that makes Infinitest break, it's a good idea to reproduce it in testWorkspace, fix it, and then try it again to be sure you've really fixed it.


# Setup and debug in in Eclipse

## Install Eclipse

* Install “Eclipse IDE for Eclipse Committers”
* Create a workspace
* Add infinitest git repository to workspace
* Import all projects from git using default options
* Eclipse proposes to install maven plugins connector for *maven-bundle-plugin*, *maven-dependency-plugin*, Click auto-select and Accept
* run `mvn clean install` from command line
* Right click project infinitest-eclipse > Maven > Update project and update project

If you have Error “AutoValue_InfinitestConfiguration cannot be resolved to a type”
* Install https://marketplace.eclipse.org/content/m2e-apt from market place
* In Preferences > Maven > Annotation processor
  * In Select Annotation Processing mode 
  * Choose Automatically configure JDT APT


## Running and Debugging Infinitest in Eclipse

After some changes
* Run `mvn clean install -Dmaven.test.skip=true`
* Right-click plugin.xml -> Open With -> Plug-in Manifest Editor Tab Overview -> Launch/Debug an Eclipse application

Keep in mind that while you can use Infinitest to test Infinitest, some weird things happen when you change anything in the `infinitest-runner` project, because that project is actually used to run the tests, so any changes you make may conflict with the classes in whatever version of the plugin you have installed. As a result, we try to keep `infinitest-runner` as thin as we can get it.

# Setup and debug in IntelliJ

**TODO** 

# Maintainers Area

## Publishing a release 

**This is obsoloete**

When you finish something, release it. Also, make sure you don't push anything to github that's not in a releasable state. It's OK to temporarily disable stuff...just make sure that work in progress isn't breaking anything else.

The `release.sh` script will build a new version of the plugin, incrementing the version number, and copy it to the server hosting the update site. This copy will fail if Ben hasn't added your public ssh key to the server (See above). We like developers to publish a release after implementing every feature.

Running `release.sh` copies the plugin to the experimental [update site](http://update.improvingworks.com/experimental). It's suggested that you publish and then test the plugin before promoting it to the main [update site](http://update.improvingworks.com). You do this using the `promote.sh` script.