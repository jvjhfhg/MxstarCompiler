package mxstar.ast;

public abstract class AstNode {
    public TokenPosition position;

    public abstract void accept(IAstVisitor visitor);
}
