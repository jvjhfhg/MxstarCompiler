package mxstar.AST;

public class AstReturnStatement extends AstStatement {
    AstExpression value;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
