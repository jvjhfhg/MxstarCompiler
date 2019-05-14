package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;

public class IrImmidiate extends IrConstant {
    public int value;

    public IrImmidiate(int value) {
        this.value = value;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
