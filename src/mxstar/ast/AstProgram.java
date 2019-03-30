package mxstar.ast;

import java.util.*;

public class AstProgram extends AstNode {
    public List<AstClassDeclaration> classes;
    public List<AstFunctionDeclaration> functions;
    public List<AstVariableDeclaration> variables;
    public List<AstDeclaration> declarations;

    public AstProgram() {
        this.classes = new LinkedList<>();
        this.functions = new LinkedList<>();
        this.variables = new LinkedList<>();
        this.declarations = new LinkedList<>();
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
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
