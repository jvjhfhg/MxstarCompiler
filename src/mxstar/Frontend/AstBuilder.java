package mxstar.frontend;

import mxstar.AST.*;
import mxstar.parser.MxstarBaseVisitor;
import mxstar.parser.MxstarParser.*;

import java.util.*;
import java.io.*;

public class AstBuilder extends MxstarBaseVisitor<Object> {
    public AstProgram program;

}
