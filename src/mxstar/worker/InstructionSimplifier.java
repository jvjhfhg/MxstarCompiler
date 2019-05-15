package mxstar.worker;

import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.IrProgram;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrVirtualRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class InstructionSimplifier {
    private IrProgram irProgram;
    private LivenessAnalyzer livenessAnalyzer;
    private HashMap<IrBasicBlock, HashSet<IrVirtualRegister>> liveOut;

    public InstructionSimplifier(IrProgram irProgram) {
        this.irProgram = irProgram;
        this.livenessAnalyzer = new LivenessAnalyzer();
    }

    public void process() {
        for (IrFunction function : irProgram.functions) {
            processFunction(function);
        }
    }

    private LinkedList<IrVirtualRegister> trans(LinkedList<IrRegister> regs) {
        LinkedList<IrVirtualRegister> ret = new LinkedList<>();
        for (IrRegister reg : regs) {
            ret.add((IrVirtualRegister) reg);
        }
        return ret;
    }

    private boolean isRemovable(IrInstruction instruction) {
        return !(instruction instanceof IrReturn
                || instruction instanceof IrLeave
                || instruction instanceof IrCall
                || instruction instanceof IrCdq
                || instruction instanceof IrPush
                || instruction instanceof IrPop
                || instruction instanceof IrJump
                || instruction instanceof IrCjump);
    }

    private void processFunction(IrFunction function) {
        liveOut = livenessAnalyzer.getLiveOut(function);
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            HashSet<IrVirtualRegister> liveSet = new HashSet<>(liveOut.get(basicBlock));
            for (IrInstruction instruction = basicBlock.tail; instruction != null; instruction = instruction.prev) {
                LinkedList<IrRegister> usedSet = instruction instanceof IrCall ? ((IrCall) instruction).getCallUsed() : instruction.getUsedRegs();
                LinkedList<IrRegister> definedSet = instruction.getDefRegs();
                boolean dead = true;
                if (definedSet.isEmpty()) {
                    dead = false;
                }
                for (IrRegister register : definedSet) {
                    IrVirtualRegister virtualRegister = (IrVirtualRegister) register;
                    if (!dead) {
                        break;
                    }
                    if (liveSet.contains(virtualRegister) || virtualRegister.spillPlace != null) {
                        dead = false;
                        break;
                    }
                }
                if (dead && isRemovable(instruction)) {
                    instruction.delete();
                } else {
                    liveSet.removeAll(trans(definedSet));
                    liveSet.addAll(trans(usedSet));
                }
            }
        }
    }
}
