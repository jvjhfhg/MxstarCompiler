package mxstar.exception;

import mxstar.AST.TokenPosition;

import java.io.*;
import java.util.*;

public class ErrorRecorder {
    private List<String> errorMessages;

    public ErrorRecorder() {
        errorMessages = new LinkedList<>();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: errorMessages) {
            stringBuilder.append(s + "\n");
        }
        return stringBuilder.toString();
    }

    public void add(TokenPosition position, String message) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        errorMessages.add(stacks[1].getClassName() + "." + stacks[1].getLineNumber() + ":" + position + ":" + message);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public boolean errorOccured() {
        return !errorMessages.isEmpty();
    }

    public void printTo(PrintStream out) {
        out.print(toString());
    }
}