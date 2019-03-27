package mxstar.AST;

import java.util.*;

public class AstVariableDeclaration extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
