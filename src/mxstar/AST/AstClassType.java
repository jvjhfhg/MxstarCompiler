package mxstar.AST;

public class AstClassType extends AstType {
    public String typeName;

    public AstClassType() {
        typeName = null;
    }
    public AstClassType(String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
