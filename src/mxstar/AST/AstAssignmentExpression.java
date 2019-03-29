package mxstar.AST;

public class AstAssignmentExpression extends AstExpression {
    public AstExpression expr1, expr2;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
