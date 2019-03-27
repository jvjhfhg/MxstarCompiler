package mxstar.AST;

import java.util.*;

public class AstPrimaryExpression extends AstNode {
    
    
    @Override
    void accept(IAstVisitor visitor) {
        visitor.visit(this);
    }
}
