package mxstar.ast;

public class AstEmptyStatement extends AstStatement {
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
