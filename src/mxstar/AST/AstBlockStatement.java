package mxstar.AST;

import java.util.*;

public class AstBlockStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
