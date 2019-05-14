package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.IrMemory;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrStackSlot;

import java.util.HashMap;
import java.util.LinkedList;

public class IrLea extends IrInstruction {
    public IrRegister dest;
    public IrMemory src;

    public IrLea(IrBasicBlock basicBlock, IrRegister dest, IrMemory src) {
        super(basicBlock);
        this.dest = dest;
        this.src = src;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        src = src.copy();
        src.renameUsedReg(renameMap);
    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (renameMap.containsKey(dest)) {
            dest = renameMap.get(dest);
        }
    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        regs.addAll(src.getUsedRegs());
        regs.add(dest);
        return regs;
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        regs.add(dest);
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
