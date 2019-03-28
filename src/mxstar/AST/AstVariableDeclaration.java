package mxstar.AST;

import java.util.*;

public class AstVariableDeclaration extends AstDeclaration {
    public AstType type;
    public String varName;
    public AstExpression initValue;

    public AstVariableDeclaration() {
        type = null;
        varName = null;
        initValue = null;
    }

    public AstVariableDeclaration(AstType type, String varName, AstExpression initValue) {
        this.type = type;
        this.varName = varName;
        this.initValue = initValue;
    }
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
