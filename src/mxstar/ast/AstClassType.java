package mxstar.ast;

public class AstClassType extends AstType {
    public String name;

    public AstClassType() {
        name = null;
    }
    public AstClassType(String name) {
        this.name = name;
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
