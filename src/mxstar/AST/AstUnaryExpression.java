package mxstar.AST;

public class AstUnaryExpression extends AstExpression {
    public String opt;
    public AstExpression expr;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
