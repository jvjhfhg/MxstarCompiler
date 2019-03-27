package mxstar.AST;

import java.util.*;

public class AstFunctionDeclaration extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
