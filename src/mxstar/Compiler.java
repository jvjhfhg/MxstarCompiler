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

        System.err.println("* Parsed...");

        AstBuilder astBuilder = new AstBuilder(errorRecorder);
        astBuilder.visit(parseTree);

        AstProgram astProgram = astBuilder.getAstProgram();

        System.err.println("* AST Built...");

        StBuilder stBuilder = new StBuilder(errorRecorder);
        astProgram.accept(stBuilder);

        System.err.println("* ST Built...");

        StGlobalTable globalTable = stBuilder.globalTable;
        SemanticChecker semanticChecker = new SemanticChecker(errorRecorder, globalTable);
        astProgram.accept(semanticChecker);

        if (errorRecorder.errorOccured()) {
            errorRecorder.printTo(System.out);
            System.exit(1);
        }

        System.err.println("* Semantic Check Passed...");

        ConstantFolder constantFolder = new ConstantFolder();
        astProgram.accept(constantFolder);

        System.err.println("* Constant Folded...");

        IrRegisterSet.init();
        IrBuilder irBuilder = new IrBuilder(globalTable);
        astProgram.accept(irBuilder);
        IrProgram irProgram = irBuilder.getIrProgram();

        System.err.println("* IR Built...");

        InstructionSimplifier instructionSimplifier = new InstructionSimplifier(irProgram);
        instructionSimplifier.process();

        System.err.println("* Useless Instruction Eliminated...");

        boolean isBasicAllocator = true;

        IrCorrector irCorrector = new IrCorrector(isBasicAllocator);
        irProgram.accept(irCorrector);

        System.err.println("* IR Corrected...");

        if (isBasicAllocator) {
            FoolAllocator allocator = new FoolAllocator(irProgram);
            allocator.process();
        } else {
            RegisterAllocator allocator = new RegisterAllocator(irProgram);
            allocator.process();
        }

        System.err.println("* Register Allocated...");

        StackFrameBuilder stackFrameBuilder = new StackFrameBuilder(irProgram);
        stackFrameBuilder.process();

        System.err.println("* Stack Frame Built...");

        IrPrinter irPrinter = new IrPrinter();
        irProgram.accept(irPrinter);
        irPrinter.printTo(System.out);

        System.err.println("* Compilation Completed.");
        System.exit(0);
    }
}
