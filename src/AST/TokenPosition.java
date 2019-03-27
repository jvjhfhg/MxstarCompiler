package mxstar.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class TokenPosition {
    public final int row;
    public final int column;
    
    public TokenPosition(int row, int column) {
        this.row = rot;
        this.column = column;
    }
    
    public TokenPosition(Token token) {
        row = token.getLine();
        column = token.getCharPositionInLine();
    }
    
    public TokenPosition(ParserRuleContext ctx) {
        this(ctx.start);
    }
    
    public TokenPosition(ASTNode node) {
        this.row = node.position.row;
        this.column = node.position.column;
    }
    
    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
