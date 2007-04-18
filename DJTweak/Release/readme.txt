The DJ Project - Rediscover the Dekstop
http://djproject.sourceforge.net
Christopher Deckers (chrriis@nextencia.net)
Licence terms: GPL v2 (see licence.txt)


1. What is DJ Tweak?

DJ Tweak is part of the DJ tool set. It allows to configure a JAR file to modify
the icons that the operating system shows for that file. It also allows to
specify the Virtual Machine arguments and to alter the content of the manifest
file.

Note that icons are shown for JAR files by the operating system only if the
corresponding application from the DJ Project is installed.

DJ Tweak requires Java 5.0 or greater.


2. How to use it?

Simply launch the graphical user interface.

Alternatively, you can use DJ Tweak's Ant task. Here is an example:

<project name="Sample build script" default="configureJar">
  <target name="configureJar" description="Configure the JAR file icons">
    <taskdef resource="chrriis/dj/tweak/ant/task.properties" classpath="DJTweak.jar"/>
    <djtweak file="MyJARFile.jar" tofile="MyJARFileWithIcons.jar">
      <icons>
        <internalset>
          <include name="some/package/in/the/jar/file/**/*.png"/>
        </internalset>
        <externalset dir="icons">
          <include name="some/folder/on/the/filesystem/**/*.gif"/>
        </externalset>
      </icons>
      <vmargs>
        <pattern vendor="Sun .*" version="1\.[^01234].*"
            args="-DSomeProperty=SomeValue"/>
      </vmargs>
    </djtweak>
  </target>
</project>


3. What is the development status?

Icon and manifest handling is implemented. More modules may come if there is a
need.

For more detailed information about the current implementation status, visit
the DJ Project's website.


4. Sources?

The sources are part of the source distribution and on SourceForge.


5. How to support?

Kind e-mails are always a pleasure!
And if you feel this product was helpful, you can even make a donation from
SourceForge's pages.
