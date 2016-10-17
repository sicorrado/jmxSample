A simple command-line program which monitors values of a Mbean attribute
published on a remote JVM using the JMX API.
The Mbean attribute choosen is the Heap Memory Usage, with two different
approaches : 

(1) using the interface MemoryMBean and calling the methods of the MBean directly;

(2) declaring a dedicated MBean object, called "LargeArray", and calling the methods through a MBean proxy.

The MBean object LargeArray allows to build an array of double values and fill it several times,
in order to follow the heap memory usage behaviour.
This MBean object is also able to send a notification to signal that 
the free Heap Size is lower then the default min value (defined as a constanst variable).

Two jars are delivered with this program : 
- Server.jar, which represents the MBean server of the remote JVM, 
where the Mbean object is published; 
- Client.jar, which creates an RMI connector and performs remote operations 
on the MBeans.      


--------------------------------------------------------
To run the example, follow the steps below :
--------------------------------------------------------
a) Start the server on a specified remote port : 

java -Dcom.sun.management.jmxremote.port=9999 
-Dcom.sun.management.jmxremote.authenticate=false 
-Dcom.sun.management.jmxremote.ssl=false -jar Server.jar

The message "Server running..." will confirm that the server correctly started
and it will be also available from jconsole.

b) Start the client and indicates the host:port of the remote JVM as first argument :  

java -jar Client.jar host:port

by default, the client will try to connect to the localhost, on 9999 port.
A menu is displayed to give the choice between the following options : 

1. Show HeapMemoryUsage (MemoryMBean)
2. Show HeapMemoryUsage (LargeArrayMBean)
3. Invoke fillDoubleArray
4. Quit

