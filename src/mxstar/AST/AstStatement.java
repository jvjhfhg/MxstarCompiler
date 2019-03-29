package mxstar.AST;

public class AstStatement extends AstNode {
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
