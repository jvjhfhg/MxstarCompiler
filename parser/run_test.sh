#!/bin/sh
alias grun='java -Xmx500M -cp "/usr/local/lib/antlr-4.7.2-complete.jar:$CLASSPATH" org.antlr.v4.gui.TestRig'
grun mxstar compilationUnit -tree < test/test.mx > test/out
