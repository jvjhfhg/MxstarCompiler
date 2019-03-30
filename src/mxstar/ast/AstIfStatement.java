package mxstar.ast;

import java.util.*;

public class AstIfStatement extends AstStatement {
    public AstExpression condition;
    public List<AstStatement> ifBody;
    public List<AstStatement> elseBody;

    public AstIfStatement() {
        condition = null;
        ifBody = new LinkedList<>();
        elseBody = new LinkedList<>();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
