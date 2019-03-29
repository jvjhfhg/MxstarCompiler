package mxstar.AST;

public class AstType extends AstNode {
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
