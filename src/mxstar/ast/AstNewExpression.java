package mxstar.ast;

import java.util.*;

public class AstNewExpression extends AstExpression {
    public AstType baseType;
    public List<AstExpression> arguments;

    public AstNewExpression() {
        baseType = null;
        arguments = new LinkedList<>();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
