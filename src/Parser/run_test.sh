#!/bin/sh
alias grun='java -Xmx500M -cp "/usr/local/lib/antlr-4.7.2-complete.jar:$CLASSPATH" org.antlr.v4.gui.TestRig'
javac *.java
grun mxstar compilationUnit -tree < test/test.mx > test/out
rm *.class
