package mxstar.AST;

public class AstPrimitiveType extends AstType {
    public String typeName;

    public AstPrimitiveType() {
        typeName = null;
    }
    public AstPrimitiveType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
