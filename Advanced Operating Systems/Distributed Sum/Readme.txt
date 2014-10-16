Implement a distributed system consisting of n processes. The value of n and the location of each
of the n processes is specified in a configuration file. Every process selects a label value (basically
an integer) uniformly at random in the beginning. Every process then circulates a token through
the system that visits each process in the system once and computes the sum of all the label values
along the way. The path taken by the token of each process is again specified in the configuration
file. This path is piggybacked on the token by the process that generated the token. At the end,
each process prints its label value and the sum of all the label values computed by its token.

--------------------------------------------------------------------------------------------------------

Compile: 
javac *.java

Run: 
on respective machines mentioned in the configuration file execute the following command
dc14.utdallas.edu : java SumDistributed 1
dc15.utdallas.edu : java SumDistributed 2
dc16.utdallas.edu : java SumDistributed 3
dc17.utdallas.edu : java SumDistributed 4

All the machines will have a sleep of 5 seconds before the program begins execution.

Please execute from separate terminals and you can witness the termination after the completion of all the processing.
I did not have a chance to run the program from the script, due to system errors and the processes running in the background.
