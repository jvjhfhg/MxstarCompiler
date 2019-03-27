package mxstar.AST;

import java.util.*;

public class AstEmptyStatemen extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
