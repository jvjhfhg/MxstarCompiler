package mxstar.exception;

import mxstar.AST.TokenPosition;

import java.io.*;
import java.util.*;

public class ErrorRecorder {private List<String> errorMessages;

    public ErrorRecorder() {
        errorMessages = new LinkedList<String>();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String s: errorMessages) {
            str.append(s + "\n");
        }
        return str.toString();
    }

    public void add(TokenPosition position, String message) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        errorMessages.add(stacks[1].getClassName() + "." + stacks[1].getLineNumber() + ":" + position + ":" + message);
    }

    public List<String> getErrorList() {
        return errorMessages;
    }

    public boolean errorOccured() {
        return !errorMessages.isEmpty();
    }

    public void printTo(PrintStream out) {
        out.print(toString());
    }
}