package mxstar.ast;

public class AstVariableDeclaration extends AstDeclaration {
    public AstType type;
    public String name;
    public AstExpression initValue;

    public AstVariableDeclaration() {
        type = null;
        name = null;
        initValue = null;
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
