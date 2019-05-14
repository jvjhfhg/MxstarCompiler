package mxstar.worker;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.IrProgram;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.*;
import mxstar.symbol.StVariableSymbol;

import java.util.HashSet;

import static mxstar.ir.IrRegisterSet.vArgs;
import static mxstar.ir.IrRegisterSet.vr8;

public class IrCorrector implements IIrVisitor {
    private IrProgram program;
    private boolean isBasicAllocator;

    public IrCorrector(boolean isBasicAllocator) {
        this.isBasicAllocator = isBasicAllocator;
    }

    @Override
    public void visit(IrProgram program) {
        this.program = program;
        for (IrFunction function : program.functions) {
            function.accept(this);
        }
    }

    @Override
    public void visit(IrFunction function) {
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            basicBlock.accept(this);
        }
    }

    @Override
    public void visit(IrBasicBlock basicBlock) {
        for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
            instruction.accept(this);
        }
    }

    @Override
    public void visit(IrBinaryInstruction instruction) {
        if ((instruction.opt == IrBinaryInstruction.IrBinaryOpt.MUL
                || instruction.opt == IrBinaryInstruction.IrBinaryOpt.DIV
                || instruction.opt == IrBinaryInstruction.IrBinaryOpt.MOD)
                && instruction.src instanceof IrConstant) {
            IrVirtualRegister virtualRegister = new IrVirtualRegister("");
            instruction.insertPrev(new IrMove(instruction.basicBlock, virtualRegister, instruction.src));
            instruction.src = virtualRegister;
        }
    }

    @Override
    public void visit(IrUnaryInstruction instruction) {

    }

    private IrPhysicalRegister getPhysicalRegister(IrOperand operand) {
        if (operand instanceof IrVirtualRegister) {
            return ((IrVirtualRegister) operand).allocatedPlace;
        } else {
            return null;
        }
    }

    @Override
    public void visit(IrMove instruction) {
        if (instruction.src instanceof IrMemory && instruction.dest instanceof IrMemory) {
            IrVirtualRegister virtualRegister = new IrVirtualRegister("");
            instruction.insertPrev(new IrMove(instruction.basicBlock, virtualRegister, instruction.src));
            instruction.src = virtualRegister;
        } else if (isBasicAllocator) {
            IrPhysicalRegister pDest = getPhysicalRegister(instruction.dest);
            IrPhysicalRegister pSrc = getPhysicalRegister(instruction.src);
            if (pDest != null && instruction.src instanceof IrMemory) {
                IrVirtualRegister virtualRegister = new IrVirtualRegister("");
                instruction.insertPrev(new IrMove(instruction.basicBlock, virtualRegister, instruction.src));
                instruction.src = virtualRegister;
            } else if (pSrc != null && instruction.dest instanceof IrMemory) {
                IrVirtualRegister virtualRegister = new IrVirtualRegister("");
                instruction.insertPrev(new IrMove(instruction.basicBlock, virtualRegister, instruction.dest));
                instruction.dest = virtualRegister;
            }
        }
    }

    @Override
    public void visit(IrPush instruction) {

    }

    @Override
    public void visit(IrPop instruction) {

    }

    @Override
    public void visit(IrCjump instruction) {
        if (instruction.lhs instanceof IrConstant) {
            if (instruction.rhs instanceof IrConstant) {
                instruction.insertPrev(new IrJump(instruction.basicBlock, instruction.doCompare()));
                instruction.delete();
            } else {
                instruction.reverseSrcs();
            }
        }
    }

    @Override
    public void visit(IrJump instruction) {

    }

    @Override
    public void visit(IrLea instruction) {

    }

    @Override
    public void visit(IrReturn instruction) {

    }

    @Override
    public void visit(IrCall instruction) {
        IrFunction caller = instruction.basicBlock.function;
        IrFunction callee = instruction.function;
        HashSet<StVariableSymbol> callerUsed = caller.usedGlobalVariables;
//        HashSet<StVariableSymbol> calleeUsed = callee.recursiveUsedGlobalVariables;
        for (StVariableSymbol variableSymbol : callerUsed) {
//            if (calleeUsed.contains(variableSymbol)) {
                instruction.insertPrev(new IrMove(instruction.basicBlock, variableSymbol.virtualRegister.spillPlace, variableSymbol.virtualRegister));
                instruction.prev.accept(this);
//            }
        }
        while (instruction.arguments.size() > 6) {
            instruction.insertPrev(new IrPush(instruction.basicBlock, instruction.arguments.removeLast()));
        }
        for (int i = instruction.arguments.size() - 1; i >= 0; --i) {
            instruction.insertPrev(new IrMove(instruction.basicBlock, vArgs.get(i), instruction.arguments.get(i)));
            instruction.prev.accept(this);
        }
        for (StVariableSymbol variableSymbol : callerUsed) {
//            if (calleeUsed.contains(variableSymbol)) {
                instruction.insertNext(new IrMove(instruction.basicBlock, variableSymbol.virtualRegister, variableSymbol.virtualRegister.spillPlace));
//            }
        }
    }

    @Override
    public void visit(IrCdq instruction) {

    }

    @Override
    public void visit(IrLeave instruction) {

    }

    @Override
    public void visit(IrMemory operand) {

    }

    @Override
    public void visit(IrVirtualRegister operand) {

    }

    @Override
    public void visit(IrPhysicalRegister operand) {

    }

    @Override
    public void visit(IrStackSlot operand) {

    }

    @Override
    public void visit(IrImmidiate operand) {

    }

    @Override
    public void visit(IrStaticData operand) {

    }

    @Override
    public void visit(IrFunctionAddress operand) {

    }
}
