package mxstar.ast;

import mxstar.symbol.StFunctionSymbol;

import java.util.*;

public class AstFunctionCallExpression extends AstExpression {
    public String name;
    public List<AstExpression> arguments;

    public StFunctionSymbol symbol;

    public AstFunctionCallExpression() {
        name = null;
        arguments = new LinkedList<>();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
