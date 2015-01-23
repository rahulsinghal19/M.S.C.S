Group
-----
Gaurav Kamath
Radhika Pai
Rahul Singhal
---------------------
Compile Instructions.
---------------------
In the src directory, run the following command to compile the source code.

javac *.java

-----------------------
Execution Instructions.
-----------------------

To run it on a distributed environment, use the Test.sh script.

It can also be configured to run on a single system with some modifications.

If you intend to run it individually, use the following commands.

Make sure the configuration.txt (config file) is in the same folder and then run

java Main <NodeID> 
(<NodeID> is a number between 0 - (n-1)
------------
Config File.
------------
Example for 4 nodes.

4 ;number of processes
8008 ;port number of Monitor Process
dc14.utdallas.edu 4425 0 ;host, port, process id
dc15.utdallas.edu 4426 1
dc16.utdallas.edu 4427 2
dc17.utdallas.edu 4428 3
4 ;number of quorums
3 0 1 2 ;size of quorum, followed by process ids
3 0 1 3
3 2 3 0
3 2 3 1
-------------------------
Testing and Verification.
-------------------------

We use an auxillary monitor server, which verifies and reports that only one and only one process is concurrently in the Critical section.
Everytime a process enters CS, it reports it to the Monitor and when it leaves it does the same. Therefore, the monitor server tries to verify if no two process have sent CS enter messages simulataneously.

The monitor server is also responsible for the termination of the Distributed system.