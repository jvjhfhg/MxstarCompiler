package mxstar.AST;

public class AstType extends AstNode {
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
