# Mxstar Compiler

## Structure of AST
```
AstNode
	AstProgram
	AstType
		AstArrayType
		AstPrimitiveType
		AstClassType
	AstDeclaration
		AstClassDeclaration
		AstFunctionDeclaration
		AstVariableDeclaration
	AstExpression
		AstPrimaryExpression
		AstMemberAccessExpression
		AstFunctionCallExpression
		AstArrayIndexExpression
		AstNewArrayExpression
		AstNewExpression
		AstUnaryExpression
		AstPostfixIncDecExpression
		AstBinaryExpression
		AstAssignmentExpression
	AstStatement
		AstBlockStatement
		AstVarDeclStatement
		AstExprStatement
		AstIfStatement
		AstWhileStatement
		AstForStatement
		AstBreakStatement
		AstContiStatement
		AstReturnStatement
		AstEmptyStatement
```


