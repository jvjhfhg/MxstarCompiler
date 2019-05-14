package mxstar.ir;

import mxstar.ir.instruction.*;

import java.util.LinkedList;

public class IrBasicBlock {
    public String label;
    public IrFunction function;
    public IrInstruction head;
    public IrInstruction tail;
    public LinkedList<IrBasicBlock> predecessors;
    public LinkedList<IrBasicBlock> successors;

    public IrBasicBlock(IrFunction function, String label) {
        this.function = function;
        this.label = label;
        this.predecessors = new LinkedList<>();
        this.successors = new LinkedList<>();
    }

    public boolean isEnd() {
        return tail instanceof IrReturn || tail instanceof IrJump || tail instanceof IrCjump;
    }

    public void prepend(IrInstruction instruction) {
        if (tail == null) {
            instruction.prev = instruction.next = null;
            head = tail = instruction;
        } else {
            head.insertPrev(instruction);
        }
    }

    public void append(IrInstruction instruction) {
        if (isEnd()) {
            return;
        }
        if (head == null) {
            instruction.prev = instruction.next = null;
            head = tail = instruction;
        } else {
            tail.insertNext(instruction);
        }
    }

    public void accept(IIrVisitor visitor) {
        visitor.visit(this);
    }
}
