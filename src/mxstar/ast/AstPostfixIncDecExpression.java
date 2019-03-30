package mxstar.ast;

public class AstPostfixIncDecExpression extends AstExpression {
    public AstExpression expr;
    public String opt;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
