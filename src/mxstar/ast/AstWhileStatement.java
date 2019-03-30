package mxstar.ast;

import java.util.*;

public class AstWhileStatement extends AstStatement {
    public AstExpression condition;
    public AstStatement body;

    public AstWhileStatement() {}
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
