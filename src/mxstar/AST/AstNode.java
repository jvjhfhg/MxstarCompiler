package mxstar.AST;

public abstract class AstNode {
    public TokenPosition position = null;
    public abstract void accept(IAstVisitor visitor);
}
