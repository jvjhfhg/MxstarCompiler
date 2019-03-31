package mxstar.symbol;

import mxstar.ast.TokenPosition;

import java.util.LinkedHashMap;
import java.util.Map;

public class StGlobalTable extends StSymbolTable {
    public Map<String, StClassSymbol> classes;
    public Map<String, StPrimitiveSymbol> primitives;

    public StGlobalTable() {
        super(null);
        classes = new LinkedHashMap<>();
        primitives = new LinkedHashMap<>();

        addDefaultSymbols();
    }

    public void putClassSymbol(String name, StClassSymbol symbol) {
        classes.put(name, symbol);
    }

    public StClassSymbol getClassSymbol(String name) {
        return classes.get(name);
    }

    public void putPrimitiveSymbol(String name, StPrimitiveSymbol symbol) {
        primitives.put(name, symbol);
    }

    public StPrimitiveSymbol getPrimitiveSymbol(String name) {
        return primitives.get(name);
    }

    private StType voidType() {
        return new StPrimitiveType("void", primitives.get("void"));
    }

    private StType intType() {
        return new StPrimitiveType("int", primitives.get("int"));
    }

    private StType stringType() {
        return new StClassType("string", classes.get("string"));
    }

    private void addDefaultPrimitiveTypes() {
        putPrimitiveSymbol("int", new StPrimitiveSymbol("int"));
        putPrimitiveSymbol("bool", new StPrimitiveSymbol("bool"));
        putPrimitiveSymbol("void", new StPrimitiveSymbol("void"));
    }

    private void addDefaultString() {
        StClassSymbol stringSymbol = new StClassSymbol();
        stringSymbol.name = "string";
        stringSymbol.position = new TokenPosition(0, 0);
        stringSymbol.symbolTable = new StSymbolTable(this);
        stringSymbol.symbolTable.putFunctionSymbol("length", stringLength());
        stringSymbol.symbolTable.putFunctionSymbol("substring", stringSubstring());
        stringSymbol.symbolTable.putFunctionSymbol("parseInt", stringParseInt());
        stringSymbol.symbolTable.putFunctionSymbol("ord", stringOrd());
        putClassSymbol("string", stringSymbol);
    }

    private StFunctionSymbol stringLength() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "length";
        symbol.position = new TokenPosition(0, 0);
        symbol.parameterTypes.add(stringType());
        symbol.parameterNames.add("this");
        symbol.returnType = intType();
        return symbol;
    }

    private StFunctionSymbol stringSubstring() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "substring";
        symbol.position = new TokenPosition(0, 0);
        symbol.parameterTypes.add(stringType());
        symbol.parameterNames.add("this");
        symbol.parameterTypes.add(intType());
        symbol.parameterNames.add("left");
        symbol.parameterTypes.add(intType());
        symbol.parameterNames.add("right");
        symbol.returnType = stringType();
        return symbol;
    }

    private StFunctionSymbol stringParseInt() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "parseInt";
        symbol.position = new TokenPosition(0, 0);
        symbol.parameterTypes.add(stringType());
        symbol.parameterNames.add("this");
        symbol.returnType = intType();
        return symbol;
    }

    private StFunctionSymbol stringOrd() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "ord";
        symbol.position = new TokenPosition(0, 0);
        symbol.parameterTypes.add(stringType());
        symbol.parameterNames.add("this");
        symbol.parameterTypes.add(intType());
        symbol.parameterNames.add("pos");
        symbol.returnType = intType();
        return symbol;
    }

    private void addDefaultNull() {
        StClassSymbol nullSymbol = new StClassSymbol();
        nullSymbol.name = "null";
        nullSymbol.position = new TokenPosition(0, 0);
        nullSymbol.symbolTable = new StSymbolTable(this);
        putClassSymbol("null", nullSymbol);
    }

    private void addDefaultFunctions() {
        putFunctionSymbol("print", globalPrint());
        putFunctionSymbol("println", globalPrintln());
        putFunctionSymbol("getString", globalGetString());
        putFunctionSymbol("getInt", globalGetInt());
        putFunctionSymbol("toString", globalToString());
    }

    private StFunctionSymbol globalPrint() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "print";
        symbol.position = new TokenPosition(0, 0);
        symbol.parameterTypes.add(stringType());
        symbol.parameterNames.add("str");
        symbol.returnType = voidType();
        return symbol;
    }

    private StFunctionSymbol globalPrintln() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "println";
        symbol.position = new TokenPosition(0, 0);
        symbol.parameterTypes.add(stringType());
        symbol.parameterNames.add("str");
        symbol.returnType = voidType();
        return symbol;
    }

    private StFunctionSymbol globalGetString() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "getString";
        symbol.position = new TokenPosition(0, 0);
        symbol.returnType = stringType();
        return symbol;
    }

    private StFunctionSymbol globalGetInt() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "getInt";
        symbol.position = new TokenPosition(0, 0);
        symbol.returnType = intType();
        return symbol;
    }

    private StFunctionSymbol globalToString() {
        StFunctionSymbol symbol = new StFunctionSymbol();
        symbol.name = "toString";
        symbol.position = new TokenPosition(0, 0);
        symbol.returnType = stringType();
        symbol.parameterTypes.add(intType());
        symbol.parameterNames.add("i");
        return symbol;
    }

    private void addDefaultSymbols() {
        addDefaultPrimitiveTypes();
        addDefaultString();
        addDefaultNull();
        addDefaultFunctions();
    }
}
