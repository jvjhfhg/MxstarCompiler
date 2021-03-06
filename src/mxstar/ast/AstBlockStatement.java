package mxstar.ast;

import java.util.*;

public class AstBlockStatement extends AstStatement {
    public List<AstStatement> statements;

    public AstBlockStatement() {
        statements = new LinkedList<>();
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
