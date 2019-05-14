package mxstar.ir.operand;

import mxstar.ir.IIrVisitor;

public class IrStaticData extends IrConstant {
    public String hint;
    public int bytes;
    public String init;

    public IrStaticData(String hint, int bytes) {
        this.hint = hint;
        this.bytes = bytes;
        this.init = null;
    }

    public IrStaticData(String hint, String init) {
        this.hint = hint;
        this.bytes = init.length() + 9;
        this.init = init;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
