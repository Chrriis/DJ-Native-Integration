The DJ Project - Rediscover the Dekstop
http://djproject.sourceforge.net
Christopher Deckers (chrriis@nextencia.net)
Issam Chehab (issam.chehab at gmail dot com)
Licence terms: GPL v2 (see licence.txt)


1. What is the DJ Project?

The DJ Project is a set of tools to enhance the user experience provided by Java
on the desktop.

The main focus is on making rich client Java applications first class citizens
of the operating system.

The DJ Project brings:
- Proper icon support for Jar files, with different icons to differentiate
  libraries and applications. The icons can be customized.
- The ability to run executable Jar files as real processes. See their name in
  the task manager, and have a proper task bar aggregation per program.
- Configuration of the default Virtual Machine arguments that an executable Jar
  file should use. Multiple sets of arguments can be defined to allow per vendor and/or runtime version configuration.
- The Tweak utility, to customize all the aspects described above, using its Ant
  task or its graphical user interface.
- An "Open in Shell" command in the contextual menu of a Jar file to launch it
  from a visible shell.
- A view of the manifest attributes of a Jar file from its properties.

2. How to use it?

The installer allows to decide which of the features to install. Most of the
features actually configure the environment automatically.

Some utilies can be accessed from the shortcuts in the Start menu if this option
was selected at install time.


3. What is the development status?

Several integrations to the Windows operating system and the necessary utilities
are implemented and seem stable. More extensions may come if we see a need.

About other operating systems support, we lack the competences to implement the
same functionalities. Nevertheless, the modifications affecting the files, like
the icon support for JAR files, are implemented with platform independance in
mind, so it is theoretically possible to implement it.
If someone with the necessary competences wants to contribute, just contact us!

For more detailed information about the current implementation status, visit
the DJ Project's website.


4. Troubleshooting

In case of a problem, due to the reinstallation of the Java runtime or issues
with the uninstaller for example, simply run the installer again: it will
restore a consistent system. In case of uninstaller problems, uninstalling
should work fine after the fresh re-installation.


5. Sources?

The sources are part of the source distribution and on SourceForge.


6. How to support?

Kind e-mails are always a pleasure!
And if you feel this product was helpful, you can even make a donation from
SourceForge's pages.
