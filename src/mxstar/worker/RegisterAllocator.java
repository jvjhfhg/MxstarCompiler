package mxstar.worker;

import mxstar.ir.*;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.*;
import static mxstar.ir.IrRegisterSet.*;

import java.util.*;

public class RegisterAllocator {
    IrProgram irProgram;
    LinkedList<IrPhysicalRegister> generalRegisters = new LinkedList<>();

    public RegisterAllocator(IrProgram irProgram) {
        this.irProgram = irProgram;
        this.generalRegisters.add(rbx);
        this.generalRegisters.add(r10);
        this.generalRegisters.add(r11);
        this.generalRegisters.add(r12);
        this.generalRegisters.add(r13);
        this.generalRegisters.add(r14);
        this.generalRegisters.add(r15);
    }

    public void allocate() {
        for(IrFunction function : irProgram.functions)
            processFunction(function);
    }

    private IrPhysicalRegister getPhysical(IrOperand v) {
        if(v instanceof IrVirtualRegister)
            return ((IrVirtualRegister) v).allocatedPlace;
        else
            return null;
    }

    private void processFunction(IrFunction function) {
        for(IrBasicBlock bb : function.basicBlocks) {
            for(IrInstruction inst = bb.head; inst != null; inst = inst.next) {

                if(inst instanceof IrCall) continue;

                HashMap<IrRegister, IrRegister> renameMap = new HashMap<>();
                HashSet<IrRegister> allRegs = new HashSet<>();
                HashSet<IrRegister> usedRegs = new HashSet<>(inst.getUsedRegs());
                HashSet<IrRegister> definedRegs = new HashSet<>(inst.getDefRegs());
                allRegs.addAll(usedRegs);
                allRegs.addAll(definedRegs);

                for(IrRegister avr : allRegs) {
                    assert avr instanceof IrVirtualRegister;
                    IrVirtualRegister vr = (IrVirtualRegister) avr;
                    if(vr.allocatedPlace != null) continue;
                    if(vr.spillPlace == null)
                        vr.spillPlace = new IrStackSlot(vr.hint);
                }

                if(inst instanceof IrMove) {
                    IrMove move = (IrMove) inst;
                    IrAddress dest = move.dest;
                    IrOperand src = move.src;
                    IrPhysicalRegister pdest = getPhysical(dest);
                    IrPhysicalRegister psrc = getPhysical(src);
                    if(pdest != null && psrc != null) {
                        move.dest = pdest;
                        move.src = psrc;
                        continue;
                    } else if(pdest != null) {
                        move.dest = pdest;
                        if(move.src instanceof IrVirtualRegister) {
                            move.src = ((IrVirtualRegister) move.src).spillPlace;
                        } else if(move.src instanceof IrConstant) {
                        } else {
                            assert false;
                        }
                        continue;
                    } else if(psrc != null) {
                        move.src = psrc;
                        if(move.dest instanceof IrVirtualRegister) {
                            move.dest = ((IrVirtualRegister) move.dest).spillPlace;
                        } else {
                            assert false;
                        }
                        continue;
                    }
                }

                int cnt = 0;
                for (IrRegister reg : allRegs) {
                    if (!renameMap.containsKey(reg)) {
                        IrPhysicalRegister pr = ((IrVirtualRegister)reg).allocatedPlace;
                        if(pr == null)
                            renameMap.put(reg, generalRegisters.get(cnt++));
                        else {
                            renameMap.put(reg, pr);
                        }
                    }
                }
                inst.renameUsedReg(renameMap);
                inst.renameDefReg(renameMap);

                for (IrRegister reg : usedRegs) {
                    if(((IrVirtualRegister)reg).allocatedPlace == null)
                        inst.insertPrev(new IrMove(bb, renameMap.get(reg),((IrVirtualRegister) reg).spillPlace));
                }
                for (IrRegister reg : definedRegs) {
                    if(((IrVirtualRegister)reg).allocatedPlace == null) {
                        inst.insertNext(new IrMove(bb, ((IrVirtualRegister) reg).spillPlace, renameMap.get(reg)));
                        inst = inst.next;
                    }
                }
            }
        }
    }
}
