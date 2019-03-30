package mxstar.symbol;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StSymbolTable {
    public Map<String, StVariableSymbol> variables;
    public Map<String, StFunctionSymbol> functions;
    public StSymbolTable parent;
    public List<StSymbolTable> children;

    public StSymbolTable(StSymbolTable parent) {
        this.variables = new LinkedHashMap<>();
        this.functions = new LinkedHashMap<>();
        this.parent = parent;
        this.children = new LinkedList<>();
    }

    public void putVariableSymbol(String name, StVariableSymbol variableSymbol) {
        variables.put(name, variableSymbol);
    }

    public StVariableSymbol getVariableSymbol(String name) {
        return variables.get(name);
    }

    public void putFunctionSymbol(String name, StFunctionSymbol functionSymbol) {
        functions.put(name, functionSymbol);
    }

    public StFunctionSymbol getFunctionSymbol(String name) {
        return functions.get(name);
    }
}
