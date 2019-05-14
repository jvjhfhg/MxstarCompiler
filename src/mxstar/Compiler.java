package mxstar;

import mxstar.ast.AstProgram;
import mxstar.exception.ErrorRecorder;
import mxstar.exception.SyntaxErrorListener;
import mxstar.ir.IrProgram;
import mxstar.ir.IrRegisterSet;
import mxstar.parser.MxstarLexer;
import mxstar.parser.MxstarParser;
import mxstar.symbol.StGlobalTable;
import mxstar.worker.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Compiler {
    public static void main(String[] args) throws IOException {
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

        AstProgram astProgram = astBuilder.getAstProgram();

        StBuilder stBuilder = new StBuilder(errorRecorder);
        astProgram.accept(stBuilder);

        StGlobalTable globalTable = stBuilder.globalTable;
        SemanticChecker semanticChecker = new SemanticChecker(errorRecorder, globalTable);
        astProgram.accept(semanticChecker);

        if (errorRecorder.errorOccured()) {
            errorRecorder.printTo(System.out);
            System.exit(1);
        }

        IrRegisterSet.init();
        IrBuilder irBuilder = new IrBuilder(globalTable);
        astProgram.accept(irBuilder);
        IrProgram irProgram = irBuilder.getIrProgram();

        IrCorrector irCorrector = new IrCorrector(true);
        irProgram.accept(irCorrector);

        RegisterAllocator allocator = new RegisterAllocator(irProgram);
        allocator.allocate();

        IrPrinter irPrinter = new IrPrinter();
        irProgram.accept(irPrinter);
        irPrinter.printTo(new PrintStream("program.asm"));

        System.err.println("Compilation completed. ");
        System.exit(0);
    }
}
