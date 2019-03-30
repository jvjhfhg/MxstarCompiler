package mxstar.symbol;

import mxstar.ast.TokenPosition;

import java.util.LinkedList;
import java.util.List;

public class StFunctionSymbol {
    public String name;
    public TokenPosition position;
    public StType returnType;
    public List<StType> parameterTypes;
    public List<String> parameterNames;
    public StSymbolTable symbolTable;


    public StFunctionSymbol() {
        parameterTypes = new LinkedList<>();
        parameterNames = new LinkedList<>();
    }
}
