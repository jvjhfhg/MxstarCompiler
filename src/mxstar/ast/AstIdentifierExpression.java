package mxstar.ast;

import mxstar.symbol.StVariableSymbol;
import org.antlr.v4.runtime.Token;

public class AstIdentifierExpression extends AstExpression {
    public String name;

    public StVariableSymbol symbol;

    public AstIdentifierExpression(Token token) {
        if (token != null) {
            this.position = new TokenPosition(token);
            this.name = token.getText();
        }
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
