package mxstar.AST;

public interface IAstVisitor {
    void visit(AstProgram node);

    void visit(AstType node);
    void visit(AstArrayType node);
    void visit(AstPrimitiveType node);
    void visit(AstClassType node);

    void visit(AstDeclaration node);
    void visit(AstClassDeclaration node);
    void visit(AstFunctionDeclaration node);
    void visit(AstVariableDeclaration node);

    void visit(AstExpression node);
    void visit(AstLiteralExpression node);
    void visit(AstIdentifierExpression node);
    void visit(AstMemberAccessExpression node);
    void visit(AstFunctionCallExpression node);
    void visit(AstArrayIndexExpression node);
    void visit(AstNewArrayExpression node);
    void visit(AstNewExpression node);
    void visit(AstUnaryExpression node);
    void visit(AstPostfixIncDecExpression node);
    void visit(AstBinaryExpression node);
    void visit(AstAssignmentExpression node);

    void visit(AstStatement node);
    void visit(AstBlockStatement node);
    void visit(AstVarDeclStatement node);
    void visit(AstExprStatement node);
    void visit(AstIfStatement node);
    void visit(AstWhileStatement node);
    void visit(AstForStatement node);
    void visit(AstBreakStatement node);
    void visit(AstContiStatement node);
    void visit(AstReturnStatement node);
    void visit(AstEmptyStatement node);
}
