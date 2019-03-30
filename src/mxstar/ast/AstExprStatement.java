package mxstar.ast;

public class AstExprStatement extends AstStatement {
    public AstExpression expr;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
