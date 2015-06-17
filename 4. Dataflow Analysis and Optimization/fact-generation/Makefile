all: compile

compile:
	java -jar jtb132di.jar -te spiglet.jj
	java -jar javacc5.jar spiglet-jtb.jj
	javac *.java 

clean:	
	rm -rf *.class fact_info/*.class *~ syntaxtree visitor Return* Spiglet* Token* JTBTool* Java* Parse* spiglet-jtb.jj *.iris
