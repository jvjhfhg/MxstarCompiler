package mxstar.ast;

public class AstVarDeclStatement extends AstStatement {
    public AstVariableDeclaration declaration;
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
