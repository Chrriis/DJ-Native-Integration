The DJ Project - Rediscover the Dekstop
http://djproject.sourceforge.net
Christopher Deckers (chrriis@nextencia.net)
Licence terms: GPL v2 (see licence.txt)


1. What is DJ WebApplicationPacker?

DJ WebApplicationPacker is part of the DJ tool set. It allows to create an
installer for WebStart applications. The resulting application can be installed
offline, with proper integration to the user's desktop, and benefit from the
udate mechanism of Java WebStart.

DJ WebApplicationPacker requires Java 5.0 or greater.


2. How to use it?

Simply launch the graphical user interface.
12345678901234567890123456789012345678901234567890123456789012345678901234567890
If you are behind a proxy and want to access a JNLP file from a URL, you need to
launch the application from a shell:
java -Dhttp.useProxy=true -Dhttp.proxyHost=123.123.123.123 -Dhttp.proxyPort=80 -jar DJWebApplicationPacker.jar

Alternatively, you can use DJ WebApplicationPacker's Ant task. Here is an
example:

<project name="Sample build script" default="createInstallerJar">
  <target name="createInstallerJar" description="Create the Installer Jar file">
    <taskdef resource="chrriis/dj/wapacker/ant/task.properties" classpath="DJWebApplicationPacker.jar"/>
    <djwapacker jnlpurl="http://udoc.sf.net/UDoc.jnlp"
                readmefile="readme.html"
                licensefile="license.txt"
                tofile="udoc-installer.jar"
                applicationarchivefile="udoc-wsdeploy.zip"/>
  </target>
</project>


3. What is the development status?

The application that gets packed behaves as expected. A few minor
functionalities would be nice to be implemented, but we hit the limitations
imposed by the Java Runtime Environment and Java WebStart.

For more detailed information about the current implementation status, visit
the DJ Project's website.


4. Sources?

The sources are part of the source distribution and on SourceForge.


5. How to support?

Kind e-mails are always a pleasure!
And if you feel this product was helpful, you can even make a donation from
SourceForge's pages.
