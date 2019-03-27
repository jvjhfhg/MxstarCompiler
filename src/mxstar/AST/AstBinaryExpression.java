package mxstar.AST;

import java.util.*;

public class AstBinaryExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
