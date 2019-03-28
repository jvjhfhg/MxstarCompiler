package mxstar.frontend;

import mxstar.AST.*;
import mxstar.parser.MxstarBaseVisitor;
import mxstar.parser.MxstarParser.*;
import mxstar.frontend.ErrorRecorder;

import java.util.*;

import static mxstar.parser.MxstarParse.*;

public class AstBuilder extends MxstarBaseVisitor<Object> {
    public AstProgram program;

}
