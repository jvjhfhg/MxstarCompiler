package mxstar.ir.instruction;

import mxstar.ir.IIrVisitor;
import mxstar.ir.IrBasicBlock;
import mxstar.ir.operand.IrOperand;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrStackSlot;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class IrInstruction {
    public IrBasicBlock basicBlock;
    public IrInstruction prev;
    public IrInstruction next;

    public IrInstruction() {
        this.basicBlock = null;
    }

    public IrInstruction(IrBasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }

    public void insertPrev(IrInstruction instruction) {
        if (prev != null) {
            prev.next = instruction;
        } else {
            basicBlock.head = instruction;
        }
        instruction.prev = prev;
        instruction.next = this;
        prev = instruction;
    }

    public void insertNext(IrInstruction instruction) {
        if (next != null) {
            next.prev = instruction;
        } else {
            basicBlock.tail = instruction;
        }
        instruction.prev = this;
        instruction.next = next;
        next = instruction;
    }

    public void delete() {
        if (prev == null && next == null) {
            basicBlock.head = basicBlock.tail = null;
        } else if (prev == null) {
            basicBlock.head = next;
            next.prev = null;
        } else if (next == null) {
            basicBlock.tail = prev;
            prev.next = null;
        } else {
            prev.next = next;
            next.prev = prev;
        }
    }

    public void replace(IrInstruction instruction) {
        if (prev == null && next == null) {
            basicBlock.head = basicBlock.tail = instruction;
            instruction.prev = instruction.next = null;
        } else if (prev == null) {
            basicBlock.head = instruction;
            next.prev = instruction;
            instruction.prev = null;
            instruction.next = next;
        } else if (next == null) {
            basicBlock.tail = instruction;
            prev.next = instruction;
            instruction.prev = prev;
            instruction.next = null;
        } else {
            prev.next = instruction;
            next.prev = instruction;
            instruction.prev = prev;
            instruction.next = next;
        }
    }

    public abstract void renameUsedReg(HashMap<IrRegister, IrRegister> renameMap);

    public abstract void renameDefReg(HashMap<IrRegister, IrRegister> renameMap);

    public abstract LinkedList<IrRegister> getUsedRegs();

    public abstract LinkedList<IrRegister> getDefRegs();

    public abstract LinkedList<IrStackSlot> getStackSlots();

    LinkedList<IrStackSlot> defaultGetStackSlots(IrOperand... operands) {
        LinkedList<IrStackSlot> stackSlots = new LinkedList<>();
        for (IrOperand operand : operands) {
            if (operand instanceof IrStackSlot) {
                stackSlots.add((IrStackSlot) operand);
            }
        }
        return stackSlots;
    }

    public abstract void accept(IIrVisitor visitor);
}
