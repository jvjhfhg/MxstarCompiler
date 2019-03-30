package mxstar.symbol;

import mxstar.ast.TokenPosition;

public class StPrimitiveSymbol extends StBaseSymbol {
    public String name;
    public TokenPosition position;

    public StPrimitiveSymbol() {}
    public StPrimitiveSymbol(String name) {
        this.name = name;
        this.position = new TokenPosition(0, 0);
    }
}
