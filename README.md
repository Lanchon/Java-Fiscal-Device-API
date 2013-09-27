Java Fiscal Device API
======================

Copyright 2003, Rodrigo Balerdi.
Licensed under the [GNU General Public License (GPL) Version 2](http://www.gnu.org/licenses/gpl-2.0-standalone.html).

The Java Fiscal Device API is a medium-level API to communicate with fiscal devices such as fiscal printers. It handles the communication protocol and the formatting and parsing of the protocol packets, and provides event-based notification for certain events such as out-of-paper events. It was made for Hasar printers but also works fine with Epsons, after some small protocol differences were accommodated.

Where do I start?
-----------------

You can try the API documentation. These interfaces will probably be the most interesting to you:

    com.taliter.fiscal.device.FiscalDeviceSource
    com.taliter.fiscal.device.FiscalDevice
    com.taliter.fiscal.device.FiscalPacket
    com.taliter.fiscal.device.FiscalDeviceEventHandler

And these classes encapsulate all the configuration options:

    com.taliter.fiscal.device.hasar.HasarFiscalDeviceSource
    com.taliter.fiscal.port.serial.SerialFiscalPortSource

Additionally, `com.taliter.fiscal.device.hasar.HasarConstants` defines some useful constants for Hasar printers. The documentation for these is written in spanish. If you are using another brand of printer it is up to you to check with the printer's manual, these constants may or may not be useful to you. Beware that some commands may produce irreversible changes to your printer, even to the point of rendering it useless, so read the manual carefully.

You may want to take a look into package `com.taliter.fiscal.util`. It contains tools that provide protocol and port speed autodetection, as well as event and communication loggers that can assist you during development.

There is also a very basic sample available.

Update
------

This software depends on an implementation of its own `FiscalPort` interface to communicate with devices. It includes a `SerialFiscalPort` implementation for serial ports that is a thin wrapper over Sun's Java Communications API (`javax.comm`). Unfortunately Sun has deprecated this API without providing an alternative and some operating systems are no longer supported. However there are some free and commercial implementations of `javax.comm`, for example [RXTX](http://rxtx.qbang.org/). There are also other serial port drivers over which a `FiscalPort` could be more or less trivially implemented; see [java-simple-serial-connector](https://code.google.com/p/java-simple-serial-connector/) and [serial-comm](https://code.google.com/p/serial-comm/).

Please contribute your drivers to the project. They will be accepted if they are tested to work and implement `FiscalPort` and `FiscalPortSource` classes in their own package, in the style of package `com.taliter.fiscal.port.serial`. (Modifications to the current driver will not be accepted unless they are bug fixes.)

I no longer have access to fiscal devices and thus I cannot test. For this reason I cannot implement `FiscalPort`s that wrap these new serial port drivers. However this API is still being maintained: no bugfix releases were made in 10 years because, although it has many users, no bugs were ever reported against it. Debugging would be harder now without test devices but I will still try my best to fix the code if a bug is reported.

And what else?
--------------

Not much really. Drop me a line if you use this software, tell me if it worked with your printer model, or what you are doing, or whatever.

