package mxstar.ir;

import mxstar.ir.instruction.*;
import mxstar.ir.operand.IrPhysicalRegister;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrVirtualRegister;
import mxstar.symbol.StVariableSymbol;

import java.util.HashSet;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.*;

public class IrFunction {
    public enum IrFuncType {
        EXTERNAL, BUILTIN, USERDEFINED
    }

    public IrFuncType type;
    public String name;
    public IrBasicBlock frontBasicBlock;
    public IrBasicBlock backBasicBlock;
    public LinkedList<IrBasicBlock> basicBlocks;
    public HashSet<IrFunction> callees;
    public LinkedList<IrVirtualRegister> parameters;
    public LinkedList<IrBasicBlock> reversePostOrder;
    public LinkedList<IrBasicBlock> reversePostOrderOnReversedCFG;

    public boolean hasReturnValue;

    public HashSet<StVariableSymbol> usedGlobalVariables;
    public HashSet<StVariableSymbol> recursiveUsedGlobalVariables;
    public HashSet<IrPhysicalRegister> usedPhysicalRegisters;
    public HashSet<IrPhysicalRegister> recursiveUsedPhysicalRegisters;

    private HashSet<IrFunction> visitedFunctions;
    private HashSet<IrBasicBlock> visitedBlocks;

    public IrFunction(IrFuncType type, String name, boolean hasReturnValue) {
        this.type = type;
        this.name = name;
        this.hasReturnValue = hasReturnValue;

        this.callees = new HashSet<>();
        this.basicBlocks = new LinkedList<>();
        this.parameters = new LinkedList<>();

        this.usedGlobalVariables = new HashSet<>();
        this.recursiveUsedGlobalVariables = new HashSet<>();
        this.usedPhysicalRegisters = new HashSet<>();
        this.recursiveUsedPhysicalRegisters = new HashSet<>();

        this.visitedFunctions = new HashSet<>();
        this.visitedBlocks = new HashSet<>();

        this.reversePostOrder = new LinkedList<>();
        this.reversePostOrderOnReversedCFG = new LinkedList<>();

        if (type != IrFuncType.USERDEFINED && !name.equals("init")) {
            for (IrPhysicalRegister physicalRegister : allRegs) {
                if (physicalRegister.name.equals("rbp") || physicalRegister.name.equals("rsp")) {
                    continue;
                }
                this.usedPhysicalRegisters.add(physicalRegister);
                this.recursiveUsedPhysicalRegisters.add(physicalRegister);
            }
        }
    }

    private void dfsReversePostOrder(IrBasicBlock node) {
        if (visitedBlocks.contains(node)) {
            return;
        }
        visitedBlocks.add(node);
        for (IrBasicBlock basicBlock : node.successors) {
            dfsReversePostOrder(basicBlock);
        }
        reversePostOrder.addFirst(node);
    }

    private void dfsReversePostOrderOnReversedCFG(IrBasicBlock node) {
        if (visitedBlocks.contains(node)) {
            return;
        }
        visitedBlocks.add(node);
        for (IrBasicBlock basicBlock : node.predecessors) {
            dfsReversePostOrderOnReversedCFG(basicBlock);
        }
        reversePostOrderOnReversedCFG.addFirst(node);
    }

    private void dfsRecursiveUsedGlobalVariables(IrFunction node) {
        if (visitedFunctions.contains(node)) {
            return;
        }
        visitedFunctions.add(node);
        for (IrFunction function : node.callees) {
            dfsRecursiveUsedGlobalVariables(function);
        }
        recursiveUsedGlobalVariables.addAll(node.usedGlobalVariables);
    }

    public void finalProcess() {
        for (IrBasicBlock basicBlock : basicBlocks) {
            basicBlock.predecessors.clear();
            basicBlock.successors.clear();
        }
        for (IrBasicBlock basicBlock : basicBlocks) {
            if (basicBlock.tail instanceof IrCjump) {
                basicBlock.successors.add(((IrCjump) basicBlock.tail).thenBlock);
                basicBlock.successors.add(((IrCjump) basicBlock.tail).elseBlock);
            } else if (basicBlock.tail instanceof IrJump) {
                basicBlock.successors.add(((IrJump) basicBlock.tail).target);
            }

            for (IrBasicBlock successor : basicBlock.successors) {
                successor.predecessors.add(basicBlock);
            }
        }
        for (IrBasicBlock basicBlock : basicBlocks) {
            if (basicBlock.tail instanceof IrCjump) {
                IrCjump cjump = (IrCjump) basicBlock.tail;
                if (cjump.thenBlock.predecessors.size() < cjump.elseBlock.predecessors.size()) {
                    cjump.reverseThenElse();
                }
            }
        }

        visitedBlocks.clear();
        reversePostOrder.clear();
        dfsReversePostOrder(frontBasicBlock);

        visitedBlocks.clear();
        reversePostOrderOnReversedCFG.clear();
        dfsReversePostOrderOnReversedCFG(backBasicBlock);

        visitedFunctions.clear();
        recursiveUsedGlobalVariables.clear();
        dfsRecursiveUsedGlobalVariables(this);
    }

    private LinkedList<IrPhysicalRegister> trans(LinkedList<IrRegister> regs) {
        LinkedList<IrPhysicalRegister> ret = new LinkedList<>();
        for (IrRegister reg : regs) {
            ret.add((IrPhysicalRegister) reg);
        }
        return ret;
    }

    private void dfsRecursiveUsedPhysicalRegisters(IrFunction node) {
        if (visitedFunctions.contains(node)) {
            return;
        }
        visitedFunctions.add(node);
        for (IrFunction function : node.callees) {
            dfsRecursiveUsedPhysicalRegisters(function);
        }
        recursiveUsedPhysicalRegisters.addAll(node.usedPhysicalRegisters);
    }

    private boolean isSpecialBinaryOpt(IrBinaryInstruction.IrBinaryOpt opt) {
        return opt == IrBinaryInstruction.IrBinaryOpt.MUL
                || opt == IrBinaryInstruction.IrBinaryOpt.DIV
                || opt == IrBinaryInstruction.IrBinaryOpt.MOD;
    }

    public void finalAllocation() {
        for (IrBasicBlock basicBlock : basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                if (instruction instanceof IrReturn) {
                    continue;
                }
                if (instruction instanceof IrCall) {
                    usedPhysicalRegisters.addAll(callerSave);
                } else if (instruction instanceof IrBinaryInstruction && isSpecialBinaryOpt(((IrBinaryInstruction) instruction).opt)) {
                    if (((IrBinaryInstruction) instruction).src instanceof IrRegister) {
                        usedPhysicalRegisters.add((IrPhysicalRegister) ((IrBinaryInstruction) instruction).src);
                    }
                    usedPhysicalRegisters.add(rax);
                    usedPhysicalRegisters.add(rdx);
                } else {
                    usedPhysicalRegisters.addAll(trans(instruction.getUsedRegs()));
                    usedPhysicalRegisters.addAll(trans(instruction.getDefRegs()));
                }
            }
        }
        visitedFunctions.clear();
        dfsRecursiveUsedPhysicalRegisters(this);
    }

    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}