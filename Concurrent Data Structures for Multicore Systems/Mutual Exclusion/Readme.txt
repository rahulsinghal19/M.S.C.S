Implement n-thread mutual exclusion using:
1. Tournament algorithm using Peterson’s algorithm as the building block, and
2. Lamport’s bakery algorithm.

Compare the performance of your two implementations experimentally with respect to system
throughput using the following system parameters:

1. System load: this depends on the mean inter-request delay. Assume that inter-request delay values are exponentially distributed and vary the mean time between two critical section requests from [0, 100] time units in suitable increments.

2. Degree of contention: this depends on the number of threads in the system. Vary the number of threads from one to twice the number of cores in the machine in suitable increments.

Average the results over several runs.

----------------------------------------------------------------------------------------------------------------------------------

Firstly, compile the java files in the directory:
javac *.java

Execute the java class files by varying the parameters to the program:
java <MainClass> <Total_Threads> <Inter-request_delay> <Number_of_operations_per_thread>

Example:
java MutualExclusionTest 16 0 1000

The execution mentioned above will execute both the Bakery algorithm & Tournament algorithm one after the other.