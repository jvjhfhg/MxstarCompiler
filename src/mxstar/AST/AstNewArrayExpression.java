package mxstar.AST;

import java.util.*;

public class AstNewArrayExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
