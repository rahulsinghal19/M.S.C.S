Firstly, compile the java files in the directory:
javac *.java

Execute the java class files by varying the parameters to the program:
java <MainClass> <Total_Threads> <Inter-request_delay> <Number_of_operations_per_thread>

Example:
java MutualExclusionTest 16 0 1000

The execution mentioned above will execute both the Bakery algorithm & Tournament algorithm one after the other.