package mxstar.worker;

import mxstar.ast.*;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.IrProgram;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.*;
import mxstar.symbol.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import static mxstar.ir.IrRegisterSet.*;

public class IrBuilder implements IAstVisitor {
    private IrProgram program;

    private StGlobalTable globalTable;

    private IrBasicBlock currentBasicBlock;
    private Stack<IrBasicBlock> loopConditionBlock;
    private Stack<IrBasicBlock> loopAfterBlock;
    private IrFunction currentFunction;
    private StClassSymbol currentClassSymbol;
    private IrVirtualRegister currentThisHandle;
    private HashMap<String, IrFunction> functionMap;
    private HashMap<String, AstFunctionDeclaration> functionDeclarationMap;

    private HashMap<AstExpression, IrBasicBlock> trueBlockMap, falseBlockMap;
    private HashMap<AstExpression, IrOperand> expressionResultMap;
    private HashMap<AstExpression, IrAddress> assignToMap;

    private static IrFunction builtinStringConcat;
    private static IrFunction builtinStringCompare;
    private static IrFunction externalMalloc;
    private static IrFunction builtinInit;

    private boolean isParameter = false;
    private boolean isInClassDeclaration = false;

    public IrBuilder(StGlobalTable globalTable) {
        this.currentBasicBlock = null;
        this.globalTable = globalTable;
        this.program = new IrProgram();
        this.loopConditionBlock = new Stack<>();
        this.loopAfterBlock = new Stack<>();
        this.currentFunction = null;
        this.currentClassSymbol = null;
        this.currentThisHandle = null;
        this.functionMap = new HashMap<>();
        this.functionDeclarationMap = new HashMap<>();
        this.trueBlockMap = new HashMap<>();
        this.falseBlockMap = new HashMap<>();
        this.expressionResultMap = new HashMap<>();
        this.assignToMap = new HashMap<>();
        initBuiltinFunctions();
    }

    private void initBuiltinFunctions() {
        functionMap.put("print", new IrFunction(IrFunction.IrFuncType.BUILTIN, "print", false));
        functionMap.put("println", new IrFunction(IrFunction.IrFuncType.BUILTIN, "println", false));
        functionMap.put("getString", new IrFunction(IrFunction.IrFuncType.BUILTIN, "getString", true));
        functionMap.put("getInt", new IrFunction(IrFunction.IrFuncType.BUILTIN, "getInt", true));
        functionMap.put("toString", new IrFunction(IrFunction.IrFuncType.BUILTIN, "toString", true));
        functionMap.put("string.length", new IrFunction(IrFunction.IrFuncType.BUILTIN, "string_length", true));
        functionMap.put("string.substring", new IrFunction(IrFunction.IrFuncType.BUILTIN, "string_substring", true));
        functionMap.put("string.parseInt", new IrFunction(IrFunction.IrFuncType.BUILTIN, "string_parseInt", true));
        functionMap.put("string.ord", new IrFunction(IrFunction.IrFuncType.BUILTIN, "string_ord", true));

        builtinStringConcat = new IrFunction(IrFunction.IrFuncType.BUILTIN, "stringConcat", true);
        builtinStringCompare = new IrFunction(IrFunction.IrFuncType.BUILTIN, "stringCompare", true);
        externalMalloc = new IrFunction(IrFunction.IrFuncType.EXTERNAL, "malloc", true);
        builtinInit = new IrFunction(IrFunction.IrFuncType.BUILTIN, "init", true);
    }

    public IrProgram getIrProgram() {
        return program;
    }

    private boolean isVoidType(StType type) {
        return type instanceof StPrimitiveType && ((StPrimitiveType) type).name.equals("void");
    }

    private boolean isIntType(StType type) {
        return type instanceof StPrimitiveType && ((StPrimitiveType) type).name.equals("int");
    }

    private boolean isBoolType(StType type) {
        return type instanceof StPrimitiveType && ((StPrimitiveType) type).name.equals("bool");
    }

    private boolean isStringType(StType type) {
        return type instanceof StClassType && ((StClassType) type).name.equals("string");
    }

    private void buildInitFunction(AstProgram node) {
        program.functions.add(builtinInit);
        currentFunction = builtinInit;
        builtinInit.usedGlobalVariables = new HashSet<>(globalTable.globalInitVariables);
        IrBasicBlock frontBlock = new IrBasicBlock(currentFunction, "frontBlock");
        currentBasicBlock = currentFunction.frontBasicBlock = frontBlock;
        for (AstVariableDeclaration variableDeclaration : node.variables) {
            if (variableDeclaration.initValue != null) {
                assign(variableDeclaration.symbol.virtualRegister, variableDeclaration.initValue);
            }
        }
        currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, functionMap.get("main")));
        currentBasicBlock.append(new IrReturn(currentBasicBlock));
        currentFunction.backBasicBlock = currentBasicBlock;
        currentFunction.finalProcess();
    }

    private void boolAssign(IrAddress address, AstExpression expression) {
        IrBasicBlock trueBlock = new IrBasicBlock(currentFunction, "trueBlock");
        IrBasicBlock falseBlock = new IrBasicBlock(currentFunction, "falseBlock");
        IrBasicBlock afterBlock = new IrBasicBlock(currentFunction, "afterBlock");
        trueBlockMap.put(expression, trueBlock);
        falseBlockMap.put(expression, falseBlock);
        expression.accept(this);
        trueBlock.append(new IrMove(trueBlock, address, new IrImmidiate(1)));
        trueBlock.append(new IrJump(trueBlock, afterBlock));
        falseBlock.append(new IrMove(falseBlock, address, new IrImmidiate(0)));
        falseBlock.append(new IrJump(falseBlock, afterBlock));
        currentBasicBlock = afterBlock;
    }

    @Override
    public void visit(AstProgram node) {
        for (AstVariableDeclaration variableDeclaration : node.variables) {
            IrStaticData staticData = new IrStaticData(variableDeclaration.name, 8);
            IrVirtualRegister virtualRegister = new IrVirtualRegister(variableDeclaration.name);
            virtualRegister.spillPlace = new IrMemory(staticData);
            program.staticDatas.add(staticData);
            variableDeclaration.symbol.virtualRegister = virtualRegister;
        }

        LinkedList<AstFunctionDeclaration> functionDeclarations = new LinkedList<>();
        functionDeclarations.addAll(node.functions);
        for (AstClassDeclaration classDeclaration : node.classes) {
            if (classDeclaration.constructor != null) {
                functionDeclarations.add(classDeclaration.constructor);
            }
            functionDeclarations.addAll(classDeclaration.methods);
        }
        for (AstFunctionDeclaration functionDeclaration : functionDeclarations) {
            functionDeclarationMap.put(functionDeclaration.symbol.name, functionDeclaration);
        }
        for (AstFunctionDeclaration functionDeclaration : functionDeclarations) {
            if (functionMap.containsKey(functionDeclaration.symbol.name)) {
                continue;
            }
            functionMap.put(functionDeclaration.symbol.name, new IrFunction(IrFunction.IrFuncType.USERDEFINED,
                    functionDeclaration.symbol.name, !isVoidType(functionDeclaration.symbol.returnType)));
        }
        for (AstFunctionDeclaration functionDeclaration : node.functions) {
            functionDeclaration.accept(this);
        }
        for (AstClassDeclaration classDeclaration : node.classes) {
            classDeclaration.accept(this);
        }

        for (IrFunction function : functionMap.values()) {
            if (function.type == IrFunction.IrFuncType.USERDEFINED) {
                function.finalProcess();
            }
        }

        buildInitFunction(node);
    }

    @Override
    public void visit(AstFunctionDeclaration node) {
        currentFunction = functionMap.get(node.symbol.name);
        currentBasicBlock = currentFunction.frontBasicBlock = new IrBasicBlock(currentFunction, "frontBlock");

        if (isInClassDeclaration) {
            IrVirtualRegister thisHandle = new IrVirtualRegister("");
            currentFunction.parameters.add(thisHandle);
            currentThisHandle = thisHandle;
        }
        isParameter = true;
        for (AstVariableDeclaration variableDeclaration : node.parameters) {
            variableDeclaration.accept(this);
        }
        isParameter = false;

        for (int i = 0; i < currentFunction.parameters.size(); ++i) {
            if (i < 6) {
                currentBasicBlock.append(new IrMove(currentBasicBlock, currentFunction.parameters.get(i), vArgs.get(i)));
            } else {
                currentBasicBlock.append(new IrMove(currentBasicBlock, currentFunction.parameters.get(i), currentFunction.parameters.get(i).spillPlace));
            }
        }

        for (StVariableSymbol variableSymbol : node.symbol.usedGlobalVariables) {
            currentBasicBlock.append(new IrMove(currentBasicBlock, variableSymbol.virtualRegister, variableSymbol.virtualRegister.spillPlace));
        }

        for (AstStatement statement : node.body) {
            statement.accept(this);
        }
        if (!(currentBasicBlock.tail instanceof IrReturn)) {
            if (isVoidType(node.symbol.returnType)) {
                currentBasicBlock.append(new IrReturn(currentBasicBlock));
            } else {
                currentBasicBlock.append(new IrMove(currentBasicBlock, vrax, new IrImmidiate(0)));
                currentBasicBlock.append(new IrReturn(currentBasicBlock));
            }
        }

        LinkedList<IrReturn> returnInstructions = new LinkedList<>();
        for (IrBasicBlock basicBlock : currentFunction.basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                if (instruction instanceof IrReturn) {
                    returnInstructions.add((IrReturn) instruction);
                }
            }
        }

        IrBasicBlock backBlock = new IrBasicBlock(currentFunction, "backBlock");
        for (IrReturn returnInstruction : returnInstructions) {
            returnInstruction.insertPrev(new IrJump(returnInstruction.basicBlock, backBlock));
            returnInstruction.delete();
        }
        backBlock.append(new IrReturn(backBlock));
        currentFunction.backBasicBlock = backBlock;

        IrInstruction returnInstruction = currentFunction.backBasicBlock.tail;
        for (StVariableSymbol variableSymbol : node.symbol.usedGlobalVariables) {
            returnInstruction.insertPrev(new IrMove(returnInstruction.basicBlock, variableSymbol.virtualRegister.spillPlace, variableSymbol.virtualRegister));
        }

        functionMap.put(node.symbol.name, currentFunction);
        program.functions.add(currentFunction);
    }

    @Override
    public void visit(AstPrimitiveType node) {

    }

    @Override
    public void visit(AstNewExpression node) {
        IrFunction constructor = null;
        if (node.valueType instanceof StClassType) {
            StClassType classType = (StClassType) node.valueType;
            if (classType.name.equals("string")) {
                constructor = null;
            } else {
                StFunctionSymbol functionSymbol = classType.symbol.symbolTable.getFunctionSymbol(classType.name);
                if (functionSymbol == null) {
                    constructor = null;
                } else {
                    constructor = functionMap.get(functionSymbol.name);
                }
            }
        } else {
            constructor = null;
        }

        LinkedList<IrOperand> dims = new LinkedList<>();
        if (node.baseType instanceof AstPrimitiveType) {
            IrOperand handle = allocateArray(dims, 0, null);
            expressionResultMap.put(node, handle);
        } else {
            int bytes;
            if (node.valueType instanceof StClassType && ((StClassType) node.valueType).name.equals("string")) {
                bytes = 8 * 2;
            } else {
                bytes = node.valueType.getByte();
            }
            IrOperand handle = allocateArray(dims, bytes, constructor);
            expressionResultMap.put(node, handle);
        }
    }

    @Override
    public void visit(AstExprStatement node) {
        node.expr.accept(this);
    }

    @Override
    public void visit(AstForStatement node) {
        if (node.expr1 != null) {
            node.expr1.accept(this);
        }

        IrBasicBlock bodyBlock = new IrBasicBlock(currentFunction, "forBodyBlock");
        IrBasicBlock afterBlock = new IrBasicBlock(currentFunction, "forAfterBlock");
        IrBasicBlock conditionBlock = (node.expr2 != null ? new IrBasicBlock(currentFunction, "forConditionBlock") : bodyBlock);
        IrBasicBlock updateBlock = (node.expr3 != null ? new IrBasicBlock(currentFunction, "forUpdateBlock") : conditionBlock);

        currentBasicBlock.append(new IrJump(currentBasicBlock, conditionBlock));
        loopConditionBlock.push(conditionBlock);
        loopAfterBlock.push(afterBlock);

        if (node.expr2 != null) {
            trueBlockMap.put(node.expr2, bodyBlock);
            falseBlockMap.put(node.expr2, afterBlock);
            currentBasicBlock = conditionBlock;
            node.expr2.accept(this);
        }

        currentBasicBlock = bodyBlock;
        node.expr2.accept(this);
        currentBasicBlock.append(new IrJump(currentBasicBlock, updateBlock));

        if (node.expr3 != null) {
            currentBasicBlock = updateBlock;
            node.expr3.accept(this);
            currentBasicBlock.append(new IrJump(currentBasicBlock, conditionBlock));
        }

        currentBasicBlock = afterBlock;
        loopAfterBlock.pop();
        loopConditionBlock.pop();
    }

    @Override
    public void visit(AstIfStatement node) {
        IrBasicBlock thenBlock = new IrBasicBlock(currentFunction, "ifThenBlock");
        IrBasicBlock afterBlock = new IrBasicBlock(currentFunction, "ifAfterBlock");
        IrBasicBlock elseBlock = (node.elseBody != null ? new IrBasicBlock(currentFunction, "ifElseBlock") : afterBlock);
        trueBlockMap.put(node.condition, thenBlock);
        falseBlockMap.put(node.condition, elseBlock);

        node.condition.accept(this);
        currentBasicBlock = thenBlock;
        node.ifBody.accept(this);
        currentBasicBlock.append(new IrJump(currentBasicBlock, afterBlock));

        if (node.elseBody != null) {
            currentBasicBlock = elseBlock;
            node.elseBody.accept(this);
            currentBasicBlock.append(new IrJump(currentBasicBlock, afterBlock));
        }
        currentBasicBlock = afterBlock;
    }

    @Override
    public void visit(AstDeclaration node) {

    }

    @Override
    public void visit(AstExpression node) {

    }

    @Override
    public void visit(AstStatement node) {

    }

    @Override
    public void visit(AstClassType node) {

    }

    @Override
    public void visit(AstArrayType node) {

    }

    @Override
    public void visit(AstType node) {

    }

    @Override
    public void visit(AstBlockStatement node) {
        for (AstStatement statement : node.statements) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(AstClassDeclaration node) {
        currentClassSymbol = node.symbol;
        isInClassDeclaration = true;
        if (node.constructor != null) {
            node.constructor.accept(this);
        }
        for (AstFunctionDeclaration functionDeclaration : node.methods) {
            functionDeclaration.accept(this);
        }
        isInClassDeclaration = false;
    }

    @Override
    public void visit(AstBreakStatement node) {
        currentBasicBlock.append(new IrJump(currentBasicBlock, loopAfterBlock.peek()));
    }

    @Override
    public void visit(AstContiStatement node) {
        currentBasicBlock.append(new IrJump(currentBasicBlock, loopConditionBlock.peek()));
    }

    @Override
    public void visit(AstEmptyStatement node) {

    }

    @Override
    public void visit(AstWhileStatement node) {
        IrBasicBlock conditionBlock = new IrBasicBlock(currentFunction, "whileConditionBlock");
        IrBasicBlock bodyBlock = new IrBasicBlock(currentFunction, "whileBodyBlock");
        IrBasicBlock afterBlock = new IrBasicBlock(currentFunction, "whileAfterBlock");

        currentBasicBlock.append(new IrJump(currentBasicBlock, conditionBlock));
        loopConditionBlock.push(conditionBlock);
        loopAfterBlock.push(afterBlock);

        currentBasicBlock = conditionBlock;
        trueBlockMap.put(node.condition, bodyBlock);
        falseBlockMap.put(node.condition, afterBlock);
        node.condition.accept(this);

        currentBasicBlock = bodyBlock;
        node.body.accept(this);
        currentBasicBlock.append(new IrJump(currentBasicBlock, conditionBlock));

        currentBasicBlock = afterBlock;
        loopConditionBlock.pop();
        loopAfterBlock.pop();
    }

    @Override
    public void visit(AstReturnStatement node) {
        if (node.value != null) {
            if (isBoolType(node.value.valueType)) {
                boolAssign(vrax, node.value);
            } else {
                node.value.accept(this);
                currentBasicBlock.append(new IrMove(currentBasicBlock, vrax, expressionResultMap.get(node.value)));
            }
        }
        currentBasicBlock.append(new IrReturn(currentBasicBlock));
    }

    @Override
    public void visit(AstUnaryExpression node) {
        if (node.opt == "!") {
            trueBlockMap.put(node.expr, falseBlockMap.get(node));
            falseBlockMap.put(node.expr, trueBlockMap.get(node));
            node.expr.accept(this);
            return;
        }
        node.expr.accept(this);
        IrOperand operand = expressionResultMap.get(node.expr);
        switch (node.opt) {
            case "++":
            case "--":
                assert operand instanceof IrAddress;
                currentBasicBlock.append(new IrUnaryInstruction(currentBasicBlock,
                        node.opt.equals("++") ? IrUnaryInstruction.IrUnaryOpt.INC : IrUnaryInstruction.IrUnaryOpt.DEC, (IrAddress) operand));
                expressionResultMap.put(node, operand);
                break;
            case "+":
                expressionResultMap.put(node, operand);
                break;
            case "-":
            case "~":
                IrVirtualRegister virtualRegister = new IrVirtualRegister("");
                currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, operand));
                currentBasicBlock.append(new IrUnaryInstruction(currentBasicBlock,
                        node.opt.equals("-") ? IrUnaryInstruction.IrUnaryOpt.NEG : IrUnaryInstruction.IrUnaryOpt.NOT, virtualRegister));
                expressionResultMap.put(node, virtualRegister);
                break;
        }
    }

    private IrOperand doStringConcat(AstExpression lhs, AstExpression rhs) {
        IrAddress res = new IrVirtualRegister("");
        lhs.accept(this);
        IrOperand loperand = expressionResultMap.get(lhs);
        rhs.accept(this);
        IrOperand roperand = expressionResultMap.get(rhs);
        if (loperand instanceof IrMemory && !(loperand instanceof IrStackSlot)) {
            IrVirtualRegister virtualRegister = new IrVirtualRegister("");
            currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, loperand));
            loperand = virtualRegister;
        }
        if (roperand instanceof IrMemory && !(roperand instanceof IrStackSlot)) {
            IrVirtualRegister virtualRegister = new IrVirtualRegister("");
            currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, roperand));
            roperand = virtualRegister;
        }
        currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, builtinStringConcat, loperand, roperand));
        currentBasicBlock.append(new IrMove(currentBasicBlock, res, vrax));
        return res;
    }

    private IrOperand doArithmeticBinary(String optStr, IrAddress dest, AstExpression lhs, AstExpression rhs) {
        IrBinaryInstruction.IrBinaryOpt opt = null;
        boolean isSpecial = false;
        boolean isReversible = false;
        switch (optStr) {
            case "*":
                opt = IrBinaryInstruction.IrBinaryOpt.MUL; isSpecial = true; break;
            case "/":
                opt = IrBinaryInstruction.IrBinaryOpt.DIV; isSpecial = true; break;
            case "%":
                opt = IrBinaryInstruction.IrBinaryOpt.MOD; isSpecial = true; break;
            case "+":
                opt = IrBinaryInstruction.IrBinaryOpt.ADD; isReversible = true; break;
            case "-":
                opt = IrBinaryInstruction.IrBinaryOpt.SUB; break;
            case ">>":
                opt = IrBinaryInstruction.IrBinaryOpt.SAR; break;
            case "<<":
                opt = IrBinaryInstruction.IrBinaryOpt.SAL; break;
            case "&":
                opt = IrBinaryInstruction.IrBinaryOpt.AND; isReversible = true; break;
            case "|":
                opt = IrBinaryInstruction.IrBinaryOpt.OR; isReversible = true; break;
            case "^":
                opt = IrBinaryInstruction.IrBinaryOpt.XOR; isReversible = true; break;
        }

        IrAddress res = new IrVirtualRegister("");
        lhs.accept(this);
        IrOperand loperand = expressionResultMap.get(lhs);
        rhs.accept(this);
        IrOperand roperand = expressionResultMap.get(rhs);

        if (isSpecial) {
            if (optStr.equals("*")) {
                currentBasicBlock.append(new IrMove(currentBasicBlock, vrax, loperand));
                currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, null, roperand));
                currentBasicBlock.append(new IrMove(currentBasicBlock, res, vrax));
            } else {
                currentBasicBlock.append(new IrMove(currentBasicBlock, vrax, loperand));
                currentBasicBlock.append(new IrCdq(currentBasicBlock));
                currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, null, roperand));
                if (optStr.equals("/")) {
                    currentBasicBlock.append(new IrMove(currentBasicBlock, res, vrax));
                } else {
                    currentBasicBlock.append(new IrMove(currentBasicBlock, res, vrdx));
                }
            }
        } else {
            if (loperand == dest) {
                res = dest;
                if (optStr.equals("<<") || optStr.equals(">>")) {
                    currentBasicBlock.append(new IrMove(currentBasicBlock, vrcx, roperand));
                    currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, res, vrcx));
                } else {
                    currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, res, roperand));
                }
            } else if (isReversible && roperand == dest) {
                res = dest;
                currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, res, loperand));
            } else {
                if (optStr.equals("<<") || optStr.equals(">>")) {
                    currentBasicBlock.append(new IrMove(currentBasicBlock, res, loperand));
                    currentBasicBlock.append(new IrMove(currentBasicBlock, vrcx, roperand));
                    currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, res, vrcx));
                } else {
                    currentBasicBlock.append(new IrMove(currentBasicBlock, res, loperand));
                    currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, opt, res, roperand));
                }
            }
        }

        return res;
    }

    private void doRelationalBinary(String optStr, AstExpression lhs, AstExpression rhs, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        lhs.accept(this);
        IrOperand loperand = expressionResultMap.get(lhs);
        rhs.accept(this);
        IrOperand roperand = expressionResultMap.get(rhs);

        IrCjump.IrCompareOpt opt = null;
        switch (optStr) {
            case "<":
                opt = IrCjump.IrCompareOpt.L; break;
            case ">":
                opt = IrCjump.IrCompareOpt.G; break;
            case "==":
                opt = IrCjump.IrCompareOpt.E; break;
            case "<=":
                opt = IrCjump.IrCompareOpt.LE; break;
            case ">=":
                opt = IrCjump.IrCompareOpt.GE; break;
            case "!=":
                opt = IrCjump.IrCompareOpt.NE; break;
        }
        if (lhs.valueType instanceof StClassType && ((StClassType) lhs.valueType).name.equals("string")) {
            IrVirtualRegister virtualRegister = new IrVirtualRegister("");
            currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, builtinStringCompare, loperand, roperand));
            currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, vrax));
            currentBasicBlock.append(new IrCjump(currentBasicBlock, virtualRegister, opt, new IrImmidiate(0), trueBlock, falseBlock));
        } else {
            if (loperand instanceof IrMemory && roperand instanceof IrMemory) {
                IrVirtualRegister virtualRegister = new IrVirtualRegister("");
                currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, loperand));
                loperand = virtualRegister;
            }
            currentBasicBlock.append(new IrCjump(currentBasicBlock, loperand, opt, roperand, trueBlock, falseBlock));
        }
    }

    private void doLogicalBinary(String opt, AstExpression lhs, AstExpression rhs, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        IrBasicBlock checkSecondConditionBlock = new IrBasicBlock(currentFunction, "secondConditionBlock");
        if (opt.equals("&&")) {
            trueBlockMap.put(lhs, checkSecondConditionBlock);
            falseBlockMap.put(lhs, falseBlock);
        } else {
            trueBlockMap.put(lhs, trueBlock);
            falseBlockMap.put(lhs, checkSecondConditionBlock);
        }
        lhs.accept(this);
        currentBasicBlock = checkSecondConditionBlock;
        trueBlockMap.put(rhs, trueBlock);
        falseBlockMap.put(rhs, falseBlock);
        rhs.accept(this);
    }

    @Override
    public void visit(AstBinaryExpression node) {
        switch (node.opt) {
            case "*":
            case "/":
            case "%":
            case "+":
            case "-":
            case ">>":
            case "<<":
            case "&":
            case "|":
            case "^":
                if (node.opt.equals("+") && isStringType(node.valueType)) {
                    expressionResultMap.put(node, doStringConcat(node.expr1, node.expr2));
                } else {
                    expressionResultMap.put(node, doArithmeticBinary(node.opt, assignToMap.get(node), node.expr1, node.expr2));
                }
                break;
            case "<":
            case ">":
            case "==":
            case "<=":
            case ">=":
            case "!=":
                doRelationalBinary(node.opt, node.expr1, node.expr2, trueBlockMap.get(node), falseBlockMap.get(node));
                break;
            case "&&":
            case "||":
                doLogicalBinary(node.opt, node.expr1, node.expr2, trueBlockMap.get(node), falseBlockMap.get(node));
                break;
        }
    }

    @Override
    public void visit(AstVarDeclStatement node) {
        node.declaration.accept(this);
    }

    @Override
    public void visit(AstLiteralExpression node) {
        IrOperand operand = null;
        switch (node.typeName) {
            case "int":
                operand = new IrImmidiate(Integer.valueOf(node.value));
                break;
            case "null":
                operand = new IrImmidiate(0);
                break;
            case "bool":
                currentBasicBlock.append(new IrJump(currentBasicBlock, node.value.equals("true") ? trueBlockMap.get(node) : falseBlockMap.get(node)));
                break;
            case "string":
                IrStaticData staticData = new IrStaticData("static_string", node.value.substring(1, node.value.length() - 1));
                program.staticDatas.add(staticData);
                operand = staticData;
                break;
        }
        expressionResultMap.put(node, operand);
    }

    private IrOperand allocateArray(LinkedList<IrOperand> dims, int baseBytes, IrFunction constructor) {
        if (dims.size() == 0) {
            if (baseBytes == 0) {
                return new IrImmidiate(0);
            } else {
                IrVirtualRegister retAddress = new IrVirtualRegister("");
                currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, externalMalloc, new IrImmidiate(baseBytes)));
                currentBasicBlock.append(new IrMove(currentBasicBlock, retAddress, vrax));
                if (constructor != null) {
                    currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, constructor, retAddress));
                } else {
                    if (baseBytes == 8) {
                        currentBasicBlock.append(new IrMove(currentBasicBlock, new IrMemory(retAddress), new IrImmidiate(0)));
                    } else if (baseBytes == 8 * 2) {
                        currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, IrBinaryInstruction.IrBinaryOpt.ADD, retAddress, new IrImmidiate(8)));
                        currentBasicBlock.append(new IrMove(currentBasicBlock, new IrMemory(retAddress), new IrImmidiate(0)));
                        currentBasicBlock.append(new IrBinaryInstruction(currentBasicBlock, IrBinaryInstruction.IrBinaryOpt.SUB, retAddress, new IrImmidiate(8)));
                    }
                }
                return retAddress;
            }
        } else {
            IrVirtualRegister address = new IrVirtualRegister("");
            IrVirtualRegister size = new IrVirtualRegister("");
            IrVirtualRegister bytes = new IrVirtualRegister("");
            currentBasicBlock.append(new IrMove(currentBasicBlock, size, dims.getFirst()));
            currentBasicBlock.append(new IrLea(currentBasicBlock, bytes, new IrMemory(size, 8, new IrImmidiate(8))));
            currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, externalMalloc, bytes));
            currentBasicBlock.append(new IrMove(currentBasicBlock, address, vrax));
            currentBasicBlock.append(new IrMove(currentBasicBlock, new IrMemory(address), size));
            IrBasicBlock conditionBlock = new IrBasicBlock(currentFunction, "allocateConditionBlock");
            IrBasicBlock bodyBlock = new IrBasicBlock(currentFunction, "allocateBodyBlock");
            IrBasicBlock afterBlock = new IrBasicBlock(currentFunction, "allocateAfterBlock");
            currentBasicBlock.append(new IrJump(currentBasicBlock, conditionBlock));
            conditionBlock.append(new IrCjump(conditionBlock, size, IrCjump.IrCompareOpt.G, new IrImmidiate(0), bodyBlock, afterBlock));
            currentBasicBlock = conditionBlock;
            if (dims.size() == 1) {
                IrOperand handle = allocateArray(new LinkedList<>(), baseBytes, constructor);
                currentBasicBlock.append(new IrMove(currentBasicBlock, new IrMemory(address, size, 8), handle));
            } else {
                LinkedList<IrOperand> remainDims = new LinkedList<>();
                for (int i = 1; i < dims.size(); ++i) {
                    remainDims.add(dims.get(i));
                }
                IrOperand handle = allocateArray(remainDims, baseBytes, constructor);
                currentBasicBlock.append(new IrMove(currentBasicBlock, new IrMemory(address, size, 8), handle));
            }
            currentBasicBlock.append(new IrUnaryInstruction(currentBasicBlock, IrUnaryInstruction.IrUnaryOpt.DEC, size));
            currentBasicBlock.append(new IrJump(currentBasicBlock, conditionBlock));
            currentBasicBlock = afterBlock;
            return address;
        }
    }

    @Override
    public void visit(AstNewArrayExpression node) {
        IrFunction constructor = null;
        if (node.emptyDimCnt == 0) {
            if (node.valueType instanceof StClassType) {
                StClassType classType = (StClassType) node.valueType;
                if (classType.name.equals("string")) {
                    constructor = null;
                } else {
                    StFunctionSymbol functionSymbol = classType.symbol.symbolTable.getFunctionSymbol(classType.name);
                    if (functionSymbol == null) {
                        constructor = null;
                    } else {
                        constructor = functionMap.get(functionSymbol.name);
                    }
                }
            } else {
                constructor = null;
            }
        } else {
            constructor = null;
        }

        LinkedList<IrOperand> dims = new LinkedList<>();
        for (AstExpression expression : node.indexes) {
            expression.accept(this);
            dims.add(expressionResultMap.get(expression));
        }
        if (node.emptyDimCnt > 0 || node.baseType instanceof AstPrimitiveType) {
            IrOperand handle = allocateArray(dims, 0, null);
            expressionResultMap.put(node, handle);
        } else {
            int bytes;
            if (node.valueType instanceof StClassType && ((StClassType) node.valueType).name.equals("string")) {
                bytes = 8 * 2;
            } else {
                bytes = node.valueType.getByte();
            }
            IrOperand handle = allocateArray(dims, bytes, constructor);
            expressionResultMap.put(node, handle);
        }
    }

    @Override
    public void visit(AstVariableDeclaration node) {
        assert currentFunction != null;
        IrVirtualRegister virtualRegister = new IrVirtualRegister(node.name);
        if (isParameter) {
            if (currentFunction.parameters.size() >= 6) {
                virtualRegister.spillPlace = new IrStackSlot(virtualRegister.hint);
            }
            currentFunction.parameters.add(virtualRegister);
        }
        node.symbol.virtualRegister = virtualRegister;
        if (node.initValue != null) {
            assign(virtualRegister, node.initValue);
        }
    }

    @Override
    public void visit(AstArrayIndexExpression node) {
        node.address.accept(this);
        IrOperand address = expressionResultMap.get(node.address);
        node.index.accept(this);
        IrOperand index = expressionResultMap.get(node.index);

        IrVirtualRegister base = null;
        if (address instanceof IrRegister) {
            base = (IrVirtualRegister) address;
        } else {
            base = new IrVirtualRegister("");
            currentBasicBlock.append(new IrMove(currentBasicBlock, base, address));
        }

        IrMemory memory = null;
        if (index instanceof IrImmidiate) {
            memory = new IrMemory(base, new IrImmidiate(((IrImmidiate) index).value * 8 + 8));
        } else if (index instanceof IrRegister) {
            memory = new IrMemory(base, (IrRegister) index, 8, new IrImmidiate(8));
        } else if (index instanceof IrMemory) {
            IrVirtualRegister virtualRegister = new IrVirtualRegister("");
            currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, index));
            memory = new IrMemory(base, virtualRegister, 8, new IrImmidiate(8));
        }

        if (trueBlockMap.containsKey(node)) {
            currentBasicBlock.append(new IrCjump(currentBasicBlock, memory, IrCjump.IrCompareOpt.NE,
                    new IrImmidiate(0), trueBlockMap.get(node), falseBlockMap.get(node)));
        } else {
            expressionResultMap.put(node, memory);
        }
    }

    private void assign(IrAddress address, AstExpression expression) {
        if (isBoolType(expression.valueType)) {
            boolAssign(address, expression);
        } else {
            assignToMap.put(expression, address);
            expression.accept(this);
            IrOperand res = expressionResultMap.get(expression);
            if (res != address) {
                currentBasicBlock.append(new IrMove(currentBasicBlock, address, res));
            }
        }
    }

    @Override
    public void visit(AstAssignmentExpression node) {
        node.expr1.accept(this);
        IrOperand lvalue = expressionResultMap.get(node.expr1);
        assert lvalue instanceof IrAddress;
        assign((IrAddress) lvalue, node.expr2);
    }

    @Override
    public void visit(AstIdentifierExpression node) {
        IrOperand operand;
        if (node.name.equals("this")) {
            operand = currentThisHandle;
        } else if (node.symbol.isClassField) {
            String fieldName = node.name;
            int offset = currentClassSymbol.symbolTable.getVariableOffset(fieldName);
            operand = new IrMemory(currentThisHandle, new IrImmidiate(offset));
        } else {
            operand = node.symbol.virtualRegister;
            if (node.symbol.isGlobal) {
                currentFunction.usedGlobalVariables.add(node.symbol);
            }
        }

        if (trueBlockMap.containsKey(node)) {
            currentBasicBlock.append(new IrCjump(currentBasicBlock, operand, IrCjump.IrCompareOpt.NE,
                    new IrImmidiate(0), trueBlockMap.get(node), falseBlockMap.get(node)));
        } else {
            expressionResultMap.put(node, operand);
        }
    }

    @Override
    public void visit(AstFunctionCallExpression node) {
        LinkedList<IrOperand> arguments = new LinkedList<>();
        if (!node.symbol.isGlobal) {
            arguments.add(currentThisHandle);
        }
        for (AstExpression expression : node.arguments) {
            expression.accept(this);
            arguments.add(expressionResultMap.get(expression));
        }
        currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, functionMap.get(node.symbol.name), arguments));
        if (trueBlockMap.containsKey(node)) {
            currentBasicBlock.append(new IrCjump(currentBasicBlock, vrax, IrCjump.IrCompareOpt.NE,
                    new IrImmidiate(0), trueBlockMap.get(node), falseBlockMap.get(node)));
        } else {
            if (!isVoidType(node.symbol.returnType)) {
                IrVirtualRegister virtualRegister = new IrVirtualRegister("");
                currentBasicBlock.append(new IrMove(currentBasicBlock, virtualRegister, vrax));
                expressionResultMap.put(node, virtualRegister);
            }
        }
    }

    @Override
    public void visit(AstMemberAccessExpression node) {
        IrVirtualRegister baseAddress = new IrVirtualRegister("");
        node.object.accept(this);
        currentBasicBlock.append(new IrMove(currentBasicBlock, baseAddress, expressionResultMap.get(node.object)));

        if (node.object.valueType instanceof StArrayType) {
            expressionResultMap.put(node, new IrMemory(baseAddress));
        } else if (node.object.valueType instanceof StClassType) {
            StClassType classType = (StClassType) node.object.valueType;
            IrOperand operand;
            if (node.fieldAccess != null) {
                String fieldName = node.fieldAccess.name;
                int offset = classType.symbol.symbolTable.getVariableOffset(fieldName);
                operand = new IrMemory(baseAddress, new IrImmidiate(offset));
            } else {
                IrFunction function = functionMap.get(node.methodCall.symbol.name);
                LinkedList<IrOperand> arguments = new LinkedList<>();
                arguments.add(baseAddress);
                for (AstExpression expression : node.methodCall.arguments) {
                    expression.accept(this);
                    IrOperand argument = expressionResultMap.get(expression);
                    arguments.add(argument);
                }
                currentBasicBlock.append(new IrCall(currentBasicBlock, vrax, function, arguments));
                if (!isVoidType(node.methodCall.symbol.returnType)) {
                    IrVirtualRegister retValue = new IrVirtualRegister("");
                    currentBasicBlock.append(new IrMove(currentBasicBlock, retValue, vrax));
                    operand = retValue;
                } else {
                    operand = null;
                }
            }
            if (trueBlockMap.containsKey(node)) {
                currentBasicBlock.append(new IrCjump(currentBasicBlock, operand, IrCjump.IrCompareOpt.NE,
                        new IrImmidiate(0), trueBlockMap.get(node), falseBlockMap.get(node)));
            } else {
                expressionResultMap.put(node, operand);
            }
        }
    }

    @Override
    public void visit(AstPostfixIncDecExpression node) {
        node.expr.accept(this);
        IrOperand operand = expressionResultMap.get(node.expr);
        assert operand instanceof IrAddress;
        IrVirtualRegister oldValue = new IrVirtualRegister("");
        currentBasicBlock.append(new IrMove(currentBasicBlock, oldValue, operand));
        currentBasicBlock.append(new IrUnaryInstruction(currentBasicBlock,
                node.opt.equals("++") ? IrUnaryInstruction.IrUnaryOpt.INC : IrUnaryInstruction.IrUnaryOpt.DEC, (IrAddress) operand));
        expressionResultMap.put(node, oldValue);
    }
}
