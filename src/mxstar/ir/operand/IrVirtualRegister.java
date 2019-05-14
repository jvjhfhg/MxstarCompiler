package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;

public class IrVirtualRegister extends IrRegister {
    public String hint;
    public IrPhysicalRegister allocatedPlace;
    public IrMemory spillPlace = null;

    public IrVirtualRegister(String hint) {
        this.hint = hint;
        this.allocatedPlace = null;
    }

    public IrVirtualRegister(String hint, IrPhysicalRegister allocatedPlace) {
        this.hint = hint;
        this.allocatedPlace = allocatedPlace;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
