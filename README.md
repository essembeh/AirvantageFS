AirvantageFS (Airvantage Filesytem)
===================================

Small dameon to sync a folder with Airvantage Platform using the device REST API



Presentation
------------

The idea of this makeit is to create a very simple way to send data to the Airvantage platform.

Limitations of existing methods
 - Mihini: Only for linux, too much features if you just want to send data, not so easy to integrate with other languages
 - REST API: Limited to read and some actions, need to implement HTTP-REST client


The project 
 - Unique daemon which communicate with the platform
 - Using the filesystem to represent dataset (linux philosophy, everything is a file)
 - Using Java NIO to sync FS and the Airvantage dataset
 - Can be used by every application which can read write files to send data
 - Multi-platform (tested on Linux/MacOS)
 

Using
------

You need Java 7 (java.nio) to compile/run this project.

To download the last release, goto the [release page](https://github.com/essembeh/AirvantageFS/releases)

You must use a device with a REST application (for example *eclo-rest* on edge).
See the documentation [Using REST API for devices](http://airvantage.github.io/tutorials/2013/07/05/rest-for-devices/)

To start the daemon, you need 3 arguments:
 - the Airvantage Device *serial number*
 - the device password 
 - the path of the folder to monitor.

    $ java -jar afs.jar <device-serial> <password> <path-to-root-folder>


Exemple with Eclo demo
----------------------

    $ java -jar afs.jar SEB-TEST-ECLO 123456 /home/seb/eclo

You can update the temperature of a greenhouse with

    echo -n "20.5" > /home/seb/eclo/greenhouse/data/temperature

The daemon should log something like

    ENTRY_MODIFY: eclo/greenhouse/data eclo/greenhouse/data/temperature
    Push --> greenhouse.data.temperature: 20.5
    POST: https://edge.airvantage.net/device/messages [{"greenhouse.data.temperature":[{"timestamp":1374826814435,"value":"20.5"}]}]
    HTTP/1.1 200 OK


Next
----
 - Develop apply settings
 - generate filesystem from data model
 
