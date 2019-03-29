package mxstar.AST;

public class AstMemberAccessExpression extends AstExpression {
    public AstExpression object;
    public AstIdentifierExpression fieldAccess;
    public AstFunctionCallExpression methodCall;

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
