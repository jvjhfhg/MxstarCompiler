package mxstar.AST;

public class AstExpression extends AstNode {
    public AstType valueType;

    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
