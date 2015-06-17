all: compile

compile:
	java -jar jtb132di.jar -te minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac Main.java

test:
	@./test-script.sh

clean:
	rm -rf *.class classinfo/*.class symboltable/*.class *~ syntaxtree visitor MiniJava* Token* JTBTool* Java* Parse* minijava-jtb.jj
