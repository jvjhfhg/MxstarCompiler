// Generated from Mxstar.g4 by ANTLR 4.7.2

package mxstar.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxstarParser}.
 */
public interface MxstarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxstarParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(MxstarParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(MxstarParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#globalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobalDeclaration(MxstarParser.GlobalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#globalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobalDeclaration(MxstarParser.GlobalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(MxstarParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(MxstarParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(MxstarParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(MxstarParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(MxstarParser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(MxstarParser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(MxstarParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(MxstarParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MxstarParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MxstarParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(MxstarParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(MxstarParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(MxstarParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(MxstarParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(MxstarParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(MxstarParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlockStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(MxstarParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlockStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(MxstarParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarDeclStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclStatement(MxstarParser.VarDeclStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarDeclStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclStatement(MxstarParser.VarDeclStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStatement(MxstarParser.ExprStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStatement(MxstarParser.ExprStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(MxstarParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(MxstarParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(MxstarParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(MxstarParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(MxstarParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(MxstarParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BreakStatment}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatment(MxstarParser.BreakStatmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BreakStatment}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatment(MxstarParser.BreakStatmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContiStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContiStatement(MxstarParser.ContiStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContiStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContiStatement(MxstarParser.ContiStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(MxstarParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(MxstarParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EmptyStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStatement(MxstarParser.EmptyStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EmptyStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStatement(MxstarParser.EmptyStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(MxstarParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(MxstarParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(MxstarParser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(MxstarParser.VariableDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(MxstarParser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(MxstarParser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MxstarParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MxstarParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#atomType}.
	 * @param ctx the parse tree
	 */
	void enterAtomType(MxstarParser.AtomTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#atomType}.
	 * @param ctx the parse tree
	 */
	void exitAtomType(MxstarParser.AtomTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(MxstarParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(MxstarParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(MxstarParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(MxstarParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberAccessExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccessExpression(MxstarParser.MemberAccessExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberAccessExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccessExpression(MxstarParser.MemberAccessExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayIndexExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayIndexExpression(MxstarParser.ArrayIndexExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayIndexExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayIndexExpression(MxstarParser.ArrayIndexExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpression(MxstarParser.BinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpression(MxstarParser.BinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(MxstarParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(MxstarParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpression(MxstarParser.NewExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpression(MxstarParser.NewExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(MxstarParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(MxstarParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewArrayExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewArrayExpression(MxstarParser.NewArrayExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewArrayExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewArrayExpression(MxstarParser.NewArrayExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCallExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallExpression(MxstarParser.FunctionCallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCallExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallExpression(MxstarParser.FunctionCallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PosifixIncDecExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPosifixIncDecExpression(MxstarParser.PosifixIncDecExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PosifixIncDecExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPosifixIncDecExpression(MxstarParser.PosifixIncDecExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#empty}.
	 * @param ctx the parse tree
	 */
	void enterEmpty(MxstarParser.EmptyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#empty}.
	 * @param ctx the parse tree
	 */
	void exitEmpty(MxstarParser.EmptyContext ctx);
}
