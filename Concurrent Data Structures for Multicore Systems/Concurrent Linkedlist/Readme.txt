Implement a concurrent linked list that supports search, insert and delete operations using the
following two approaches discussed in the class:
1. Coarse-grained locking.
2. Fine-grained locking using hand-over-hand locking technique.

Compare the performance of your two implementations experimentally with respect to system throughput using the following system parameters:

1. Maximum size of the linked list: this depends on the size of the key space. Use key space sizes of 100, 1,000 and 10,000.

2. Relative distribution of various operations: use the following three different types of work-loads: (a) read-dominated: 90% search, 9% insert and 1% delete. (b) mixed: 70% search, 20% insert and 10% delete. (c) write-dominated: 0% search, 50% insert and 50% delete.

3. Degree of concurrency: this depends on the number of threads in the system. Vary the number of threads from one to twice the number of cores in the machine in suitable increments.

Average the results over several runs.

--------------------------------------------------------------------------------------------------------------------------------

Firstly, compile the java files in the directory:
javac *.java

Execute the java class files by varying the parameters to the program:
java <MainClass> <Total_Threads> <Search_Thread_Percentage> <Insert_Thread_Percentage> <Delete_Thread_Percentage> <Number_of_operations> <LinkedList_Key_size)

Example:
java CncrtLinkedList 24 90 9 1 100000 100

java FgsCcrtLinkedList 24 90 9 1 100000 100