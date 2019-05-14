package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrFunction;

public class IrFunctionAddress extends IrConstant {
    public IrFunction function;

    public IrFunctionAddress(IrFunction function) {
        this.function = function;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
