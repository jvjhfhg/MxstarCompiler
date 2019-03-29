package mxstar.AST;

import java.util.*;

public class AstFunctionDeclaration extends AstDeclaration {
    public AstType type;
    public String name;
    public List<AstVariableDeclaration> parameters;
    public List<AstStatement> body;

    public AstFunctionDeclaration() {
        type = null;
        name = null;
        parameters = new LinkedList<>();
        body = new LinkedList<>();
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
