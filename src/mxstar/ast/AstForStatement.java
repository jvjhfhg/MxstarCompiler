package mxstar.ast;

public class AstForStatement extends AstStatement {
    public AstExpression expr1, expr2, expr3;
    public AstStatement body;

    public AstForStatement() {}
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
