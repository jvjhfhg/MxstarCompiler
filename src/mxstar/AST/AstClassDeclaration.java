package mxstar.AST;

import java.util.*;

public class AstClassDeclaration extends AstDeclaration {
    public String className;
    public List<AstVariableDeclaration> variables;
    public List<AstFunctionDeclaration> methods;
    public AstFunctionDeclaration constructor;

    public AstClassDeclaration() {
        className = null;
        variables = new LinkedList<AstVariableDeclaration>();
        methods = new LinkedList<AstFunctionDeclaration>();
        constructor = null;
    }
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
