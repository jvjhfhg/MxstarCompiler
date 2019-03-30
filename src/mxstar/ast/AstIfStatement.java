package mxstar.ast;

public class AstIfStatement extends AstStatement {
    public AstExpression condition;
    public AstStatement ifBody;
    public AstStatement elseBody;

    public AstIfStatement() {}
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
