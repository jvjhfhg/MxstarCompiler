# Mxstar Compiler

Compiler 2019 Project

## Abstract Syntax Tree
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
		AstLiteralExpression
		AstIdentifierExpression
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

## Symbol Table

```
StBaseSymbol
	StPrimitiveSymbol
	StClassSymbol
StVariableSymbol
StFunctionSymbol
StType
	StPrimitiveType
	StClassType
	StArrayType
StSymbolTable
	StGlobalTable
```

## Intermediate Representation

```
IrProgram
IrOperand

IrInstruction

IrBasicBlock
IrFunction
IrRegisterSet
```

