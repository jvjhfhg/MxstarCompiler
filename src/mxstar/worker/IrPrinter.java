package mxstar.worker;

import mxstar.ir.*;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class IrPrinter implements IIrVisitor {
    private StringBuilder stringBuilder;
    private HashMap<IrBasicBlock, String> basicBlockNames;
    private HashMap<IrVirtualRegister, String> virtualRegisterNames;
    private HashMap<IrStackSlot, String> stackSlotNames;
    private HashMap<IrStaticData, String> staticDataNames;

    private IrBasicBlock nextBasicBlock;

    private int basicBlockCnt;
    private int virtualRegisterCnt;
    private int stackSlotCnt;
    private int staticDataCnt;

    private boolean inLeaInst;

    public IrPrinter() {
        stringBuilder = new StringBuilder();
        basicBlockNames = new HashMap<>();
        virtualRegisterNames = new HashMap<>();
        stackSlotNames = new HashMap<>();
        staticDataNames = new HashMap<>();
        nextBasicBlock = null;
        inLeaInst = false;

        basicBlockCnt = 0;
        virtualRegisterCnt = 0;
        stackSlotCnt = 0;
        staticDataCnt = 0;
    }

    public String toString() {
        return stringBuilder.toString();
    }

    public void printTo(PrintStream out) {
        out.print(toString());
    }

    private void append(String str) {
        stringBuilder.append(str);
    }

    private void appendln(String str) {
        stringBuilder.append(str + "\n");
    }

    private void appendln() {
        stringBuilder.append("\n");
    }

    private final String indent = "          ";

    private String formattedToken(String token) {
        return String.format("%-10s", token);
    }

    private String indentedInst(String cmd, boolean trailingSpaces) {
        return indent + (trailingSpaces ? String.format("%-10s", cmd) : cmd);
    }

    private String getFunctionName(IrFunction function) {
        switch (function.type) {
            case BUILTIN:
                return "__" + function.name;
            case USERDEFINED:
                return "_" + function.name;
            case EXTERNAL:
                return function.name;
            default:
                return null;
        }
    }

    private String getBasicBlockName(IrBasicBlock basicBlock) {
        if (!basicBlockNames.containsKey(basicBlock)) {
            basicBlockNames.put(basicBlock, "b" + String.valueOf(basicBlockCnt++));
        }
        return basicBlockNames.get(basicBlock);
    }

    private String getVirtualRegisterName(IrVirtualRegister virtualRegister) {
        if (!virtualRegisterNames.containsKey(virtualRegister)) {
            virtualRegisterNames.put(virtualRegister, "vr" + String.valueOf(virtualRegisterCnt++));
        }
        return virtualRegisterNames.get(virtualRegister);
    }

    private String getStackSlotName(IrStackSlot stackSlot) {
        if (!stackSlotNames.containsKey(stackSlot)) {
            stackSlotNames.put(stackSlot, "stack [" + String.valueOf(stackSlotCnt++) + "]");
        }
        return stackSlotNames.get(stackSlot);
    }

    private String getStaticDataName(IrStaticData staticData) {
        if (!staticDataNames.containsKey(staticData)) {
            staticDataNames.put(staticData, "L_" + String.valueOf(staticDataCnt++));
        }
        return staticDataNames.get(staticData);
    }

    @Override
    public void visit(IrProgram program) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./lib/c2nasm/lib.asm"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                appendln(line);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(0);
        }

        appendln();
        appendln(formattedToken("section") + ".text");
        for (IrFunction function : program.functions) {
            function.accept(this);
        }

        appendln();
        appendln(formattedToken("section") + ".data");
        for (IrStaticData staticData : program.staticDatas) {
            appendln(getStaticDataName(staticData) + ":");
            if (staticData.init != null) {
                appendln(indentedInst("dq", true) + String.valueOf(staticData.init.length()));
                append(indentedInst("db", true));
                for (int i = 0; i < staticData.init.length(); ++i) {
                    append(String.format("%02XH, ", (int) staticData.init.charAt(i)));
                }
                append("00H");
                appendln();
            } else {
                append(indentedInst("db", true));
                for (int i = 0; i < staticData.bytes; ++i) {
                    if (i != 0) {
                        append(", ");
                    }
                    append("00H");
                }
                appendln();
            }
        }
    }

    @Override
    public void visit(IrFunction function) {
        appendln(getFunctionName(function) + ":");
        ArrayList<IrBasicBlock> basicBlocks = new ArrayList<>(function.basicBlocks);
        for (int i = 0; i < basicBlocks.size(); ++i) {
            if (i == basicBlocks.size() - 1) {
                nextBasicBlock = null;
            } else {
                nextBasicBlock = basicBlocks.get(i + 1);
            }
            basicBlocks.get(i).accept(this);
        }
    }

    @Override
    public void visit(IrBasicBlock basicBlock) {
        appendln(getBasicBlockName(basicBlock) + ":");
        for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
            instruction.accept(this);
        }
    }

    @Override
    public void visit(IrBinaryInstruction instruction) {
        if ((instruction.opt == IrBinaryInstruction.IrBinaryOpt.ADD || instruction.opt == IrBinaryInstruction.IrBinaryOpt.SUB)
                && instruction.src instanceof IrImmidiate && ((IrImmidiate) instruction.src).value == 0) {
            return;
        }
        if (instruction.opt == IrBinaryInstruction.IrBinaryOpt.MUL) {
            append(indentedInst("imul", true));
            instruction.src.accept(this);
            appendln();
        }
        if (instruction.opt == IrBinaryInstruction.IrBinaryOpt.DIV) {
            append(indentedInst("div", true));
            instruction.src.accept(this);
            appendln();
        }

        String opt = null;
        switch (instruction.opt) {
            case ADD:
                opt = "add"; break;
            case SUB:
                opt = "sub"; break;
            case MOD:
                opt = "mod"; break;
            case SAL:
                opt = "sal"; break;
            case SAR:
                opt = "sar"; break;
            case AND:
                opt = "and"; break;
            case OR:
                opt = "or"; break;
            case XOR:
                opt = "xor"; break;
        }

        if (instruction.opt == IrBinaryInstruction.IrBinaryOpt.SAL || instruction.opt == IrBinaryInstruction.IrBinaryOpt.SAR) {
            append(indentedInst(opt, true));
            instruction.dest.accept(this);
            append(", cl");
            appendln();
            return;
        }

        append(indentedInst(opt, true));
        instruction.dest.accept(this);
        append(", ");
        instruction.src.accept(this);
        appendln();
    }

    @Override
    public void visit(IrUnaryInstruction instruction) {
        String opt = null;
        switch (instruction.opt) {
            case INC:
                opt = "inc"; break;
            case DEC:
                opt = "dec"; break;
            case NEG:
                opt = "neg"; break;
            case NOT:
                opt = "not"; break;
        }

        append(indentedInst(opt, true));
        instruction.dest.accept(this);
        appendln();
    }

    @Override
    public void visit(IrMove instruction) {
        if (instruction.src == instruction.dest) {
            return;
        }
        append(indentedInst("mov", true));
        instruction.dest.accept(this);
        append(", ");
        instruction.src.accept(this);
        appendln();
    }

    @Override
    public void visit(IrPush instruction) {
        append(indentedInst("push", true));
        instruction.src.accept(this);
        appendln();
    }

    @Override
    public void visit(IrPop instruction) {
        append(indentedInst("pop", true));
        instruction.dest.accept(this);
        appendln();
    }

    @Override
    public void visit(IrCjump instruction) {
        String opt = null;
        switch (instruction.opt) {
            case E:
                opt = "je"; break;
            case NE:
                opt = "jne"; break;
            case G:
                opt = "jg"; break;
            case GE:
                opt = "jge"; break;
            case L:
                opt = "jl"; break;
            case LE:
                opt = "jle"; break;
        }

        append(indentedInst("cmp", true));
        instruction.lhs.accept(this);
        append(", ");
        instruction.rhs.accept(this);
        appendln();

        appendln(indentedInst(opt, true) + getBasicBlockName(instruction.thenBlock));

        if (instruction.elseBlock != nextBasicBlock) {
            appendln(indentedInst(opt, true) + getBasicBlockName(instruction.elseBlock));
        }
    }

    @Override
    public void visit(IrJump instruction) {
        if (instruction.target != nextBasicBlock) {
            appendln(indentedInst("jmp", true) + getBasicBlockName(instruction.target));
        }
    }

    @Override
    public void visit(IrLea instruction) {
        append(indentedInst("lea", true));
        instruction.dest.accept(this);
        append(", ");
        instruction.src.accept(this);
        appendln();
        inLeaInst = false;
    }

    @Override
    public void visit(IrReturn instruction) {
        appendln(indentedInst("ret", false));
    }

    @Override
    public void visit(IrCall instruction) {
        appendln(indentedInst("call", true) + getFunctionName(instruction.function));
    }

    @Override
    public void visit(IrCdq instruction) {
        appendln(indentedInst("cdq", false));
    }

    @Override
    public void visit(IrLeave instruction) {
        appendln(indentedInst("leave", false));
    }

    @Override
    public void visit(IrVirtualRegister operand) {
        if (operand.allocatedPlace != null) {
            visit(operand.allocatedPlace);
            virtualRegisterNames.put(operand, operand.allocatedPlace.name);
        } else {
            append(getVirtualRegisterName(operand));
        }
    }

    @Override
    public void visit(IrPhysicalRegister operand) {
        append(operand.name);
    }

    @Override
    public void visit(IrMemory operand) {
        boolean first = true;
        if (!inLeaInst) {
            append("qword ");
        }
        append("[");
        if (operand.base != null) {
            operand.base.accept(this);
            first = false;
        }
        if (operand.index != null) {
            if (!first) {
                append(" + ");
            }
            operand.index.accept(this);
            if (operand.scale != 1) {
                append(" * " + String.valueOf(operand.scale));
            }
            first = false;
        }
        if (operand.constant != null) {
            IrConstant constant = operand.constant;
            if (constant instanceof IrStaticData) {
                if (!first) {
                    append(" + ");
                }
                constant.accept(this);
            } else if (constant instanceof IrImmidiate) {
                int value = ((IrImmidiate) constant).value;
                if (!first) {
                    if (value > 0) {
                        append(" + " + String.valueOf(value));
                    } else if (value < 0) {
                        append(" - " + String.valueOf(-value));
                    }
                } else {
                    append(String.valueOf(value));
                }
            }
        }
        append("]");
    }

    @Override
    public void visit(IrStackSlot operand) {
        if (operand.base != null || operand.index != null || operand.constant != null) {
            visit((IrMemory) operand);
        } else {
            append(getStackSlotName(operand));
        }
    }

    @Override
    public void visit(IrImmidiate operand) {
        append(String.valueOf(operand.value));
    }

    @Override
    public void visit(IrStaticData operand) {
        append(getStaticDataName(operand));
    }

    @Override
    public void visit(IrFunctionAddress operand) {
        append(getFunctionName(operand.function));
    }
}