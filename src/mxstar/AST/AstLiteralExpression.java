package mxstar.AST;

import org.antlr.v4.runtime.Token;

import static mxstar.parser.MxstarParser.*;

public class AstLiteralExpression extends AstExpression {
    public String value;

    public AstLiteralExpression(Token token) {
        position = new TokenPosition(token);
        switch (token.getType()) {
            case IntegralLiteral:
                typeNAme
                break;
            case BoolLiteral:

                break;
            case NullLiteral:

                break;
            default: // StringLiteral

        }
    }
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
