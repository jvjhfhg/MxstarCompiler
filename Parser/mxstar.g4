grammar mxstar;

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
    |   variableDeclaration ';'
    |   expression ';'
    |   IF '(' expression ')' statement (ELSE statement)?
    |   WHILE '(' expression ')' statement
    |   FOR '(' expression? ';' expression? ';' expression? ')' statement
    |   BREAK ';'
    |   CONTINUE ';'
    |   RETURN expression? ';'
    |   ';'
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
    |   THIS
    |   IntegralLiteral
    |   StringLiteral
    |   BoolLiteral
    |   NullLiteral
    |   Identifier
    |   expression '.' (Identifier | functionCall)
    |   functionCall
    |   expression '[' expression ']'
    |   NEW atomType ('[' expression ']')* ('[' empty ']')*
    |   NEW atomType '(' expressionList? ')'
    |   ('++' | '--' | '-' | '!' | '~') expression
    |   expression ('++' | '--')
    |   expression ('*' | '/' | '%') expression
    |   expression ('+' | '-') expression
    |   expression ('<<' | '>>') expression
    |   expression ('<' | '>' | '<=' | '>=') expression
    |   expression ('==' | '!=') expression
    |   expression '&' expression
    |   expression '^' expression
    |   expression '|' expression
    |   expression '&&' expression
    |   expression '||' expression
    |   expression '=' expression
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
