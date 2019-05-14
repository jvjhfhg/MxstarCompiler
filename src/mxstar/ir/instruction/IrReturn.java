package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrStackSlot;

import java.util.HashMap;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.vrax;

public class IrReturn extends IrInstruction {
    public IrReturn(IrBasicBlock basicBlock) {
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
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (basicBlock.function.hasReturnValue) {
            regs.add(vrax);
        }
        return regs;
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
