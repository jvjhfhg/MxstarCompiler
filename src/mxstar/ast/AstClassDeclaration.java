package mxstar.ast;

import mxstar.symbol.StClassSymbol;

import java.util.*;

public class AstClassDeclaration extends AstDeclaration {
    public String name;
    public List<AstVariableDeclaration> fields;
    public List<AstFunctionDeclaration> methods;
    public AstFunctionDeclaration constructor;

    public StClassSymbol symbol;

    public AstClassDeclaration() {
        fields = new LinkedList<>();
        methods = new LinkedList<>();
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
