package mxstar.AST;

public class AstExprStatement extends AstStatement {
    AstExpression expr;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
