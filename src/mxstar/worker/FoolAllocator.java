package mxstar.worker;

import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.IrProgram;
import mxstar.ir.instruction.IrCall;
import mxstar.ir.instruction.IrInstruction;
import mxstar.ir.instruction.IrMove;
import mxstar.ir.operand.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static mxstar.ir.IrRegisterSet.*;

public class FoolAllocator {
    private IrProgram irProgram;
    private LinkedList<IrPhysicalRegister> generalRegisters = new LinkedList<>();

    public FoolAllocator(IrProgram irProgram) {
        this.irProgram = irProgram;
        this.generalRegisters.add(rbx);
        this.generalRegisters.add(r10);
        this.generalRegisters.add(r11);
        this.generalRegisters.add(r12);
        this.generalRegisters.add(r13);
        this.generalRegisters.add(r14);
        this.generalRegisters.add(r15);
    }

    private IrPhysicalRegister getPhysicalRegister(IrOperand operand) {
        if (operand instanceof IrVirtualRegister) {
            return ((IrVirtualRegister) operand).allocatedPlace;
        } else {
            return null;
        }
    }

    public void process() {
        for (IrFunction function : irProgram.functions) {
            processFunction(function);
        }
    }

    private void processFunction(IrFunction function) {
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                if (instruction instanceof IrCall) {
                    continue;
                }

                HashMap<IrRegister, IrRegister> renameMap = new HashMap<>();
                HashSet<IrRegister> allRegs = new HashSet<>();
                HashSet<IrRegister> usedRegs = new HashSet<>(instruction.getUsedRegs());
                HashSet<IrRegister> definedRegs = new HashSet<>(instruction.getDefRegs());
                allRegs.addAll(usedRegs);
                allRegs.addAll(definedRegs);

                for (IrRegister register : allRegs) {
                    assert register instanceof IrVirtualRegister;
                    IrVirtualRegister virtualRegister = (IrVirtualRegister) register;
                    if (virtualRegister.allocatedPlace != null) {
                        continue;
                    }
                    if (virtualRegister.spillPlace == null) {
                        virtualRegister.spillPlace = new IrStackSlot(virtualRegister.hint);
                    }
                }

                if (instruction instanceof IrMove) {
                    IrMove move = (IrMove) instruction;
                    IrAddress dest = move.dest;
                    IrOperand src = move.src;
                    IrPhysicalRegister pDest = getPhysicalRegister(dest);
                    IrPhysicalRegister pSrc = getPhysicalRegister(src);
                    if (pDest != null && pSrc != null) {
                        move.dest = pDest;
                        move.src = pSrc;
                        continue;
                    } else if (pDest != null) {
                        move.dest = pDest;
                        if (move.src instanceof IrVirtualRegister) {
                            move.src = ((IrVirtualRegister) move.src).spillPlace;
                        }
                        continue;
                    } else if (pSrc != null) {
                        move.src = pSrc;
                        if (move.dest instanceof IrVirtualRegister) {
                            move.dest = ((IrVirtualRegister) move.dest).spillPlace;
                        }
                        continue;
                    }
                }

                int cnt = 0;
                for (IrRegister register : allRegs) {
                    if (!renameMap.containsKey(register)) {
                        IrPhysicalRegister physicalRegister = ((IrVirtualRegister) register).allocatedPlace;
                        if (physicalRegister == null) {
                            renameMap.put(register, generalRegisters.get(cnt++));
                        } else {
                            renameMap.put(register, physicalRegister);
                        }
                    }
                }

                instruction.renameUsedReg(renameMap);
                instruction.renameDefReg(renameMap);
                for (IrRegister register : usedRegs) {
                    if (((IrVirtualRegister) register).allocatedPlace == null) {
                        instruction.insertPrev(new IrMove(basicBlock, renameMap.get(register), ((IrVirtualRegister) register).spillPlace));
                    }
                }
                for (IrRegister register : definedRegs) {
                    if (((IrVirtualRegister) register).allocatedPlace == null) {
                        instruction.insertNext(new IrMove(basicBlock, ((IrVirtualRegister) register).spillPlace, renameMap.get(register)));
                        instruction = instruction.next;
                    }
                }
            }
        }
    }
}
