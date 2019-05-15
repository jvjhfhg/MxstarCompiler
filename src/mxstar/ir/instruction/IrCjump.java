package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.*;

import java.util.HashMap;
import java.util.LinkedList;

public class IrCjump extends IrInstruction {
    public enum IrCompareOpt {
        E, NE, G, GE, L, LE
    }

    public IrCompareOpt opt;
    public IrBasicBlock thenBlock;
    public IrBasicBlock elseBlock;
    public IrOperand lhs;
    public IrOperand rhs;

    public IrCjump(IrBasicBlock basicBlock, IrOperand lhs, IrCompareOpt opt, IrOperand rhs, IrBasicBlock thenBlock, IrBasicBlock elseBlock) {
        super(basicBlock);
        this.opt = opt;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void reverseThenElse() {
        switch (opt) {
            case E:
                opt = IrCompareOpt.NE; break;
            case NE:
                opt = IrCompareOpt.E; break;
            case G:
                opt = IrCompareOpt.LE; break;
            case GE:
                opt = IrCompareOpt.L; break;
            case L:
                opt = IrCompareOpt.GE; break;
            case LE:
                opt = IrCompareOpt.G; break;
        }
        IrBasicBlock tmp = thenBlock;
        thenBlock = elseBlock;
        elseBlock = tmp;
    }

    public void reverseSrcs() {
        switch (opt) {
            case E:
                opt = IrCompareOpt.E; break;
            case NE:
                opt = IrCompareOpt.NE; break;
            case G:
                opt = IrCompareOpt.L; break;
            case GE:
                opt = IrCompareOpt.LE; break;
            case L:
                opt = IrCompareOpt.G; break;
            case LE:
                opt = IrCompareOpt.GE; break;
        }
        IrOperand tmp = lhs;
        lhs = rhs;
        rhs = tmp;
    }

    public IrBasicBlock doCompare() {
        assert lhs instanceof IrImmidiate && rhs instanceof IrImmidiate;
        int l = ((IrImmidiate) lhs).value;
        int r = ((IrImmidiate) rhs).value;
        boolean res = false;
        switch (opt) {
            case E:
                res = (l == r); break;
            case NE:
                res = (l != r); break;
            case G:
                res = (l > r); break;
            case GE:
                res = (l >= r); break;
            case L:
                res = (l < r); break;
            case LE:
                res = (l <= r); break;
        }
        return res ? thenBlock : elseBlock;
    }

    @Override
    public void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap) {
        if (lhs instanceof IrMemory) {
            lhs = ((IrMemory) lhs).copy();
            ((IrMemory) lhs).renameUsedReg(renameMap);
        } else if (lhs instanceof IrRegister && renameMap.containsKey(lhs)) {
            lhs = renameMap.get(lhs);
        }
        if (rhs instanceof IrMemory) {
            rhs = ((IrMemory) rhs).copy();
            ((IrMemory) rhs).renameUsedReg(renameMap);
        } else if (rhs instanceof IrRegister && renameMap.containsKey(rhs)) {
            rhs = renameMap.get(rhs);
        }
    }

    @Override
    public void renameDefReg(HashMap<IrRegister, IrRegister> renameMap) {

    }

    @Override
    public LinkedList<IrRegister> getUsedRegs() {
        LinkedList<IrRegister> regs = new LinkedList<>();
        LinkedList<IrOperand> srcs = new LinkedList<>();
        srcs.add(lhs);
        srcs.add(rhs);
        for (IrOperand operand : srcs) {
            if (operand instanceof IrMemory) {
                regs.addAll(((IrMemory) operand).getUsedRegs());
            } else if (operand instanceof IrRegister) {
                regs.add((IrRegister) operand);
            }
        }
        return regs;
    }

    @Override
    public LinkedList<IrRegister> getDefRegs() {
        return new LinkedList<>();
    }

    @Override
    public LinkedList<IrStackSlot> getStackSlots() {
        return defaultGetStackSlots(lhs, rhs);
    }

    @Override
    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
