package mxstar.AST;

import org.antlr.v4.runtime.Token;

import static mxstar.parser.MxstarParser.*;

public class AstLiteralExpression extends AstExpression {
    public String value;

    public AstLiteralExpression(Token token) {
        position = new TokenPosition(token);
        switch (token.getType()) {
            case IntegralLiteral:
                type = new AstPrimitiveType("int");
                value = token.getText();
                break;
            case BoolLiteral:
                type = new AstPrimitiveType("bool");
                value = token.getText();
                break;
            case NullLiteral:
                type = new AstPrimitiveType("null");
                value = token.getText();
                break;
            default: // StringLiteral
                type = new AstClassType("string");
                value = escape(token.getText());
        }
    }

    private String escape(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '\\') {
                char cc = string.charAt(i + 1);
                switch (cc) {
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    case '"':
                        stringBuilder.append('"');
                        break;
                    default:
                        stringBuilder.append(c);
                        stringBuilder.append(cc);
                }
                ++i;
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
