package mxstar;

import mxstar.ast.AstProgram;
import mxstar.exception.*;
import mxstar.parser.MxstarLexer;
import mxstar.parser.MxstarParser;
import mxstar.symbol.StGlobalTable;
import mxstar.worker.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Compiler {
    public static void main(String[] argv) throws IOException {
        InputStream is = new FileInputStream("program.cpp");
        ANTLRInputStream ais = new ANTLRInputStream(is);
        MxstarLexer lexer = new MxstarLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxstarParser parser = new MxstarParser(tokens);
        ErrorRecorder errorRecorder = new ErrorRecorder();

        parser.removeErrorListeners();
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener(errorRecorder);
        parser.addErrorListener(syntaxErrorListener);

        ParseTree parseTree = parser.compilationUnit();

        AstBuilder astBuilder = new AstBuilder(errorRecorder);
        astBuilder.visit(parseTree);

        AstProgram program = astBuilder.getAstProgram();

        StBuilder stBuilder = new StBuilder(errorRecorder);
        program.accept(stBuilder);

        StGlobalTable globalTable = stBuilder.globalTable;
        SemanticChecker semanticChecker = new SemanticChecker(errorRecorder, globalTable);
        program.accept(semanticChecker);

        if (errorRecorder.errorOccured()) {
            errorRecorder.printTo(System.out);
            System.exit(1);
        }

        System.err.println("Compilation completed. ");
        System.exit(0);
    }
}
