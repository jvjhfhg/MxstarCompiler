package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.IrMemory;
import mxstar.ir.operand.IrOperand;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrStackSlot;
import org.antlr.v4.codegen.model.SrcOp;

import java.util.HashMap;
import java.util.LinkedList;

public class IrPush extends IrInstruction {
    public IrOperand src;

    public IrPush(IrBasicBlock basicBlock, IrOperand src) {
        super(basicBlock);
        this.src = src;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (src instanceof IrMemory) {
            src = ((IrMemory) src).copy();
            ((IrMemory) src).renameUsedReg(renameMap);
        }
    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (src instanceof IrRegister && renameMap.containsKey(src)) {
            src = renameMap.get(src);
        }
    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (src instanceof IrMemory) {
            regs.addAll(((IrMemory) src).getUsedRegs());
        }
        return regs;
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (src instanceof IrRegister) {
            regs.add((IrRegister) src);
        }
        return regs;
    }

    @Override
    public LinkedList<IrStackSlot> getStackSlots() {
        return defaultGetStackSlots(src);
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
