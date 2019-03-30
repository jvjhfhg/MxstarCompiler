package mxstar.ast;

public class AstContiStatement extends AstStatement {
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
