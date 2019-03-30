package mxstar.ast;

public class AstBinaryExpression extends AstExpression {
    public AstExpression expr1, expr2;
    public String opt;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
