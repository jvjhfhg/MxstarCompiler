package mxstar.AST;

import java.util.*;

public class AstDeclaration extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
