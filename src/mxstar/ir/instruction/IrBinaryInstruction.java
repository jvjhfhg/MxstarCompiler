package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.*;

import java.util.HashMap;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.vrax;
import static mxstar.ir.IrRegisterSet.vrdx;

public class IrBinaryInstruction extends IrInstruction {
    public enum IrBinaryOpt {
        ADD, SUB, MUL, DIV, MOD, SAL, SAR, AND, OR, XOR
    }

    public IrBinaryOpt opt;
    public IrAddress dest;
    public IrOperand src;

    public IrBinaryInstruction(IrBasicBlock basicBlock, IrBinaryOpt opt, IrAddress dest, IrOperand src) {
        super(basicBlock);
        this.opt = opt;
        this.dest = dest;
        this.src = src;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (src instanceof IrMemory) {
            src = ((IrMemory) src).copy();
            ((IrMemory) src).renameUsedReg(renameMap);
        } else if (src instanceof IrRegister && renameMap.containsKey(src)) {
            src = renameMap.get(src);
        }
        if (dest instanceof IrMemory) {
            dest = ((IrMemory) dest).copy();
            ((IrMemory) dest).renameUsedReg(renameMap);
        } else if (dest instanceof IrRegister && renameMap.containsKey(dest)) {
            dest = renameMap.get(dest);
        }
    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (dest instanceof IrRegister && renameMap.containsKey(dest)) {
            dest = renameMap.get(dest);
        }
    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (src instanceof IrMemory) {
            regs.addAll(((IrMemory) src).getUsedRegs());
        } else if (src instanceof IrRegister) {
            regs.add((IrRegister) src);
        }
        if (dest instanceof IrMemory) {
            regs.addAll(((IrMemory) dest).getUsedRegs());
        } else if (dest instanceof IrRegister) {
            regs.add((IrRegister) dest);
        }
        if (opt == IrBinaryOpt.MUL) {
            if (!regs.contains(vrax)) {
                regs.add(vrax);
            }
        } else if (opt == IrBinaryOpt.DIV || opt == IrBinaryOpt.MOD) {
            if (!regs.contains(vrax)) {
                regs.add(vrax);
            }
            if (!regs.contains(vrdx)) {
                regs.add(vrdx);
            }
        }
        return regs;
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        if (dest instanceof IrRegister) {
            regs.add((IrRegister) dest);
        }
        if (opt == IrBinaryOpt.MUL || opt == IrBinaryOpt.DIV || opt == IrBinaryOpt.MOD) {
            if (!regs.contains(vrax)) {
                regs.add(vrax);
            }
            if (!regs.contains(vrdx)) {
                regs.add(vrdx);
            }
        }
        return regs;
    }

    @Override
    public LinkedList<IrStackSlot> getStackSlots() {
        return defaultGetStackSlots(dest, src);
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
