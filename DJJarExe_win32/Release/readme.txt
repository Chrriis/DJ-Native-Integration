The DJ Project - Rediscover the Dekstop
http://djproject.sourceforge.net
Christopher Deckers (chrriis@nextencia.net)
Licence terms: GPL v2 (see license.txt)


1. What is DJ JarExe?

DJ JarExe is part of the DJ tool set. It allows to run an executable JAR file in
its own dedicated process, which is easily identifiable in the task manager
through its name. It also allows the JAR files to run with pre-configured
Virtual Machine (VM) arguments, which can be defined per VM vendor and version.

DJ JarExe requires Java 5.0 or greater.


2. How to use it?

The program self registers in the operating system automatically. To configure
the Virtual Machine parameters, you should refer to the DJ Tweak utility.

Alternatively, one can use it by changing a typical line like:
    java.exe -jar myJar.jar myParam1 myParam2
by:
    java.exe -jar DJJarExe.jar myJar.jar myParam1 myParam2

Note 1: the process returns immediately after the JAR file is launched.
Note 2: if a problem occurs, then the JAR file is launched without the process
management.
Note 3: When multiple versions of Java are installed, it was noticed that
DJJarExe would work only with the default version.

It is possible to make the same executable JAR file behave as different
processes, by setting the system property "dj.jarexe.pid". For example:
    java.exe -Ddj.jarexe.pid="1" -jar DJJarExe.jar myJar.jar myParam1 myParam2

If one needs to identify the process, it bears the name of the JAR file. The
actual path on the other hand is different. It is created like this:
<temp_directory>/.djjarexe/<modified_path_to_jar>/<jar_name>
The modified_path_to_jar is a directory created with the path to the JAR file
where '/', '\' and ':' are replaced by '_'. In addition, if the pid property is
specified, it is appended to that path name.


3. What is the development status?

This application seems perfectly stable.

For more detailed information about the current implementation status, visit
the DJ Project's website.


4. Sources?

The sources are part of the source distribution and on SourceForge.


5. How to support?

Kind e-mails are always a pleasure!
And if you feel this product was helpful, you can even make a donation from
SourceForge's pages.
