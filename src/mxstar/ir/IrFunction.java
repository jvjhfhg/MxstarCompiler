package mxstar.ir;

import mxstar.ir.instruction.IrCjump;
import mxstar.ir.instruction.IrJump;
import mxstar.ir.operand.IrPhysicalRegister;
import mxstar.ir.operand.IrVirtualRegister;
import mxstar.symbol.StVariableSymbol;

import java.util.HashSet;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.allRegs;

public class IrFunction {
    public enum IrFuncType {
        EXTERNAL, BUILTIN, USERDEFINED
    }

    public IrFuncType type;
    public String name;
    public IrBasicBlock frontBasicBlock;
    public IrBasicBlock backBasicBlock;
    public LinkedList<IrBasicBlock> basicBlocks;
    public LinkedList<IrFunction> callees;
    public LinkedList<IrVirtualRegister> parameters;
    public boolean hasReturnValue;

    public HashSet<StVariableSymbol> usedGlobalVariables;
    public HashSet<StVariableSymbol> recursiveUsedGlobalVariables;
    public HashSet<IrPhysicalRegister> usedPhysicalRegisters;
    public HashSet<IrPhysicalRegister> recursiveUsedPhysicalRegisters;

    public IrFunction(IrFuncType type, String name, boolean hasReturnValue) {
        this.type = type;
        this.name = name;
        this.hasReturnValue = hasReturnValue;

        this.callees = new LinkedList<>();
        this.basicBlocks = new LinkedList<>();
        this.parameters = new LinkedList<>();

        this.usedGlobalVariables = new HashSet<>();
        this.recursiveUsedGlobalVariables = new HashSet<>();
        this.usedPhysicalRegisters = new HashSet<>();
        this.recursiveUsedPhysicalRegisters = new HashSet<>();

        if (type != IrFuncType.USERDEFINED && !name.equals("init")) {
            for (IrPhysicalRegister physicalRegister : allRegs) {
                if (!physicalRegister.name.equals("rbp") && !physicalRegister.name.equals("rsp")) {
                    this.usedPhysicalRegisters.add(physicalRegister);
                    this.recursiveUsedPhysicalRegisters.add(physicalRegister);
                }
            }
        }
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
    }

    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}