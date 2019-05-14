package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrStackSlot;

import java.util.HashMap;
import java.util.LinkedList;

public class IrLeave extends IrInstruction {
    public IrLeave(IrBasicBlock basicBlock) {
        super(basicBlock);
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {

    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {

    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        return new LinkedList<>();
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        return new LinkedList<>();
    }

    @Override
    public LinkedList<IrStackSlot> getStackSlots() {
        return new LinkedList<>();
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
