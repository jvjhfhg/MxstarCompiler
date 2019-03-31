package mxstar.worker;

import mxstar.ast.*;
import mxstar.parser.MxstarBaseVisitor;
import mxstar.parser.MxstarParser.*;
import mxstar.exception.ErrorRecorder;

import java.util.*;

import static mxstar.parser.MxstarParser.*;

public class AstBuilder extends MxstarBaseVisitor<Object> {
    public AstProgram astProgram;
    public ErrorRecorder errorRecorder;

    public AstBuilder(ErrorRecorder errorRecorder) {
        this.astProgram = new AstProgram();
        this.astProgram.position = new TokenPosition(0, 0);
        this.errorRecorder = errorRecorder;
    }

    public AstProgram getAstProgram() {
        return astProgram;
    }

    @Override
    public Object visitCompilationUnit(CompilationUnitContext ctx) {
        for (GlobalDeclarationContext c : ctx.globalDeclaration()) {
            if (c.classDeclaration() != null) {
                astProgram.classes.add(visitClassDeclaration(c.classDeclaration()));
            } else if (c.functionDeclaration() != null) {
                astProgram.functions.add(visitFunctionDeclaration(c.functionDeclaration()));
            } else { //
                astProgram.variables.addAll(visitVariableDeclaration(c.variableDeclaration()));
            }
        }
        return null;
    }

    @Override
    public AstClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
        AstClassDeclaration astClassDeclaration = new AstClassDeclaration();

        astClassDeclaration.position = new TokenPosition(ctx);
        astClassDeclaration.name = ctx.Identifier().getSymbol().getText();

        List<ClassBodyDeclarationContext> cbdCtx = ctx.classBody().classBodyDeclaration();

        if (cbdCtx != null) {
            // Constructor
            for (ClassBodyDeclarationContext c : cbdCtx) {
                if (c.constructorDeclaration() != null) {
                    if (astClassDeclaration.constructor == null) {
                        astClassDeclaration.constructor = visitConstructorDeclaration(c.constructorDeclaration());
                    } else {
                        errorRecorder.add(new TokenPosition(c), "class cannot have more than one constructor");
                    }
                }
            }

            // Fields
            for (ClassBodyDeclarationContext c : cbdCtx) {
                if (c.variableDeclaration() != null) {
                    astClassDeclaration.fields.addAll(visitVariableDeclaration(c.variableDeclaration()));
                }
            }

            // Methods
            for (ClassBodyDeclarationContext c : cbdCtx) {
                if (c.functionDeclaration() != null) {
                    AstFunctionDeclaration tmp = visitFunctionDeclaration(c.functionDeclaration());
                    if (tmp.name.equals(astClassDeclaration.name)) {
                        errorRecorder.add(new TokenPosition(c), "constructor cannot have return value");
                    }
                    astClassDeclaration.methods.add(tmp);
                }
            }
        }

        return astClassDeclaration;
    }

    @Override
    public AstFunctionDeclaration visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        AstFunctionDeclaration astFunctionDeclaration = new AstFunctionDeclaration();

        astFunctionDeclaration.position = new TokenPosition(ctx);
        astFunctionDeclaration.name = ctx.Identifier().getSymbol().getText();
        astFunctionDeclaration.returnType = new AstPrimitiveType("void");
        if (ctx.parameterList() != null) {
            astFunctionDeclaration.parameters = visitParameterList(ctx.parameterList());
        }
        astFunctionDeclaration.body = visitFunctionBody(ctx.functionBody());

        return astFunctionDeclaration;
    }

    @Override
    public AstFunctionDeclaration visitFunctionDeclaration(FunctionDeclarationContext ctx) {
        AstFunctionDeclaration astFunctionDeclaration = new AstFunctionDeclaration();

        astFunctionDeclaration.position = new TokenPosition(ctx);
        astFunctionDeclaration.name = ctx.Identifier().getSymbol().getText();
        astFunctionDeclaration.returnType = visitType(ctx.type());
        if (ctx.parameterList() != null) {
            astFunctionDeclaration.parameters = visitParameterList(ctx.parameterList());
        }
        astFunctionDeclaration.body = visitFunctionBody(ctx.functionBody());

        return astFunctionDeclaration;
    }

    @Override
    public List<AstVariableDeclaration> visitParameterList(ParameterListContext ctx) {
        List<AstVariableDeclaration> parameterList = new LinkedList<>();

        for (ParameterContext c : ctx.parameter()) {
            AstVariableDeclaration parameter = new AstVariableDeclaration();

            parameter.position = new TokenPosition(c);
            parameter.name = c.Identifier().getSymbol().getText();
            parameter.type = visitType(c.type());
        }

        return parameterList;
    }

    @Override
    public List<AstStatement> visitFunctionBody(FunctionBodyContext ctx) {
        return visitStatementList(ctx.statementList());
    }

    @Override
    public List<AstStatement> visitStatementList(StatementListContext ctx) {
        List<AstStatement> statementList = new LinkedList<>();

        if (ctx.statement() != null) {
            for (StatementContext c : ctx.statement()) {
                if (c instanceof VarDeclStatementContext) {
                    statementList.addAll(visitVarDeclStatement((VarDeclStatementContext) c));
                } else {
                    statementList.add((AstStatement) c.accept(this));
                }
            }
        }

        return statementList;
    }

    @Override
    public List<AstStatement> visitVarDeclStatement(VarDeclStatementContext ctx) {
        List<AstStatement> statementList = new LinkedList<>();

        List<AstVariableDeclaration> declarationList = visitVariableDeclaration(ctx.variableDeclaration());
        for (AstVariableDeclaration d : declarationList) {
            AstVarDeclStatement statement = new AstVarDeclStatement();

            statement.position = d.position;
            statement.declaration = d;

            statementList.add(statement);
        }

        return statementList;
    }

    @Override
    public AstType visitType(TypeContext ctx) {
        if (ctx.empty().isEmpty()) {
            return visitAtomType(ctx.atomType());
        } else {
            AstArrayType arrayType = new AstArrayType();

            arrayType.position = new TokenPosition(ctx);
            arrayType.baseType = visitAtomType(ctx.atomType());
            arrayType.dimension = ctx.empty().size();

            return arrayType;
        }
    }

    @Override
    public AstType visitAtomType(AtomTypeContext ctx) {
        if (ctx.STRING() != null) {
            return new AstClassType("string");
        } else if (ctx.Identifier() != null) {
            return new AstClassType(ctx.Identifier().getSymbol().getText());
        } else {
            return new AstPrimitiveType(ctx.getText());
        }
    }

    @Override
    public List<AstVariableDeclaration> visitVariableDeclaration(VariableDeclarationContext ctx) {
        List<AstVariableDeclaration> variableList = new LinkedList<>();

        AstType type = visitType(ctx.type());
        for (VariableDeclaratorContext c : ctx.variableDeclarators().variableDeclarator()) {
            AstVariableDeclaration variable = visitVariableDeclarator(c);
            variable.type = type;
            variableList.add(variable);
        }

        return variableList;
    }

    @Override
    public AstVariableDeclaration visitVariableDeclarator(VariableDeclaratorContext ctx) {
        AstVariableDeclaration variable = new AstVariableDeclaration();

        variable.position = new TokenPosition(ctx);
        variable.name = ctx.Identifier().getSymbol().getText();
        if (ctx.expression() != null) {
            variable.initValue = (AstExpression) ctx.expression().accept(this);
        }

        return variable;
    }

    @Override
    public AstExpression visitPrimaryExpression(PrimaryExpressionContext ctx) {
        if (ctx.token == null) {
            return (AstExpression) ctx.expression().accept(this);
        } else if (ctx.token.getType() == Identifier || ctx.token.getType() == THIS) {
            return new AstIdentifierExpression(ctx.token);
        } else {
            return new AstLiteralExpression(ctx.token);
        }
    }

    @Override
    public AstMemberAccessExpression visitMemberAccessExpression(MemberAccessExpressionContext ctx) {
        AstMemberAccessExpression memberAccessExpression = new AstMemberAccessExpression();

        memberAccessExpression.position = new TokenPosition(ctx);
        memberAccessExpression.object = (AstExpression) ctx.expression().accept(this);
        if (ctx.Identifier() != null) {
            memberAccessExpression.fieldAccess = new AstIdentifierExpression(ctx.Identifier().getSymbol());
        } else {
            memberAccessExpression.methodCall = visitFunctionCall(ctx.functionCall());
        }

        return memberAccessExpression;
    }

    @Override
    public AstFunctionCallExpression visitFunctionCall(FunctionCallContext ctx) {
        AstFunctionCallExpression astFunctionCallExpression = new AstFunctionCallExpression();

        astFunctionCallExpression.position = new TokenPosition(ctx);
        astFunctionCallExpression.name = ctx.Identifier().getSymbol().getText();
        if (ctx.expressionList() != null) {
            for (ExpressionContext c : ctx.expressionList().expression()) {
                astFunctionCallExpression.arguments.add((AstExpression) c.accept(this));
            }
        }

        return astFunctionCallExpression;
    }

    @Override
    public AstFunctionCallExpression visitFunctionCallExpression(FunctionCallExpressionContext ctx) {
        return visitFunctionCall(ctx.functionCall());
    }

    @Override
    public AstArrayIndexExpression visitArrayIndexExpression(ArrayIndexExpressionContext ctx) {
        AstArrayIndexExpression astArrayIndexExpression = new AstArrayIndexExpression();

        astArrayIndexExpression.position = new TokenPosition(ctx);
        astArrayIndexExpression.address = (AstExpression) ctx.expression(0).accept(this);
        if (astArrayIndexExpression.address instanceof AstNewArrayExpression && ctx.expression(0).stop.getText().equals("]")) {
            errorRecorder.add(astArrayIndexExpression.address.position, "cannot use \"new a[n][i]\" to express \"(new a[n])[i]\"");
        }
        astArrayIndexExpression.index = (AstExpression) ctx.expression(1).accept(this);

        return astArrayIndexExpression;
    }

    @Override
    public AstNewArrayExpression visitNewArrayExpression(NewArrayExpressionContext ctx) {
        AstNewArrayExpression astNewArrayExpression = new AstNewArrayExpression();

        astNewArrayExpression.position = new TokenPosition(ctx);
        astNewArrayExpression.baseType = visitAtomType(ctx.atomType());
        if (ctx.expression() != null) {
            for (ExpressionContext c : ctx.expression()) {
                astNewArrayExpression.indexes.add((AstExpression) c.accept(this));
            }
        }
        if (ctx.empty() != null) {
            astNewArrayExpression.emptyDimCnt = ctx.empty().size();
        } else {
            astNewArrayExpression.emptyDimCnt = 0;
        }

        return astNewArrayExpression;
    }

    @Override
    public AstNewExpression visitNewExpression(NewExpressionContext ctx) {
        AstNewExpression astNewExpression = new AstNewExpression();

        astNewExpression.position = new TokenPosition(ctx);
        astNewExpression.baseType = visitAtomType(ctx.atomType());
        if (ctx.expressionList() != null) {
            for (ExpressionContext c : ctx.expressionList().expression()) {
                astNewExpression.arguments.add((AstExpression) c.accept(this));
            }
        }

        return astNewExpression;
    }

    @Override
    public AstUnaryExpression visitUnaryExpression(UnaryExpressionContext ctx) {
        AstUnaryExpression astUnaryExpression = new AstUnaryExpression();

        astUnaryExpression.position = new TokenPosition(ctx);
        astUnaryExpression.opt = ctx.opt.getText();
        astUnaryExpression.expr = (AstExpression) ctx.expression().accept(this);

        return astUnaryExpression;
    }

    @Override
    public AstPostfixIncDecExpression visitPostfixIncDecExpression(PostfixIncDecExpressionContext ctx) {
        AstPostfixIncDecExpression astPostfixIncDecExpression = new AstPostfixIncDecExpression();

        astPostfixIncDecExpression.position = new TokenPosition(ctx);
        astPostfixIncDecExpression.opt = ctx.opt.getText();
        astPostfixIncDecExpression.expr = (AstExpression) ctx.expression().accept(this);

        return astPostfixIncDecExpression;
    }

    @Override
    public AstBinaryExpression visitBinaryExpression(BinaryExpressionContext ctx) {
        AstBinaryExpression astBinaryExpression = new AstBinaryExpression();

        astBinaryExpression.position = new TokenPosition(ctx);
        astBinaryExpression.opt = ctx.opt.getText();
        astBinaryExpression.expr1 = (AstExpression) ctx.expression(0).accept(this);
        astBinaryExpression.expr2 = (AstExpression) ctx.expression(1).accept(this);

        return astBinaryExpression;
    }

    @Override
    public AstAssignmentExpression visitAssignmentExpression(AssignmentExpressionContext ctx) {
        AstAssignmentExpression astAssignmentExpression = new AstAssignmentExpression();

        astAssignmentExpression.position = new TokenPosition(ctx);
        astAssignmentExpression.expr1 = (AstExpression) ctx.expression(0).accept(this);
        astAssignmentExpression.expr2 = (AstExpression) ctx.expression(1).accept(this);

        return astAssignmentExpression;
    }

    @Override
    public AstBlockStatement visitBlockStatement(BlockStatementContext ctx) {
        AstBlockStatement blockStatement = new AstBlockStatement();
        blockStatement.position = new TokenPosition(ctx);
        blockStatement.statements = visitStatementList(ctx.statementList());
        return blockStatement;
    }

    @Override
    public AstBreakStatement visitBreakStatement(BreakStatementContext ctx) {
        AstBreakStatement breakStatement = new AstBreakStatement();
        breakStatement.position = new TokenPosition(ctx);
        return breakStatement;
    }

    @Override
    public AstContiStatement visitContiStatement(ContiStatementContext ctx) {
        AstContiStatement contiStatement = new AstContiStatement();
        contiStatement.position = new TokenPosition(ctx);
        return contiStatement;
    }

    @Override
    public AstEmptyStatement visitEmptyStatement(EmptyStatementContext ctx) {
        AstEmptyStatement emptyStatement = new AstEmptyStatement();
        emptyStatement.position = new TokenPosition(ctx);
        return emptyStatement;
    }

    @Override
    public AstExprStatement visitExprStatement(ExprStatementContext ctx) {
        AstExprStatement exprStatement = new AstExprStatement();
        exprStatement.position = new TokenPosition(ctx);
        exprStatement.expr = (AstExpression) ctx.expression().accept(this);
        return exprStatement;
    }

    @Override
    public AstIfStatement visitIfStatement(IfStatementContext ctx) {
        AstIfStatement ifStatement = new AstIfStatement();
        ifStatement.position = new TokenPosition(ctx);
        ifStatement.condition = (AstExpression) ctx.expression().accept(this);
        ifStatement.ifBody = (AstStatement) ctx.statement(0).accept(this);
        if (ctx.statement(1) != null) {
            ifStatement.elseBody = (AstStatement) ctx.statement(1).accept(this);
        }
        return ifStatement;
    }

    @Override
    public AstForStatement visitForStatement(ForStatementContext ctx) {
        AstForStatement forStatement = new AstForStatement();
        forStatement.position = new TokenPosition(ctx);
        if (ctx.expression(0) != null) {
            forStatement.expr1 = (AstExpression) ctx.expression(0).accept(this);
        }
        if (ctx.expression(1) != null) {
            forStatement.expr2 = (AstExpression) ctx.expression(1).accept(this);
        }
        if (ctx.expression(2) != null) {
            forStatement.expr3 = (AstExpression) ctx.expression(2).accept(this);
        }
        forStatement.body = (AstStatement) ctx.statement().accept(this);
        return forStatement;
    }

    @Override
    public AstWhileStatement visitWhileStatement(WhileStatementContext ctx) {
        AstWhileStatement whileStatement = new AstWhileStatement();
        whileStatement.position = new TokenPosition(ctx);
        whileStatement.condition = (AstExpression) ctx.expression().accept(this);
        whileStatement.body = (AstStatement) ctx.statement().accept(this);
        return whileStatement;
    }

    @Override
    public Object visitReturnStatement(ReturnStatementContext ctx) {
        AstReturnStatement returnStatement = new AstReturnStatement();
        returnStatement.position = new TokenPosition(ctx);
        if (ctx.expression() != null) {
            returnStatement.value = (AstExpression) ctx.expression().accept(this);
        }
        return returnStatement;
    }
}
