package mxstar.symbol;

import mxstar.ast.TokenPosition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class StFunctionSymbol {
    public String name;
    public TokenPosition position;
    public StType returnType;
    public List<StType> parameterTypes;
    public List<String> parameterNames;
    public StSymbolTable symbolTable;

    public boolean isGlobal;
    public HashSet<StVariableSymbol> usedGlobalVariables;

    public StFunctionSymbol() {
        parameterTypes = new LinkedList<>();
        parameterNames = new LinkedList<>();
        usedGlobalVariables = new HashSet<>();
        isGlobal = false;
    }
}
