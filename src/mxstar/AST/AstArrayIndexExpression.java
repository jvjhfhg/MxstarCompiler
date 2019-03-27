package mxstar.AST;

import java.util.*;

public class AstArrayIndexExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
