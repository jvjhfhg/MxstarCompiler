package mxstar.AST;

import java.util.*;

public class AstReturnStatement extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
