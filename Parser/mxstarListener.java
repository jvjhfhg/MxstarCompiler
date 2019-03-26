// Generated from mxstar.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link mxstarParser}.
 */
public interface mxstarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link mxstarParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(mxstarParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(mxstarParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#globalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobalDeclaration(mxstarParser.GlobalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#globalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobalDeclaration(mxstarParser.GlobalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(mxstarParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(mxstarParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(mxstarParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(mxstarParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(mxstarParser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(mxstarParser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(mxstarParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(mxstarParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(mxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(mxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(mxstarParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(mxstarParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(mxstarParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(mxstarParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(mxstarParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(mxstarParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(mxstarParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(mxstarParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(mxstarParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(mxstarParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(mxstarParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(mxstarParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(mxstarParser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(mxstarParser.VariableDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(mxstarParser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(mxstarParser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(mxstarParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(mxstarParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#atomType}.
	 * @param ctx the parse tree
	 */
	void enterAtomType(mxstarParser.AtomTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#atomType}.
	 * @param ctx the parse tree
	 */
	void exitAtomType(mxstarParser.AtomTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(mxstarParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(mxstarParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(mxstarParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(mxstarParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(mxstarParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(mxstarParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxstarParser#empty}.
	 * @param ctx the parse tree
	 */
	void enterEmpty(mxstarParser.EmptyContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxstarParser#empty}.
	 * @param ctx the parse tree
	 */
	void exitEmpty(mxstarParser.EmptyContext ctx);
}