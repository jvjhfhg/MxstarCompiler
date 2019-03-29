package mxstar.AST;

public class AstBreakStatement extends AstStatement {
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
