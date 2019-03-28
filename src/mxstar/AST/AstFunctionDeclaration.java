package mxstar.AST;

import java.util.*;

public class AstFunctionDeclaration extends AstDeclaration {
    public String funcName;
    public List<AstVariableDeclaration> parameters;
    public List<AstStatement> funcBody;

    public AstFunctionDeclaration() {
        funcName = null;
        parameters = new LinkedList<AstVariableDeclaration>();
        funcBody = new LinkedList<AstStatement>();
    }

    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
