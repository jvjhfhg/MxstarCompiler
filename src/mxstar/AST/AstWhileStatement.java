package mxstar.AST;

import java.util.*;

public class AstWhileStatement extends AstStatement {
    public AstExpression condition;
    public List<AstStatement> body;

    public AstWhileStatement() {
        condition = null;
        body = new LinkedList<>();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
