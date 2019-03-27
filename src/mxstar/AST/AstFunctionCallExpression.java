package mxstar.AST;

import java.util.*;

public class AstFunctionCallExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
