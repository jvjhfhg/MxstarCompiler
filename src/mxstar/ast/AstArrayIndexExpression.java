package mxstar.ast;

public class AstArrayIndexExpression extends AstExpression {
    public AstExpression address;
    public AstExpression index;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
