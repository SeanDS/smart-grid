smart-grid
==========

Utilities for studying the National Grid.

Frequency Logger
----------------

This program lets you log the frequency of the National Grid in Great Britain using the data available from the National Grid website. It scrapes the website's graph data and extracts the frequency, logging it to a local file.

You can either run it via Eclipse (a Java development environment) using the included run scripts or you can run it via your Java virtual machine. For instance, in Ubuntu you can run:

./Grid\ Analytics.jar

or you can right click the file and run it via (e.g.) OpenJVM 6.

There is a headless, non-GUI version too, which you can run by appending the --headless flag.

This software was built for a specific purpose, but I am willing to maintain it if you find bugs - please let me know via my Github page.

Sean Leavey
September 2014
https://github.com/SeanDS/