package mxstar.ast;

import mxstar.symbol.StFunctionSymbol;

import java.util.*;

public class AstFunctionDeclaration extends AstDeclaration {
    public AstType returnType;
    public String name;
    public List<AstVariableDeclaration> parameters;
    public List<AstStatement> body;

    public StFunctionSymbol symbol;

    public AstFunctionDeclaration() {
        parameters = new LinkedList<>();
        body = new LinkedList<>();
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
