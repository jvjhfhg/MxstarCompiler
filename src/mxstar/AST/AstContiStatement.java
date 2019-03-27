package mxstar.AST;

import java.util.*;

public class AstContiStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
