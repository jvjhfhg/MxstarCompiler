package mxstar.AST;

import java.util.*;

public class AstWhileStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
