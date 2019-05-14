package mxstar.ir;

import mxstar.ir.operand.IrStaticData;

import java.util.LinkedList;

public class IrProgram {
    public LinkedList<IrFunction> functions;
    public LinkedList<IrStaticData> staticDatas;

    public IrProgram() {
        functions = new LinkedList<>();
        staticDatas = new LinkedList<>();
    }

    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
