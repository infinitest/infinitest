# Contributing

We strongly believe in Test-Driven Development.

## Get the source

Fork the project source code on [github](https://github.com/infinitest/infinitest):

	git clone git://github.com/infinitest/infinitest.git

You can pull only a shallow clone by specifying the number of commits you want with `--depth`:

	git clone git://github.com/infinitest/infinitest.git --depth 10

## Build

Infinitest requires JDK 11 or above.
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


# Setup and debug in Eclipse

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

* run `mvn clean install` to build the project
* From IntelliJ install the plugin from disk (in `File / Settings / Plugins`), look for the .zip file in `infinitest-intellij/target`
* In Intellij's `Help / Edit Custom JVM Options` menu add the options for remote debug (and then you can remotely debug IntelliJ from Eclipse): `-agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n`

# Maintainers Area

## Signing the Eclipse and IntelliJ plugins

The plugins jars are signed with the key uploaded as the KEYSTORE_BASE64 Github secret, see [release.yml](.github/workflows/release.yml)
To generate a new key (for instance in case it expired, see [how to renew the certificate with the same key](https://xacmlinfo.org/2017/08/03/how-to-renew-self-signed-certificate-keeping-old-private-key/)):

`keytool -genkey -dname "cn=Guillaume Toison, ou=Developers, o=Infinitest, c=FR" -alias infinitest -keystore keystore.jks -validity 365`

Use a complex password and upload it as the `KEYSTORE_PASSWORD` secret.

We do not want to commit the key in Git, so we Base64 encode the keystore file and upload it as a Github secret. 

On linux:

`$ base64 -i keystore.jks -o keystore.base64`

On Windows we can use `certutil` and upload the content between the `BEGIN CERTIFICATE` and `END CERTIFICATE` lines *excluding the line breaks*:

`certutil -f -encode keystore.jks keystore.base64 `


## Publishing a release 

- Test, obviously
- Create a new branch for a pull request
- Add the changelog to the IntelliJ `plugin.xml` file
- Upgrade the `pom.xml` version: at the root of the project: 

`
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=5.3.0
`

- Commit the `pom.xml` files
- Upgrade again the `pom.xml` files to the next SNAPSHOT version:

`
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=5.3.1-SNAPSHOT
`

- Create a pull request from the head and, once approved, merge it (do not squash it since we need the two commits)
- Create a GitHub release on the commit with the final version
- The release name and the tag name must be the version, e.g. `5.3.0`
- In the release include the notable changes, GitHub can automatically list the Pull Requests since the last version
- Once created the release should trigger the [release.yml](.github/workflows/release.yml), this will add the artifacts to the release
- Get the IntelliJ plugin from the release page and upload it to the [marketplace](https://plugins.jetbrains.com/plugin/3146-infinitest)
- Get the Eclipse plugin from the release and commit the zip content to the [Infinitest website repository](https://github.com/infinitest/infinitest.github.com)