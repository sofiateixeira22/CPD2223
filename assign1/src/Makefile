JC = javac
CC = g++
CFLAGS = -Wall

.SUFFIXES: .java .class

.java.class:
	$(JC) $*.java

CLASSES = \
		  matrixproduct.java

default: $(CLASSES:.java=.class)
		 $(CC) $(CFLAGS) matrixproduct.cpp -o matrixproduct

clean:
	$(RM) *.class
	$(RM) matrixproduct
	
runJava:
	java matrixproduct

runCPP:
	./matrixproduct

