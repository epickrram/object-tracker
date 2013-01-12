object-tracker
==============

Tracking object creation in the JVM

Introduction
------------

Object-tracker consists of a JVM agent that modifies your java objects in order to track their creation. This information can then be written to a file for later analysis.


Usage
-----

Download the bundle with dependencies from the download page, and unzip. Add the following arguments to the command line when starting java:

    -javaagent:./object-tracker-agent-0.1/object-tracker-agent-0.1.jar
    -cp ./object-tracker-agent-0.1/javassist.jar:./object-tracker-agent-0.1/juxtapose-1.0.jar

Opening jconsole, look for the MBean named `com.epickrram.tools:type=ObjectInstanceCounter`. To write object count data to disk, invoke the `dumpObjectCreationCounts` method, supplying a target filename.

Object-tracker will write out a csv containing the current snapshot of the tracked object creation counts:

    classname,count
    com.epickrram.testing.TestObjectTwo,48
    com.epickrram.testing.TestObjectOne,24


Configuration
-------------

You must specify at least an inclusion filter to tell Object-tracker what classes to track.

=== Configuration by file ===

Specify a system property `com.epickrram.tool.object-tracker.config.file` that points to  a properties file with the following entries:

* `com.epickrram.tool.object-tracker.config.include` 

Semi-colon separated regexes to specify classes to include (e.g. `com.mycompany;com.external.library`)

* `com.epickrram.tool.object-tracker.config.exclude` 

Semi-colon separated regexes to specify classes to exclude

Configuration by property
-------------------------

For simple regexes, just use the above system property keys to specify a single include/exclude regex.

Caveats
-------

Attempting to monitor creation of any of the JDK classes (i.e. `java.lang`, `java.util`) will fail with a nasty exception.

Dependencies
------------

Object-tracker uses:
  * [juxtapose](https://github.com/epickrram/juxtapose) for MBean management
  * [javassist](http://www.jboss.org/javassist) for byte-code manipulation
