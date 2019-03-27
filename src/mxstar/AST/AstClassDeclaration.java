package mxstar.AST;

import java.util.*;

public class AstClassDeclaration extends AstDeclaration {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
