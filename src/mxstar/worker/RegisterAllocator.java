package mxstar.worker;

import mxstar.ir.*;
import mxstar.ir.instruction.*;
import mxstar.ir.operand.*;
import static mxstar.ir.IrRegisterSet.*;
import mxstar.worker.LivenessAnalyzer.Graph;

import java.util.*;

public class RegisterAllocator {
    private IrProgram irProgram;
    private static LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
    private LinkedList<IrPhysicalRegister> generalRegisters = new LinkedList<>();
    private int K;

    public RegisterAllocator(IrProgram irProgram) {
        this.irProgram = irProgram;
        for (IrPhysicalRegister physicalRegister : allRegs) {
            if (physicalRegister.name.equals("rsp") || physicalRegister.name.equals("rbp")) {
                continue;
            }
            generalRegisters.add(physicalRegister);
        }
        K = generalRegisters.size();
    }

    public void process() {
        for (IrFunction function : irProgram.functions) {
            this.function = function;
            processFunction();
        }
    }

    private LinkedList<IrVirtualRegister> trans(LinkedList<IrRegister> regs) {
        LinkedList<IrVirtualRegister> ret = new LinkedList<>();
        for (IrRegister reg : regs) {
            ret.add((IrVirtualRegister) reg);
        }
        return ret;
    }

    private IrFunction function;
    private Graph originalGraph;
    private Graph graph;
    private HashSet<IrVirtualRegister> simplifyWorklist;
    private HashSet<IrVirtualRegister> spillWorklist;
    private HashSet<IrVirtualRegister> spilledRegisters;
    private LinkedList<IrVirtualRegister> selectStack;
    private HashMap<IrVirtualRegister, IrPhysicalRegister> colors;

    private void init() {
        simplifyWorklist = new HashSet<>();
        spillWorklist = new HashSet<>();
        spilledRegisters = new HashSet<>();
        selectStack = new LinkedList<>();
        colors = new HashMap<>();

        for (IrVirtualRegister virtualRegister : graph.getAllRegisters()) {
            if (graph.getDegree(virtualRegister) < K) {
                simplifyWorklist.add(virtualRegister);
            } else {
                spillWorklist.add(virtualRegister);
            }
        }
    }

    private void simplify() {
        IrVirtualRegister register = simplifyWorklist.iterator().next();
        LinkedList<IrVirtualRegister> neighbors = new LinkedList<>(graph.getAdjacents(register));
        graph.removeRegister(register);
        for (IrVirtualRegister virtualRegister : neighbors) {
            if (graph.getDegree(virtualRegister) < K && spillWorklist.contains(virtualRegister)) {
                spillWorklist.remove(virtualRegister);
                simplifyWorklist.add(virtualRegister);
            }
        }
        simplifyWorklist.remove(register);
        selectStack.addFirst(register);
    }

    private void spill() {
        IrVirtualRegister candidate = null;
        int rank = -2;
        for (IrVirtualRegister virtualRegister : spillWorklist) {
            int currentRank = graph.getDegree(virtualRegister);
            if (virtualRegister.allocatedPlace != null) {
                currentRank = -1;
            }
            if (currentRank > rank) {
                candidate = virtualRegister;
                rank = currentRank;
            }
        }
        graph.removeRegister(candidate);
        spillWorklist.remove(candidate);
        selectStack.addFirst(candidate);
    }

    private void assignColors() {
        for (IrVirtualRegister virtualRegister : selectStack) {
            if (virtualRegister.allocatedPlace != null) {
                colors.put(virtualRegister, virtualRegister.allocatedPlace);
            }
        }
        for (IrVirtualRegister virtualRegister : selectStack) {
            if (virtualRegister.allocatedPlace != null) {
                continue;
            }
            HashSet<IrPhysicalRegister> validColors = new HashSet<>(generalRegisters);
            for (IrVirtualRegister neighbor : originalGraph.getAdjacents(virtualRegister)) {
                if (colors.containsKey(neighbor)) {
                    validColors.remove(colors.get(neighbor));
                }
            }
            if (validColors.isEmpty()) {
                spilledRegisters.add(virtualRegister);
            } else {
                IrPhysicalRegister physicalRegister = null;
                for (IrPhysicalRegister register : callerSave) {
                    if (validColors.contains(register)) {
                        physicalRegister = register;
                        break;
                    }
                }
                if (physicalRegister == null) {
                    physicalRegister = validColors.iterator().next();
                }
                colors.put(virtualRegister, physicalRegister);
            }
        }
    }

    private void rewriteFunction() {
        HashMap<IrVirtualRegister, IrMemory> spillPlaces = new HashMap<>();
        for (IrVirtualRegister virtualRegister : spilledRegisters) {
            if (virtualRegister.spillPlace != null) {
                spillPlaces.put(virtualRegister, virtualRegister.spillPlace);
            } else {
                spillPlaces.put(virtualRegister, new IrStackSlot(virtualRegister.hint));
            }
        }
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                LinkedList<IrVirtualRegister> used = new LinkedList<>(trans(instruction.getUsedRegs()));
                LinkedList<IrVirtualRegister> defined = new LinkedList<>(trans(instruction.getDefRegs()));
                HashMap<IrRegister, IrRegister> renameMap = new HashMap<>();
                used.retainAll(spilledRegisters);
                defined.retainAll(spilledRegisters);
                for (IrVirtualRegister register : used) {
                    if (!renameMap.containsKey(register)) {
                        renameMap.put(register, new IrVirtualRegister(""));
                    }
                }
                for (IrVirtualRegister register : defined) {
                    if (!renameMap.containsKey(register)) {
                        renameMap.put(register, new IrVirtualRegister(""));
                    }
                }
                instruction.renameUsedReg(renameMap);
                instruction.renameDefReg(renameMap);
                for (IrVirtualRegister register : used) {
                    instruction.insertPrev(new IrMove(instruction.basicBlock, renameMap.get(register), spillPlaces.get(register)));
                }
                for (IrVirtualRegister register : defined) {
                    instruction.insertNext(new IrMove(instruction.basicBlock, spillPlaces.get(register), renameMap.get(register)));
                    instruction = instruction.next;
                }
            }
        }
    }

    private void replaceRegisters() {
        HashMap<IrRegister, IrRegister> renameMap = new HashMap<>();
        for (HashMap.Entry<IrVirtualRegister, IrPhysicalRegister> entry : colors.entrySet()) {
            renameMap.put(entry.getKey(), entry.getValue());
        }
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                instruction.renameUsedReg(renameMap);
                instruction.renameDefReg(renameMap);
            }
        }
    }

    private void processFunction() {
        originalGraph = new Graph();
        while (true) {
            livenessAnalyzer.getInferenceGraph(function, originalGraph, null);
            graph = new Graph(originalGraph);
            init();
            do {
                if (!simplifyWorklist.isEmpty()) {
                    simplify();
                } else if (!spillWorklist.isEmpty()) {
                    spill();
                }
            } while (!simplifyWorklist.isEmpty() || !spillWorklist.isEmpty());
            assignColors();
            if (!spilledRegisters.isEmpty()) {
                rewriteFunction();
            } else {
                replaceRegisters();
                break;
            }
        }
        function.finalAllocation();
    }
}
