AirvantageFS (Airvantage Filesystem)
===================================

Small dameon to sync a folder with Airvantage Platform using the device REST API



Presentation
------------

The idea of this *makeit* is to create a very simple way to send data to the Airvantage platform.

Limitations of existing methods
 - Mihini 
   - Only for linux
   - Too much features if you just want to push data
   - Limited bindings to allow pushing data with other languages: socket [EMP](http://download.eclipse.org/mihini/doc/agent_connector_libraries/Embedded_Micro_Protocol_EMP.html), c, lua, java (in work)
 - REST API
   - Limited, can only push data and read "apply settings" pending operations
   - Need to implement HTTP-REST client


The project 
 - Small daemon which only communicate with the platform to push datas
 - Using the filesystem to represent the dataset (linux philosophy, everything is a file)
 - Using Java NIO to monitor files change on the FS
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


Example

    $ java -jar afs.jar <device-serial> <password> <path-to-root-folder>


Example with Eclo demo
----------------------

Start the daemon 

    $ java -jar afs.jar SEB-TEST-ECLO 123456 /home/seb/eclo

To update the temperature, simple write the new value in the corresponding file

    echo -n "20.5" > /home/seb/eclo/greenhouse/data/temperature

The daemon will log something like

    ENTRY_MODIFY: eclo/greenhouse/data eclo/greenhouse/data/temperature
    Push --> greenhouse.data.temperature: 20.5
    POST: https://edge.airvantage.net/device/messages [{"greenhouse.data.temperature":[{"timestamp":1374826814435,"value":"20.5"}]}]
    HTTP/1.1 200 OK


Next
----
 - Develop "apply settings" operations polling to update the filesystem
 - Generate filesystem from the data model contained in the Airvantage Application Model
 - Using M3DA protocol to reduce payload
 
