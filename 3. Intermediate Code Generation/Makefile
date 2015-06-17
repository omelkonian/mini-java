all: compile

compile:
	java -jar jtb132di.jar -te minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac *.java symboltable/*.java generators/*.java classinfo/*.java

test:
	@./test_compile
	cd ./output/; ./test_check

cleanOut:
	rm -rf output/*/; rm output/*.spg output/*.JVMout output/*.PGIout	

clean:	
	rm -rf *.class classinfo/*.class symboltable/*.class output/*.class *~ syntaxtree visitor MiniJava* Token* JTBTool* Java* Parse* minijava-jtb.jj ; rm -rf output/*/ ; rm output/*.spg output/*.JVMout output/*.PGIout	
