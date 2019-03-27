package mxstar.AST;

import java.util.*;

public class AstNewExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
