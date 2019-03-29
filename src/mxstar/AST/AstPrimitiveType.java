package mxstar.AST;

public class AstPrimitiveType extends AstType {
    public String name;

    public AstPrimitiveType() {
        name = null;
    }
    public AstPrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
