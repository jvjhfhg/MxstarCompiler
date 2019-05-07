package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;

public abstract class IrOperand {
    public abstract void accept(IIrVisitor visitor);
}
