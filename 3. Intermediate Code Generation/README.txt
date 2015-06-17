To compile:
	$ make

To clean:
	$ make clean

To execute (generate SPIGLET code to folder output/):
	$ java Main <inputFile1> .... <inputFileN>

# The test sript compiles all .java files in directory Examples/mini-java using both my compiler and javac
# to produce SPIGLET and Java Byte-code respectively. Then it cross checks the results of all the programs
# using PGI and JVM respectively.
To run test script (after $ make):
	$ make test  
