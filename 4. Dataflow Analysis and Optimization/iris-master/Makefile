JAVAC=javac
JVM=java
CLASSPATH = lib/iris-0.60.jar:lib/iris-parser-0.60.jar:src/
sources = $(wildcard src/iris/*.java)
classes = $(sources:.java=.class)

ifeq (run,$(firstword $(MAKECMDGOALS)))
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  $(eval $(RUN_ARGS):;@:)
endif
ifeq (optimize,$(firstword $(MAKECMDGOALS)))
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  $(eval $(RUN_ARGS):;@:)
endif

all: compile

compile: $(classes)

clean :
	rm -f $(classes) src/iris/*.class && rm -rf generated-facts/* && rm spiglet-optimized/*.spg	

%.class : %.java
	$(JAVAC) -cp $(CLASSPATH) $<

# Usage: optimize <ProgramName>
optimize:
	$(JVM) -cp $(CLASSPATH) iris.Optimizer $(RUN_ARGS)

# Usage: run <factsPath> <rulesPath/rules.iris> <queriesPath/queries.iris>
run: 
	$(JVM) -cp $(CLASSPATH) iris.Main $(RUN_ARGS)

run-family:
	$(JVM) -cp $(CLASSPATH) iris.Main datalog-examples/family/facts datalog-examples/family/rules.iris datalog-examples/family/queries.iris

run-graph:
	$(JVM) -cp $(CLASSPATH) iris.Main datalog-examples/graph/facts datalog-examples/graph/rules.iris datalog-examples/graph/queries.iris
