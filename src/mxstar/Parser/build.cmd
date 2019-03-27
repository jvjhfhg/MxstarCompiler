@echo off
SET CLASSPATH=.;..\..\..\lib\antlr-4.7.2-complete.jar;%CLASSPATH%
java org.antlr.v4.Tool -visitor Mxstar.g4