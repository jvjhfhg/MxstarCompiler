package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;

public class IrPhysicalRegister extends IrRegister {
    public String name;

    public IrPhysicalRegister() {}

    public IrPhysicalRegister(String name) {
        this.name = name;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
