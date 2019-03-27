package mxstar.AST;

import java.util.*;

public class AstIfStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
