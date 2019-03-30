package mxstar.ast;

public class AstReturnStatement extends AstStatement {
    public AstExpression value;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
