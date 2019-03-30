package mxstar.ast;

import java.util.*;

public class AstForStatement extends AstStatement {
    AstExpression expr1, expr2, expr3;
    List<AstStatement> body;

    public AstForStatement() {
        expr1 = null;
        expr2 = null;
        expr3 = null;
        body = new LinkedList<>();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
