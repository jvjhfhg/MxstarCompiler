package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.operand.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.vArgs;
import static mxstar.ir.IrRegisterSet.vCallerSave;

public class IrCall extends IrInstruction {
    public IrAddress dest;
    public IrFunction function;
    public LinkedList<IrOperand> arguments;

    public IrCall(IrBasicBlock basicBlock, IrAddress dest, IrFunction function, LinkedList<IrOperand> arguments) {
        super(basicBlock);
        this.dest = dest;
        this.function = function;
        this.arguments = new LinkedList<>(arguments);
        super.basicBlock.function.callees.add(function);
    }

    public IrCall(IrBasicBlock basicBlock, IrAddress dest, IrFunction function, IrOperand... arguments) {
        super(basicBlock);
        this.dest = dest;
        this.function = function;
        this.arguments = new LinkedList<>(Arrays.asList(arguments));
        super.basicBlock.function.callees.add(function);
    }

    public LinkedList<IrRegister> getCallUsed() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        for (IrOperand operand : arguments) {
            if (operand instanceof IrMemory) {
                regs.addAll(((IrMemory) operand).getUsedRegs());
            } else if (operand instanceof IrVirtualRegister) {
                regs.add((IrRegister) operand);
            }
        }
        return regs;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {

    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (dest instanceof IrRegister && renameMap.containsKey(dest)) {
            dest = renameMap.get(dest);
        }
    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        return new LinkedList<>(vArgs.subList(0, Math.min(6, arguments.size())));
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        return new LinkedList<>(vCallerSave);
    }

    @Override
    public LinkedList<IrStackSlot> getStackSlots() {
        LinkedList<IrStackSlot> stackSlots = new LinkedList<>();
        stackSlots.addAll(defaultGetStackSlots(dest));
        for (IrOperand operand : arguments) {
            if (operand instanceof IrStackSlot) {
                stackSlots.add((IrStackSlot) operand);
            }
        }
        return stackSlots;
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
