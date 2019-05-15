package mxstar.worker;

import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.IrProgram;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.IrImmidiate;
import mxstar.ir.operand.IrPhysicalRegister;
import mxstar.ir.operand.IrStackSlot;
import mxstar.ir.operand.IrVirtualRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.*;

public class StackFrameBuilder {
    class Frame {
        public LinkedList<IrStackSlot> parameters = new LinkedList<>();
        public LinkedList<IrStackSlot> temporaries = new LinkedList<>();

        public int getFrameSize() {
            int bytes = 8 * (parameters.size() + temporaries.size());
            bytes = (bytes + 16 - 1) / 16 * 16;
            return bytes;
        }
    }

    private IrProgram irProgram;
    private HashMap<IrFunction, Frame> framesMap;

    public StackFrameBuilder(IrProgram irProgram) {
        this.irProgram = irProgram;
        this.framesMap = new HashMap<>();
    }

    public void process() {
        for (IrFunction function : irProgram.functions) {
            processFunction(function);
        }
    }

    private void processFunction(IrFunction function) {
        Frame frame = new Frame();
        framesMap.put(function, frame);
        LinkedList<IrVirtualRegister> parameters = function.parameters;
        int[] parameterRegs = new int[] {
                7, 6, 2, 1, 8, 9
        };
        for (int i = 0; i < parameters.size(); ++i) {
            if (i >= parameterRegs.length) {
                IrStackSlot stackSlot = (IrStackSlot) parameters.get(i).spillPlace;
                frame.parameters.add(stackSlot);
            }
        }
        HashSet<IrStackSlot> stackSlotSet = new HashSet<>();
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                LinkedList<IrStackSlot> stackSlots = instruction.getStackSlots();
                for (IrStackSlot stackSlot : stackSlots) {
                    if (!frame.parameters.contains(stackSlot)) {
                        stackSlotSet.add(stackSlot);
                    }
                }
            }
        }
        frame.temporaries.addAll(stackSlotSet);
        for (int i = 0; i < frame.parameters.size(); ++i) {
            IrStackSlot stackSlot = frame.parameters.get(i);
            assert stackSlot.base == null && stackSlot.constant == null;
            stackSlot.base = rbp;
            stackSlot.constant = new IrImmidiate(16 + 8 * i);
        }
        for (int i = 0; i < frame.temporaries.size(); ++i) {
            IrStackSlot stackSlot = frame.temporaries.get(i);
            assert stackSlot.base == null && stackSlot.constant == null;
            stackSlot.base = rbp;
            stackSlot.constant = new IrImmidiate(-8 - 8 * i);
        }

        IrInstruction headInstruction = function.frontBasicBlock.head;
        headInstruction.insertPrev(new IrPush(headInstruction.basicBlock, rbp));
        headInstruction.insertPrev(new IrMove(headInstruction.basicBlock, rbp, rsp));
        headInstruction.insertPrev(new IrBinaryInstruction(headInstruction.basicBlock, IrBinaryInstruction.IrBinaryOpt.SUB, rsp,
                new IrImmidiate(frame.getFrameSize())));
        HashSet<IrPhysicalRegister> needToSave = new HashSet<>(function.usedPhysicalRegisters);
        needToSave.retainAll(calleeSave);
        headInstruction = headInstruction.prev;
        for (IrPhysicalRegister physicalRegister : needToSave) {
            headInstruction.insertNext(new IrPush(headInstruction.basicBlock, physicalRegister));
        }

        IrReturn returnInstruction = (IrReturn) function.backBasicBlock.tail;
        for (IrPhysicalRegister physicalRegister : needToSave) {
            returnInstruction.insertPrev(new IrPop(returnInstruction.basicBlock, physicalRegister));
        }
        returnInstruction.insertPrev(new IrLeave(returnInstruction.basicBlock));
    }
}
