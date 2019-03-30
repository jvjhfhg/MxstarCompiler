package mxstar.symbol;

import mxstar.ast.TokenPosition;

public class StVariableSymbol {
    public String name;
    public StType type;
    public TokenPosition position;

    public StVariableSymbol() {}
    public StVariableSymbol(String name, StType type, TokenPosition position) {
        this.name = name;
        this.type = type;
        this.position = position;
    }
}
