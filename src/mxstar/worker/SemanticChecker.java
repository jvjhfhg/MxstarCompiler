package mxstar.worker;

import mxstar.ast.*;
import mxstar.symbol.*;
import mxstar.exception.ErrorRecorder;

public class SemanticChecker implements IAstVisitor {
    ErrorRecorder errorRecorder;
    StGlobalTable globalTable;
    StFunctionSymbol currentFunction;
    int loopCount;

    public SemanticChecker(ErrorRecorder errorRecorder, StGlobalTable globalTable) {
        this.errorRecorder = errorRecorder;
        this.globalTable = globalTable;
        this.loopCount = 0;
    }

    @Override
    public void visit(AstType node) {
        assert false;
    }

    @Override
    public void visit(AstProgram node) {
        for (AstVariableDeclaration d : node.variables) {
            d.accept(this);
        }
        for (AstFunctionDeclaration d : node.functions) {
            d.accept(this);
        }
        for (AstClassDeclaration d : node.classes) {
            d.accept(this);
        }
        StFunctionSymbol mainFunc = globalTable.getFunctionSymbol("main");
        if (mainFunc == null) {
            errorRecorder.add(node.position, "main was not found");
        } else {
            if (mainFunc.returnType instanceof StPrimitiveType && ((StPrimitiveType) mainFunc.returnType).name.equals("int")) {
                if (mainFunc.parameterTypes.size() > 0) {
                    errorRecorder.add(node.position, "main should not have any parameter");
                }
            } else {
                errorRecorder.add(node.position, "main must return int");
            }
        }
    }

    @Override
    public void visit(AstArrayType node) {

    }

    @Override
    public void visit(AstClassType node) {

    }

    @Override
    public void visit(AstStatement node) {
        assert false;
    }

    @Override
    public void visit(AstExpression node) {
        assert false;
    }

    @Override
    public void visit(AstDeclaration node) {
        assert false;
    }

    @Override
    public void visit(AstIfStatement node) {
        node.condition.accept(this);
        checkIsBoolean(node.condition);
        node.ifBody.accept(this);
        if (node.elseBody != null) {
            node.elseBody.accept(this);
        }
    }

    private void checkIsBoolean(AstExpression expression) {
        if (expression.valueType instanceof StPrimitiveType && ((StPrimitiveType) expression.valueType).name.equals("bool")) {
            return;
        } else {
            errorRecorder.add(expression.position, "condition must be boolean expression");
        }
    }

    @Override
    public void visit(AstForStatement node) {
        if (node.expr1 != null) {
            node.expr1.accept(this);
        }
        if (node.expr2 != null) {
            node.expr2.accept(this);
            checkIsBoolean(node.expr2);
        }
        if (node.expr3 != null) {
            node.expr3.accept(this);
        }
        ++loopCount;
        node.body.accept(this);
        --loopCount;
    }

    @Override
    public void visit(AstExprStatement node) {
        node.expr.accept(this);
    }

    @Override
    public void visit(AstNewExpression node) {
        node.mutable = true;
    }

    @Override
    public void visit(AstPrimitiveType node) {

    }

    @Override
    public void visit(AstBlockStatement node) {
        for (AstStatement s : node.statements)
            s.accept(this);
    }

    @Override
    public void visit(AstBreakStatement node) {
        if (loopCount == 0) {
            errorRecorder.add(node.position, "invalid break");
        }
    }

    @Override
    public void visit(AstContiStatement node) {
        if (loopCount == 0) {
            errorRecorder.add(node.position, "invalid continue");
        }
    }

    @Override
    public void visit(AstEmptyStatement node) {

    }

    @Override
    public void visit(AstWhileStatement node) {
        node.condition.accept(this);
        checkIsBoolean(node.condition);
        ++loopCount;
        node.body.accept(this);
        --loopCount;
    }

    @Override
    public void visit(AstReturnStatement node) {
        StType returnType = currentFunction.returnType;
        StPrimitiveType voidType = new StPrimitiveType("void", globalTable.getPrimitiveSymbol("void"));
        if (returnType.match(voidType) && node.value != null) {
            errorRecorder.add(node.position, "void function should not have return value");
        }
        StType returnValueType;
        if (node.value != null) {
            returnValueType = node.value.valueType;
        } else {
            returnValueType = voidType;
        }
        if (!returnValueType.match(returnType)) {
            errorRecorder.add(node.position, "mismatched return type");
        }
    }

    private boolean isStringType(StType type) {
        return type instanceof StClassType && ((StClassType) type).name.equals("string");
    }

    private boolean isIntType(StType type) {
        return type instanceof StPrimitiveType && ((StPrimitiveType) type).name.equals("int");
    }

    private boolean isBoolType(StType type) {
        return type instanceof StPrimitiveType && ((StPrimitiveType) type).name.equals("bool");
    }

    @Override
    public void visit(AstUnaryExpression node) {
        node.expr.accept(this);
        boolean mutableError = false;
        boolean typeError = false;
        boolean isInt = isIntType(node.valueType);
        boolean isBool = isBoolType(node.valueType);
        switch (node.opt) {
            case "++":
            case "--":
                if (!node.mutable) {
                    mutableError = true;
                }
                if (!isInt) {
                    typeError = true;
                }
                break;
            case "+":
            case "-":
            case "~":
                if (!isInt) {
                    typeError = true;
                }
                node.mutable = false;
                break;
            case "!":
                if (!isBool) {
                    typeError = true;
                }
                node.mutable = false;
                break;
            default:
                assert false;
        }
        if (typeError) {
            errorRecorder.add(node.position, "mismatched type");
        } else if (mutableError) {
            errorRecorder.add(node.position, "try to change a unmodifiable value");
        }
    }

    @Override
    public void visit(AstBinaryExpression node) {
        node.expr1.accept(this);
        node.expr2.accept(this);
        if (!node.expr1.valueType.match(node.expr2.valueType)) {
            errorRecorder.add(node.position, "mismatched type");
        } else {
            boolean typeError = false;
            boolean isInt = isIntType(node.expr1.valueType);
            boolean isBool = isBoolType(node.expr1.valueType);
            boolean isString = isStringType(node.expr1.valueType);
            switch (node.opt) {
                case "-":
                case "*":
                case "/":
                case "%":
                case "<<":
                case ">>":
                case "&":
                case "|":
                case "^":
                    if (!isInt) {
                        typeError = true;
                    }
                    break;
                case "<":
                case ">":
                case "<=":
                case ">=":
                case "+":
                    if (!isInt && !isString) {
                        typeError = true;
                    }
                    break;
                case "&&":
                case "||":
                    if (!isBool) {
                        typeError = true;
                    }
                    break;
                case "==":
                case "!=":
                    break;
                default:
                    assert false;
            }
            if (typeError) {
                errorRecorder.add(node.position, "mismatched type");
            }
        }
        node.mutable = false;
    }

    @Override
    public void visit(AstClassDeclaration node) {
        node.constructor.accept(this);
        for (AstFunctionDeclaration d : node.methods) {
            d.accept(this);
        }
        if (!node.constructor.name.equals(node.name)) {
            errorRecorder.add(node.position, "mismatched constructor name");
        }
    }

    @Override
    public void visit(AstVarDeclStatement node) {
        node.declaration.accept(this);
    }

    @Override
    public void visit(AstLiteralExpression node) {
        node.mutable = false;
    }

    @Override
    public void visit(AstNewArrayExpression node) {
        for (AstExpression e : node.indexes) {
            e.accept(this);
        }
        node.mutable = true;
    }

    @Override
    public void visit(AstFunctionDeclaration node) {
        currentFunction = node.symbol;
        for (AstStatement s : node.body) {
            s.accept(this);
        }
    }

    @Override
    public void visit(AstVariableDeclaration node) {
        if (node.initValue != null) {
            if (!node.symbol.type.match(node.initValue.valueType)) {
                errorRecorder.add(node.position, "mismatched type");
            }
        }
    }

    @Override
    public void visit(AstArrayIndexExpression node) {
        node.address.accept(this);
        node.index.accept(this);
        node.mutable = true;
    }

    @Override
    public void visit(AstAssignmentExpression node) {
        node.expr1.accept(this);
        node.expr2.accept(this);
        if (!node.expr1.valueType.match(node.expr2.valueType)) {
            errorRecorder.add(node.position, "mismatched type");
        } else if (!node.expr1.mutable) {
            errorRecorder.add(node.position, "trying to change a unmodifiable value");
        }
        node.mutable = false;
    }

    @Override
    public void visit(AstIdentifierExpression node) {
        if (node.name.equals("this")) {
            node.mutable = false;
        } else {
            node.mutable = true;
        }
    }

    @Override
    public void visit(AstFunctionCallExpression node) {
        int parameterCnt = node.symbol.parameterNames.size();
        int inClass = (node.symbol.parameterNames.size() > 0 && node.symbol.parameterNames.get(0).equals("this") ? 1 : 0);
        if (node.arguments.size() + inClass != parameterCnt) {
            errorRecorder.add(node.position, "mismatched argument count");
        } else {
            for (int i = 0; i < node.arguments.size(); ++i) {
                node.arguments.get(i).accept(this);
                if (!node.arguments.get(i).valueType.match(node.symbol.parameterTypes.get(i + inClass))) {
                    errorRecorder.add(node.position, "mismatched argument type");
                }
            }
        }
        node.mutable = false;
    }

    @Override
    public void visit(AstMemberAccessExpression node) {
        node.object.accept(this);
        if (node.object.valueType instanceof StArrayType) {
            node.mutable = false;
        } else {
            if (node.methodCall != null) {
                node.methodCall.accept(this);
                node.mutable = node.methodCall.mutable;
            } else {
                node.mutable = true;
            }
        }
    }

    @Override
    public void visit(AstPostfixIncDecExpression node) {
        node.expr.accept(this);
        boolean mutableError = false;
        boolean typeError = false;
        boolean isInt = isIntType(node.valueType);
        if (!node.mutable) {
            mutableError = true;
        }
        if (!isInt) {
            typeError = true;
        }
        if (typeError) {
            errorRecorder.add(node.position, "mismatched type");
        } else if (mutableError) {
            errorRecorder.add(node.position, "trying to change a unmodifiable value");
        }
    }
}
