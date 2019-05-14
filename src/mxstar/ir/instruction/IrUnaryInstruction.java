package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.*;

import java.util.HashMap;
import java.util.LinkedList;

public class IrUnaryInstruction extends IrInstruction {
    public enum IrUnaryOpt {
        NEG, NOT, INC, DEC
    }

    public IrUnaryOpt opt;
    public IrAddress dest;

    public IrUnaryInstruction(IrBasicBlock basicBlock, IrUnaryOpt opt, IrAddress dest) {
        super(basicBlock);
        this.opt = opt;
        this.dest = dest;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (dest instanceof IrMemory) {
            dest = ((IrMemory) dest).copy();
            ((IrMemory) dest).renameUsedReg(renameMap);
        } else if (dest instanceof IrRegister && renameMap.containsKey(dest)) {
            dest = renameMap.get(dest);
        }
    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (dest instanceof IrRegister && renameMap.containsKey(dest)) {
            dest = renameMap.get(dest);
        }
    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (dest instanceof IrMemory) {
            regs.addAll(((IrMemory) dest).getUsedRegs());
        } else if (dest instanceof IrRegister) {
            regs.add((IrRegister) dest);
        }
        return regs;
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (dest instanceof IrRegister) {
            regs.add((IrRegister) dest);
        }
        return regs;
    }

    @Override
    public LinkedList<IrStackSlot> getStackSlots() {
        return defaultGetStackSlots(dest);
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
