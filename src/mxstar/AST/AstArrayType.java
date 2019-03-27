package mxstar.AST;

import java.util.*;

public class AstArrayType extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
