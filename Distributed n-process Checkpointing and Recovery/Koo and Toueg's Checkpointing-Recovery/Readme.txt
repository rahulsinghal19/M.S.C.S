Compile the java program:
javac *.java in src folder

Execute the project:
Run the Test.sh file from outside the src directory. Modify the path of the src folder in the script to execute the java project.

Please ensure the config file is present in the same directory as src folder.

Config.txt explained:
4 // number of processes in the system
dc14.utdallas.edu 4425 1,2 //<process_address port_number it's neighbors>
dc15.utdallas.edu 4426 0,2,3 //<process_address port_number it's neighbors>
dc16.utdallas.edu 4427 0,1,3 //<process_address port_number it's neighbors>
dc17.utdallas.edu 4428 1,2 //<process_address port_number it's neighbors>
2,c,0;1,r,1;3,c,2;2,r,3; //token of the series of operation to be performed; <process_id,type_of_operation,identifier>
10 // minimum sleep duration in seconds, maximum configured in code (upto 13 seconds)