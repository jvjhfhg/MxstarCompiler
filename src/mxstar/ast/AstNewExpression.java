package mxstar.ast;

import java.util.*;

public class AstNewExpression extends AstExpression {
    public AstType baseType;

    public AstNewExpression() {}
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
