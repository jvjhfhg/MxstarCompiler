package mxstar.AST;

import java.util.*;

public class AstBreakStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
