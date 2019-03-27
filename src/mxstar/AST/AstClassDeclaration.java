package mxstar.AST;

import java.util.*;

public class AstClassDeclaration extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
