package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.*;

import java.util.HashMap;
import java.util.LinkedList;

public class IrMove extends IrInstruction {
    public IrAddress dest;
    public IrOperand src;

    public IrMove(IrBasicBlock basicBlock, IrAddress dest, IrOperand src) {
        super(basicBlock);
        this.dest = dest;
        this.src = src;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (src instanceof IrMemory) {
            src = ((IrMemory) src).copy();
            ((IrMemory) src).renameUsedReg(renameMap);
        } else if (src instanceof IrRegister && renameMap.containsKey(src)) {
            src = renameMap.get(src);
        }
        if (dest instanceof IrMemory) {
            dest = ((IrMemory) dest).copy();
            ((IrMemory) dest).renameUsedReg(renameMap);
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
        }
        if (src instanceof IrMemory) {
            regs.addAll(((IrMemory) src).getUsedRegs());
        } else if (src instanceof IrRegister) {
            regs.add((IrRegister) src);
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
        return defaultGetStackSlots(src, dest);
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
