package mxstar.AST;

import java.util.*;

public class AstProgram extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
