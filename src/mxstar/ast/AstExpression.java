package mxstar.ast;

import mxstar.symbol.StType;

public class AstExpression extends AstNode {
    public StType valueType;
    public boolean mutable;

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
