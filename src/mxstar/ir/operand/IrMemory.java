package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;
import mxstar.ir.instruction.IrReturn;

import java.util.HashMap;
import java.util.LinkedList;

public class IrMemory extends IrAddress {
    public IrRegister base = null;
    public IrRegister index = null;
    public int scale = 0;
    public IrConstant constant = null;

    public IrMemory() {}

    public IrMemory(IrRegister base) {
        this.base = base;
    }

    public IrMemory(IrRegister base, IrRegister index, int scale) {
        this.base = base;
        this.index = index;
        this.scale = scale;
    }

    public IrMemory(IrRegister base, IrConstant constant) {
        this.base = base;
        this.constant = constant;
    }

    public IrMemory(IrRegister index, int scale, IrConstant constant) {
        this.index = index;
        this.scale = scale;
        this.constant = constant;
    }

    public IrMemory(IrConstant constant) {
        this.constant = constant;
    }

    public IrMemory(IrRegister base, IrRegister index, int scale, IrConstant constant) {
        this.base = base;
        this.index = index;
        this.scale = scale;
        this.constant = constant;
    }

    public IrMemory copy() {
        if (this instanceof IrStackSlot) {
            return this;
        } else {
            return new IrMemory(base, index, scale, constant);
        }
    }

    public LinkedList<IrRegister> getUsedRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (base != null) {
            regs.add(base);
        }
        if (index != null) {
            regs.add(index);
        }
        return regs;
    }

    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (renameMap.containsKey(base)) {
            base = renameMap.get(base);
        }
        if (renameMap.containsKey(index)) {
            index = renameMap.get(index);
        }
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
