package mxstar.ast;

public class AstDeclaration extends AstNode {
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
