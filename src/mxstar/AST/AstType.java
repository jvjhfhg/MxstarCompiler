package mxstar.AST;

import java.util.*;

public class AstType extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
