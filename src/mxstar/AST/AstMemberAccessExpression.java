package mxstar.AST;

import java.util.*;

public class AstMemberAccessExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
