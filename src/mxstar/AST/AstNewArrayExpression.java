package mxstar.AST;

import java.util.*;

public class AstNewArrayExpression extends AstExpression {
    public AstType baseType;
    public List<AstExpression> indexes;
    public int emptyDimCnt;

    public AstNewArrayExpression() {
        baseType = null;
        indexes = new LinkedList<>();
        emptyDimCnt = -1;
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
