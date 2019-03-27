package mxstar.AST;

public class AstDeclaration extends AstNode {
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
