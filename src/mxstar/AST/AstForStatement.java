package mxstar.AST;

import java.util.*;

public class AstForStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
