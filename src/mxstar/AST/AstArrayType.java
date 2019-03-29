package mxstar.AST;

public class AstArrayType extends AstType {
    public AstType baseType;
    public int dimension;

    public AstArrayType() {
        baseType = null;
        dimension = -1;
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
