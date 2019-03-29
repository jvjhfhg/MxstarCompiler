package mxstar.AST;

public class AstExpression extends AstNode {
    public AstType type;

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
