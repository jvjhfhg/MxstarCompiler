package mxstar.worker;

import mxstar.ast.*;
import mxstar.symbol.*;
import mxstar.exception.ErrorRecorder;

import java.util.HashMap;

public class StBuilder implements IAstVisitor {
    public ErrorRecorder errorRecorder;
    public StGlobalTable globalTable;
    public StSymbolTable currentSymbolTable;
    private String name;
    public HashMap<StSymbolTable, StClassSymbol> symbolTableToClassSymbol;
    private StFunctionSymbol currentFunction;

    public StBuilder(ErrorRecorder errorRecorder) {
        this.errorRecorder = errorRecorder;
        this.globalTable = new StGlobalTable();
        this.currentSymbolTable = globalTable;
        this.symbolTableToClassSymbol = new HashMap<>();
        this.currentFunction = null;
    }

    private void enter(StSymbolTable symbolTable) {
        currentSymbolTable = symbolTable;
    }

    private void leave() {
        currentSymbolTable = currentSymbolTable.parent;
    }

    private StType getStType(AstType node) {
        if (node instanceof AstPrimitiveType) {
            StPrimitiveSymbol symbol = globalTable.getPrimitiveSymbol(((AstPrimitiveType) node).name);
            if (symbol != null) {
                return new StPrimitiveType(symbol.name, symbol);
            } else {
                return null;
            }
        } else if (node instanceof AstClassType) {
            StClassSymbol symbol = globalTable.getClassSymbol(((AstClassType) node).name);
            if (symbol != null) {
                return new StClassType(symbol.name, symbol);
            } else {
                return null;
            }
        } else if (node instanceof AstArrayType) {
            StType baseType;
            if (((AstArrayType) node).dimension == 1) {
                baseType = new StArrayType(getStType(((AstArrayType) node).baseType));
                if (((AstArrayType) node).baseType instanceof AstPrimitiveType && ((AstPrimitiveType) ((AstArrayType) node).baseType).name.equals("void")) {
                    errorRecorder.add(node.position, "invalid array base type");
                    return null;
                }
            } else {
                AstArrayType tmp = new AstArrayType();
                tmp.baseType = ((AstArrayType) node).baseType;
                tmp.dimension = ((AstArrayType) node).dimension - 1;
                baseType = new StArrayType(getStType(tmp));
            }
            if (baseType != null) {
                return baseType;
            } else {
                return null;
            }
        } else {
            assert false;
            return null;
        }
    }

    private StVariableSymbol getStVariableSymbol(String name, StSymbolTable symbolTable) {
        StVariableSymbol symbol = symbolTable.getVariableSymbol(name);
        if (symbol != null) {
            return symbol;
        } else if (symbolTable.parent != null) {
            return getStVariableSymbol(name, symbolTable.parent);
        } else {
            return null;
        }
    }

    private StVariableSymbol getStVariableSymbol(String name) {
        return getStVariableSymbol(name, currentSymbolTable);
    }

    private StFunctionSymbol getStFunctionSymbol(String name, StSymbolTable symbolTable) {
        StFunctionSymbol symbol = symbolTable.getFunctionSymbol(name);
        if (symbol != null) {
            return symbol;
        } else if (symbolTable.parent != null) {
            return getStFunctionSymbol(name, symbolTable.parent);
        } else {
            return null;
        }
    }

    private StFunctionSymbol getStFunctionSymbol(String name) {
        this.name = name;
        return getStFunctionSymbol(name, currentSymbolTable);
    }

    private void declareClass(AstClassDeclaration classDeclaration) {
        if (globalTable.getClassSymbol(classDeclaration.name) != null) {
            errorRecorder.add(classDeclaration.position, "redefinition of class");
            return;
        }
        if (globalTable.getFunctionSymbol(classDeclaration.name) != null) {
            errorRecorder.add(classDeclaration.position, "class identifier conflict with function");
            return;
        }
        StClassSymbol symbol = new StClassSymbol();
        symbol.name = classDeclaration.name;
        symbol.position = classDeclaration.position;
        symbol.symbolTable = new StSymbolTable(globalTable);
        globalTable.putClassSymbol(classDeclaration.name, symbol);
        classDeclaration.symbol = symbol;
        symbolTableToClassSymbol.put(symbol.symbolTable, symbol);
    }

    private void declareClassMethods(AstClassDeclaration classDeclaration) {
        StClassSymbol symbol = globalTable.getClassSymbol(classDeclaration.name);
        enter(symbol.symbolTable);
        if (classDeclaration.constructor != null) {
            declareFunction(classDeclaration.constructor, symbol);
        }
        for (AstFunctionDeclaration d : classDeclaration.methods) {
            declareFunction(d, symbol);
        }
        leave();
    }

    private void declareFunction(AstFunctionDeclaration functionDeclaration, StClassSymbol classSymbol) {
        if (currentSymbolTable.getFunctionSymbol(functionDeclaration.name) != null) {
            errorRecorder.add(functionDeclaration.position, "redefinition of function");
            return;
        }
        if (classSymbol == null && globalTable.getClassSymbol(functionDeclaration.name) != null) {
            errorRecorder.add(functionDeclaration.position, "function identifier conflict with class");
            return;
        }
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = (classSymbol != null ? classSymbol.name + "." : "") + functionDeclaration.name;
        symbol.isGlobal = (classSymbol == null);
        symbol.position = functionDeclaration.position;
        symbol.returnType = getStType(functionDeclaration.returnType);
        if (symbol.returnType == null) {
            errorRecorder.add(functionDeclaration.position, "invalid return type");
            return;
        }
        symbol.symbolTable = null;
        if (classSymbol != null) {
            symbol.parameterTypes.add(new StClassType(classSymbol.name, classSymbol));
            symbol.parameterNames.add("this");
        }
        for (AstVariableDeclaration d : functionDeclaration.parameters) {
            StType type = getStType(d.type);
            if (type == null) {
                errorRecorder.add(d.position, "invalid parameter type");
                return;
            }
            symbol.parameterTypes.add(type);
            symbol.parameterNames.add(d.name);
        }
        functionDeclaration.symbol = symbol;
        currentSymbolTable.putFunctionSymbol(symbol.name, symbol);
    }

    private void defineClassFields(AstClassDeclaration classDeclaration) {
        StClassSymbol symbol = globalTable.getClassSymbol(classDeclaration.name);
        enter(symbol.symbolTable);
        for (AstVariableDeclaration d : classDeclaration.fields) {
            defineVariable(d);
        }
        leave();
    }

    private void defineClassMethods(AstClassDeclaration classDeclaration) {
        StClassSymbol symbol = globalTable.getClassSymbol(classDeclaration.name);
        enter(symbol.symbolTable);
        if (classDeclaration.constructor != null) {
            defineFunction(classDeclaration.constructor, symbol);
        }
        for (AstFunctionDeclaration d : classDeclaration.methods) {
            defineFunction(d, symbol);
        }
        leave();
    }

    private void defineFunction(AstFunctionDeclaration functionDeclaration, StClassSymbol classSymbol) {
        StFunctionSymbol symbol = currentSymbolTable.getFunctionSymbol(functionDeclaration.name);
        currentFunction = symbol;
        symbol.symbolTable = new StSymbolTable(currentSymbolTable);
        enter(symbol.symbolTable);
        if (classSymbol != null) {
            defineVariable(new AstVariableDeclaration(new AstClassType(classSymbol.name), "this", null));
        }
        for (AstVariableDeclaration d : functionDeclaration.parameters) {
            defineVariable(d);
        }
        for (AstStatement s : functionDeclaration.body) {
            s.accept(this);
        }
        leave();
        currentFunction = null;
    }

    private void defineVariable(AstVariableDeclaration variableDeclaration) {
        StType type = getStType(variableDeclaration.type);
        if (variableDeclaration.initValue != null) {
            variableDeclaration.initValue.accept(this);
        }
        if (type != null) {
            if (currentSymbolTable.getVariableSymbol(variableDeclaration.name) != null) {
                errorRecorder.add(variableDeclaration.position, "redefinition of variable");
                return;
            } else {
                if ((type instanceof StPrimitiveType && ((StPrimitiveType) type).name.equals("void"))
                        || (type instanceof StClassType && ((StClassType) type).name.equals("null"))) {
                    errorRecorder.add(variableDeclaration.position, "invalid variable type");
                    return;
                }
                boolean isClassField = symbolTableToClassSymbol.containsKey(currentSymbolTable);
                boolean isGlobal = (currentSymbolTable == globalTable);
                variableDeclaration.symbol = new StVariableSymbol(variableDeclaration.name, type, variableDeclaration.position);
                variableDeclaration.symbol.isClassField = isClassField;
                variableDeclaration.symbol.isGlobal = isGlobal;
                currentSymbolTable.putVariableSymbol(variableDeclaration.name, variableDeclaration.symbol);
                if (isGlobal && variableDeclaration.initValue != null) {
                    globalTable.globalInitVariables.add(variableDeclaration.symbol);
                }
            }
        } else {
            errorRecorder.add(variableDeclaration.position, "invalid variable type");
            return;
        }
    }

    @Override
    public void visit(AstType node) {
        assert false;
    }

    @Override
    public void visit(AstProgram node) {
        for (AstClassDeclaration d : node.classes) {
            declareClass(d);
        }
        for (AstClassDeclaration d : node.classes) {
            declareClassMethods(d);
        }
        for (AstFunctionDeclaration d : node.functions) {
            declareFunction(d, null);
        }
        if (errorRecorder.errorOccured()) {
            return;
        }
        for (AstClassDeclaration d : node.classes) {
            defineClassFields(d);
        }
        for (AstDeclaration d : node.declarations) {
            if (d instanceof AstClassDeclaration) {
                defineClassMethods((AstClassDeclaration) d);
            } else if (d instanceof AstFunctionDeclaration) {
                defineFunction((AstFunctionDeclaration) d, null);
            } else { // AstVariableDeclaration
                defineVariable((AstVariableDeclaration) d);
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
        node.ifBody.accept(this);
        if (node.elseBody != null) {
            node.elseBody.accept(this);
        }
    }

    @Override
    public void visit(AstForStatement node) {
        if (node.expr1 != null) node.expr1.accept(this);
        if (node.expr2 != null) node.expr2.accept(this);
        if (node.expr3 != null) node.expr3.accept(this);
        node.body.accept(this);
    }

    @Override
    public void visit(AstExprStatement node) {
        node.expr.accept(this);
    }

    @Override
    public void visit(AstNewExpression node) {
        node.valueType = getStType(node.baseType);
        if (node.valueType == null) {
            errorRecorder.add(node.position, "type was not declared");
            return;
        }
        if (node.baseType instanceof AstPrimitiveType && ((AstPrimitiveType) node.baseType).name.equals("void")) {
            errorRecorder.add(node.position, "invalid type");
            return;
        }
    }

    @Override
    public void visit(AstPrimitiveType node) {

    }

    @Override
    public void visit(AstBlockStatement node) {
        StSymbolTable stSymbolTable = new StSymbolTable(currentSymbolTable);
        enter(stSymbolTable);
        for (AstStatement s : node.statements) {
            s.accept(this);
        }
        leave();
    }

    @Override
    public void visit(AstBreakStatement node) {

    }

    @Override
    public void visit(AstContiStatement node) {

    }

    @Override
    public void visit(AstEmptyStatement node) {

    }

    @Override
    public void visit(AstWhileStatement node) {
        node.condition.accept(this);
        node.body.accept(this);
    }

    @Override
    public void visit(AstReturnStatement node) {
        if (node.value != null) {
            node.value.accept(this);
        }
    }

    @Override
    public void visit(AstUnaryExpression node) {
        node.expr.accept(this);
        node.valueType = node.expr.valueType;
    }

    @Override
    public void visit(AstBinaryExpression node) {
        node.expr1.accept(this);
        node.expr2.accept(this);
        switch (node.opt) {
            case "==":
            case "!=":
            case "<=":
            case ">=":
            case "<":
            case ">":
                node.valueType = new StPrimitiveType("bool", globalTable.getPrimitiveSymbol("bool"));
                break;
            default:
                node.valueType = node.expr1.valueType;
        }
    }

    @Override
    public void visit(AstClassDeclaration node) {

    }

    @Override
    public void visit(AstVarDeclStatement node) {
        node.declaration.accept(this);
    }

    @Override
    public void visit(AstLiteralExpression node) {
        if (node.type instanceof AstPrimitiveType) {
            node.valueType = new StPrimitiveType(((AstPrimitiveType) node.type).name, globalTable.getPrimitiveSymbol(((AstPrimitiveType) node.type).name));
        } else if (node.type instanceof AstClassType) {
            node.valueType = new StClassType(((AstClassType) node.type).name, globalTable.getClassSymbol(((AstClassType) node.type).name));
        } else {
            assert false;
        }
    }

    @Override
    public void visit(AstNewArrayExpression node) {
        for (AstExpression e : node.indexes) {
            e.accept(this);
        }
        int dimension = node.indexes.size() + node.emptyDimCnt;
        node.valueType = getStType(node.baseType);
        if (node.valueType == null) {
            errorRecorder.add(node.position, "invalid array base type");
            return;
        }
        if (dimension == 0 && node.baseType instanceof AstPrimitiveType && ((AstPrimitiveType) node.baseType).name.equals("void")) {
            errorRecorder.add(node.position, "invalid array base type");
            return;
        }
        for (int i = 0; i < dimension; ++i) {
            node.valueType = new StArrayType(node.valueType);
        }
    }

    @Override
    public void visit(AstFunctionDeclaration node) {

    }

    @Override
    public void visit(AstVariableDeclaration node) {
        defineVariable(node);
    }

    @Override
    public void visit(AstArrayIndexExpression node) {
        node.address.accept(this);
        node.index.accept(this);
        if (node.address.valueType instanceof StArrayType) {
            node.valueType = ((StArrayType) node.address.valueType).baseType;
        } else {
            node.valueType = null;
            errorRecorder.add(node.position, "cannot access index of an non-array object");
            return;
        }
    }

    @Override
    public void visit(AstAssignmentExpression node) {
        node.expr1.accept(this);
        node.expr2.accept(this);
        node.valueType = new StPrimitiveType("void", globalTable.getPrimitiveSymbol("void"));
    }

    @Override
    public void visit(AstIdentifierExpression node) {
        StVariableSymbol symbol = getStVariableSymbol(node.name);
        if (symbol == null) {
            errorRecorder.add(node.position, "variable was not declared");
            return;
        }
        node.symbol = symbol;
        node.valueType = symbol.type;
        if (symbol.isGlobal) {
            if (currentFunction != null) {
                currentFunction.usedGlobalVariables.add(symbol);
            } else {
                globalTable.globalInitVariables.add(symbol);
            }
        }
    }

    @Override
    public void visit(AstFunctionCallExpression node) {
        StFunctionSymbol symbol = getStFunctionSymbol(node.name);
        if (symbol == null) {
            errorRecorder.add(node.position, "function was not declared");
            return;
        }
        for (AstExpression e : node.arguments) {
            e.accept(this);
        }
        node.valueType = symbol.returnType;
        node.symbol = symbol;
    }

    @Override
    public void visit(AstMemberAccessExpression node) {
        node.object.accept(this);
        if (node.object.valueType instanceof StPrimitiveType) {
            errorRecorder.add(node.position, "invalid member access");
            node.valueType = null;
            return;
        }
        if (node.object.valueType instanceof StArrayType) {
            StArrayType arrayType = (StArrayType) node.object.valueType;
            if (node.methodCall == null || !node.methodCall.name.equals("size")) {
                errorRecorder.add(node.position, "member was not declared");
                return;
            } else {
                node.valueType = new StPrimitiveType("int", globalTable.getPrimitiveSymbol("int"));
            }
        } else {
            StClassType classType = (StClassType) node.object.valueType;
            if (node.fieldAccess != null) {
                node.fieldAccess.symbol = getStVariableSymbol(node.fieldAccess.name, classType.symbol.symbolTable);
                if (node.fieldAccess.symbol == null) {
                    errorRecorder.add(node.position, "member was not declared");
                    return;
                }
                node.fieldAccess.valueType = node.fieldAccess.symbol.type;
                node.valueType = node.fieldAccess.valueType;
            } else {
                node.methodCall.symbol = getStFunctionSymbol(node.methodCall.name, classType.symbol.symbolTable);
                if (node.methodCall.symbol == null) {
                    errorRecorder.add(node.methodCall.position, "member was not declared");
                    return;
                }
                node.methodCall.valueType = node.methodCall.symbol.returnType;
                node.valueType = node.methodCall.valueType;
                for (AstExpression e : node.methodCall.arguments) {
                    e.accept(this);
                }
            }
        }
    }

    @Override
    public void visit(AstPostfixIncDecExpression node) {
        node.expr.accept(this);
        node.valueType = node.expr.valueType;
    }
}
