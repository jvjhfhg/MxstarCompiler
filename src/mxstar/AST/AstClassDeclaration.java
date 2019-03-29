package mxstar.AST;

import java.util.*;

public class AstClassDeclaration extends AstDeclaration {
    public String name;
    public List<AstVariableDeclaration> fields;
    public List<AstFunctionDeclaration> methods;
    public AstFunctionDeclaration constructor;

    public AstClassDeclaration() {
        name = null;
        fields = new LinkedList<>();
        methods = new LinkedList<>();
        constructor = null;
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
