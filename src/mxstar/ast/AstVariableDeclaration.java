package mxstar.ast;

import mxstar.symbol.StVariableSymbol;

public class AstVariableDeclaration extends AstDeclaration {
    public AstType type;
    public String name;
    public AstExpression initValue;

    public StVariableSymbol symbol;

    public AstVariableDeclaration() {}
    public AstVariableDeclaration(AstType type, String name, AstExpression initValue) {
        this.type = type;
        this.name = name;
        this.initValue = initValue;
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
