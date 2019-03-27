package mxstar.AST;

import java.util.*;

public class AstProgram extends AstNode {
    public List<AstClassDeclaration> classes;
    public List<AstFunctionDeclaration> functions;
    public List<AstVariableDeclaration> variables;
    public List<AstDeclaration> declarations;

    public AstProgram() {
        this.classes = new LinkedList<AstClassDeclaration>();
        this.functions = new LinkedList<AstFunctionDeclaration>();
        this.variables = new LinkedList<AstVariableDeclaration>();
        this.declarations = new LinkedList<AstDeclaration>();
    }

    public void add(AstClassDeclaration node) {
        classes.add(node);
        declarations.add(node);
    }

    public void add(AstFunctionDeclaration node) {
        functions.add(node);
        declarations.add(node);
    }

    public void addAll(List<AstVariableDeclaration> nodes) {
        variables.addAll(nodes);
        declarations.addAll(nodes);
    }

    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
