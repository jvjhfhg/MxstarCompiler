package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrFunction;

public class IrStackSlot extends IrMemory {
    public String hint;
    public IrFunction function;

    public IrStackSlot(String hint) {
        this.hint = hint;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
