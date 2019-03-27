package mxstar.AST;

import java.util.*;

public class AstExprStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
