package mxstar.symbol;

import mxstar.ast.TokenPosition;
import mxstar.ir.operand.IrVirtualRegister;

public class StVariableSymbol {
    public String name;
    public StType type;
    public TokenPosition position;

    public IrVirtualRegister virtualRegister;
    public boolean isClassField;
    public boolean isGlobal;

    public StVariableSymbol() {}
    public StVariableSymbol(String name, StType type, TokenPosition position) {
        this.name = name;
        this.type = type;
        this.position = position;
    }
}
