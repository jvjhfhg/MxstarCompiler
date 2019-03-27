package mxstar.AST;

public abstract class AstNode {
    public TokenPosition position;

    public abstract void accept(IAstVisitor visitor);
}
