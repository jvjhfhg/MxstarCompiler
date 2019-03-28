grammar Mxstar;

compilationUnit
    :   globalDeclaration* EOF
    ;

globalDeclaration
    :   classDeclaration
    |   functionDeclaration
    |   variableDeclaration ';'
    ;

classDeclaration
    :   CLASS Identifier classBody
    ;

classBody
    :   '{' classBodyDeclaration* '}'
    ;

classBodyDeclaration
    :   constructorDeclaration
    |   functionDeclaration
    |   variableDeclaration ';'
    ;

constructorDeclaration
    :   Identifier '(' parameterList? ')' functionBody
    ;

functionDeclaration
    :   type Identifier '(' parameterList? ')' functionBody
    ;

parameterList
    :   parameter (',' parameter)*
    ;

parameter
    :   type Identifier
    ;

functionBody
    :   '{' statementList '}'
    ;

statementList
    :   statement*
    ;

statement
    :   '{' statementList '}'
        # BlockStatement
    |   variableDeclaration ';'
        # VarDeclStatement
    |   expression ';'
        # ExprStatement
    |   IF '(' expression ')' statement (ELSE statement)?
        # IfStatement
    |   WHILE '(' expression ')' statement
        # WhileStatement
    |   FOR '(' expression? ';' expression? ';' expression? ')' statement
        # ForStatement
    |   BREAK ';'
        # BreakStatment
    |   CONTINUE ';'
        # ContiStatement
    |   RETURN expression? ';'
        # ReturnStatement
    |   ';'
        # EmptyStatement
    ;

variableDeclaration
    :   type variableDeclarators
    ;

variableDeclarators
    :   variableDeclarator (',' variableDeclarator)?
    ;

variableDeclarator
    :   Identifier ('=' expression)?
    ;

type
    :   atomType ('[' empty ']')*
    ;

atomType
    :   INT
    |   BOOL
    |   VOID
    |   STRING
    |   Identifier
    ;

functionCall
    :   Identifier '(' expressionList? ')'
    ;

expressionList
    :   expression (',' expression)*
    ;

expression
    :   '(' expression ')'
        # PrimaryExpression
    |   token = THIS
        # PrimaryExpression
    |   token = IntegralLiteral
        # PrimaryExpression
    |   token = StringLiteral
        # PrimaryExpression
    |   token = BoolLiteral
        # PrimaryExpression
    |   token = NullLiteral
        # PrimaryExpression
    |   token = Identifier
        # PrimaryExpression
    |   expression opt = '.' (Identifier | functionCall)
        # MemberAccessExpression
    |   functionCall
        # FunctionCallExpression
    |   expression '[' expression ']'
        # ArrayIndexExpression
    |   NEW atomType ('[' expression ']')* ('[' empty ']')*
        # NewArrayExpression
    |   NEW atomType '(' expressionList? ')'
        # NewExpression
    |   <assoc = right> opt = ('++' | '--' | '+' | '-' | '!' | '~') expression
        # UnaryExpression
    |   expression opt = ('++' | '--')
        # PostfixIncDecExpression
    |   expression opt = ('*' | '/' | '%') expression
        # BinaryExpression
    |   expression opt = ('+' | '-') expression
        # BinaryExpression
    |   expression opt = ('<<' | '>>') expression
        # BinaryExpression
    |   expression opt = ('<' | '>' | '<=' | '>=') expression
        # BinaryExpression
    |   expression opt = ('==' | '!=') expression
        # BinaryExpression
    |   expression opt = '&' expression
        # BinaryExpression
    |   expression opt = '^' expression
        # BinaryExpression
    |   expression opt = '|' expression
        # BinaryExpression
    |   expression opt = '&&' expression
        # BinaryExpression
    |   expression opt = '||' expression
        # BinaryExpression
    |   <assoc = right> expression opt = '=' expression
        # AssignmentExpression
    ;

empty   :   ;

// reserved keywords
BOOL    :   'bool';
INT     :   'int';
STRING  :   'string';
VOID    :   'void';
IF      :   'if';
ELSE    :   'else';
FOR     :   'for';
WHILE   :   'while';
BREAK   :   'break';
CONTINUE:   'continue';
RETURN  :   'return';
NEW     :   'new';
CLASS   :   'class';
THIS    :   'this';

BoolLiteral :   'true' | 'false';
NullLiteral :   'null';

IntegralLiteral
    :   Digit
    |   NonZeroDigit Digit*
    ;

fragment Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment NonZeroDigit
    :   [1-9]
    ;

StringLiteral
    :   '"' ('\\n' | '\\\\' | '\\"' | .)*? '"'
    ;

Identifier
    :   [a-zA-Z] [a-zA-Z0-9_]*
    ;

WhiteSpaces
    :   [ \t\r\n]+ -> skip
    ;

BlockComment
    :   '/*' .*? '*/' -> skip
    ;

LineComment
    :   '//' ~[\r\n]* -> skip
    ;
