package mxstar.worker;

import mxstar.ast.*;

import java.util.HashMap;

public class ConstantFolder implements IAstVisitor {
    private HashMap<AstExpression, AstLiteralExpression> exprToConstantMap;

    public ConstantFolder() {
        exprToConstantMap = new HashMap<>();
    }

    @Override
    public void visit(AstProgram node) {
        for (AstFunctionDeclaration functionDeclaration : node.functions) {
            functionDeclaration.accept(this);
        }
        for (AstClassDeclaration classDeclaration : node.classes) {
            classDeclaration.accept(this);
        }
        for (AstVariableDeclaration variableDeclaration : node.variables) {
            variableDeclaration.accept(this);
        }
    }

    @Override
    public void visit(AstType node) {

    }

    @Override
    public void visit(AstArrayType node) {

    }

    @Override
    public void visit(AstPrimitiveType node) {

    }

    @Override
    public void visit(AstClassType node) {

    }

    @Override
    public void visit(AstDeclaration node) {

    }

    @Override
    public void visit(AstClassDeclaration node) {

    }

    @Override
    public void visit(AstFunctionDeclaration node) {
        for (AstVariableDeclaration variableDeclaration : node.parameters) {
            variableDeclaration.accept(this);
        }
        for (AstStatement statement : node.body) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(AstVariableDeclaration node) {
        if (node.initValue != null) {
            node.initValue.accept(this);
            if (exprToConstantMap.containsKey(node.initValue)) {
                node.initValue = exprToConstantMap.get(node.initValue);
            }
        }
    }

    @Override
    public void visit(AstExpression node) {

    }

    @Override
    public void visit(AstLiteralExpression node) {

    }

    @Override
    public void visit(AstIdentifierExpression node) {

    }

    @Override
    public void visit(AstMemberAccessExpression node) {
        if (node.methodCall != null) {
            node.methodCall.accept(this);
        }
    }

    @Override
    public void visit(AstFunctionCallExpression node) {
        for (int i = 0; i < node.arguments.size(); ++i) {
            node.arguments.get(i).accept(this);
            if (exprToConstantMap.containsKey(node.arguments.get(i))) {
                node.arguments.set(i, exprToConstantMap.get(node.arguments.get(i)));
            }
        }
    }

    @Override
    public void visit(AstArrayIndexExpression node) {
        node.index.accept(this);
        if (exprToConstantMap.containsKey(node.index)) {
            node.index = exprToConstantMap.get(node.index);
        }
    }

    @Override
    public void visit(AstNewArrayExpression node) {
        for (int i = 0; i < node.indexes.size(); ++i) {
            node.indexes.get(i).accept(this);
            if (exprToConstantMap.containsKey(node.indexes.get(i))) {
                node.indexes.set(i, exprToConstantMap.get(node.indexes.get(i)));
            }
        }
    }

    @Override
    public void visit(AstNewExpression node) {

    }

    @Override
    public void visit(AstUnaryExpression node) {
        node.expr.accept(this);
        if (exprToConstantMap.containsKey(node.expr)) {
            node.expr = exprToConstantMap.get(node.expr);
        }
    }

    @Override
    public void visit(AstPostfixIncDecExpression node) {
        node.expr.accept(this);
        if (exprToConstantMap.containsKey(node.expr)) {
            node.expr = exprToConstantMap.get(node.expr);
        }
    }

    @Override
    public void visit(AstBinaryExpression node) {
        node.expr1.accept(this);
        if (exprToConstantMap.containsKey(node.expr1)) {
            node.expr1 = exprToConstantMap.get(node.expr1);
        }
        node.expr2.accept(this);
        if (exprToConstantMap.containsKey(node.expr2)) {
            node.expr2 = exprToConstantMap.get(node.expr2);
        }

        if (node.expr1 instanceof AstLiteralExpression && node.expr2 instanceof AstLiteralExpression) {
            if (((AstLiteralExpression) node.expr1).typeName.equals("int") && ((AstLiteralExpression) node.expr2).typeName.equals("int")) {
                int l = Integer.valueOf(((AstLiteralExpression) node.expr1).value);
                int r = Integer.valueOf(((AstLiteralExpression) node.expr2).value);
                int res = 0;
                switch (node.opt) {
                    case "+":
                        res = l + r; break;
                    case "-":
                        res = l - r; break;
                    case "*":
                        res = l * r; break;
                    case "/":
                        res = l / r; break;
                    case "%":
                        res = l % r; break;
                    case ">>":
                        res = l >> r; break;
                    case "<<":
                        res = l << r; break;
                    case "&":
                        res = l & r; break;
                    case "|":
                        res = l | r; break;
                    case "^":
                        res = l ^ r; break;
                }
                AstLiteralExpression result = new AstLiteralExpression("int", String.valueOf(res));
                exprToConstantMap.put(node, result);
            }
        }
    }

    @Override
    public void visit(AstAssignmentExpression node) {
        if (node.expr2 != null) {
            node.expr2.accept(this);
            if (exprToConstantMap.containsKey(node.expr2)) {
                node.expr2 = exprToConstantMap.get(node.expr2);
            }
        }
    }

    @Override
    public void visit(AstStatement node) {

    }

    @Override
    public void visit(AstBlockStatement node) {
        for (AstStatement statement : node.statements) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(AstVarDeclStatement node) {
        node.declaration.accept(this);
    }

    @Override
    public void visit(AstExprStatement node) {
        node.expr.accept(this);
        if (exprToConstantMap.containsKey(node.expr)) {
            node.expr = exprToConstantMap.get(node.expr);
        }
    }

    @Override
    public void visit(AstIfStatement node) {
        node.condition.accept(this);
        if (exprToConstantMap.containsKey(node.condition)) {
            node.condition = exprToConstantMap.get(node.condition);
        }
        if (node.ifBody != null) {
            node.ifBody.accept(this);
        }
        if (node.elseBody != null) {
            node.elseBody.accept(this);
        }
    }

    @Override
    public void visit(AstWhileStatement node) {
        node.condition.accept(this);
        if (exprToConstantMap.containsKey(node.condition)) {
            node.condition = exprToConstantMap.get(node.condition);
        }
        if (node.body != null) {
            node.body.accept(this);
        }
    }

    @Override
    public void visit(AstForStatement node) {
        if (node.expr1 != null) {
            node.expr1.accept(this);
            if (exprToConstantMap.containsKey(node.expr1)) {
                node.expr1 = exprToConstantMap.get(node.expr1);
            }
        }
        if (node.expr2 != null) {
            node.expr2.accept(this);
            if (exprToConstantMap.containsKey(node.expr2)) {
                node.expr2 = exprToConstantMap.get(node.expr2);
            }
        }
        if (node.expr3 != null) {
            node.expr3.accept(this);
            if (exprToConstantMap.containsKey(node.expr3)) {
                node.expr3 = exprToConstantMap.get(node.expr3);
            }
        }
        if (node.body != null) {
            node.body.accept(this);
        }
    }

    @Override
    public void visit(AstBreakStatement node) {

    }

    @Override
    public void visit(AstContiStatement node) {

    }

    @Override
    public void visit(AstReturnStatement node) {
        if (node.value != null) {
            node.value.accept(this);
            if (exprToConstantMap.containsKey(node.value)) {
                node.value = exprToConstantMap.get(node.value);
            }
        }
    }

    @Override
    public void visit(AstEmptyStatement node) {

    }
}
