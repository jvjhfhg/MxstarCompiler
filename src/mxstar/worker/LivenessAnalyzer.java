package mxstar.worker;

import mxstar.ir.IrBasicBlock;
import mxstar.ir.IrFunction;
import mxstar.ir.instruction.IrCall;
import mxstar.ir.instruction.IrInstruction;
import mxstar.ir.instruction.IrMove;
import mxstar.ir.operand.IrRegister;
import mxstar.ir.operand.IrVirtualRegister;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;

public class LivenessAnalyzer {
    public static class Graph {
        private HashMap<IrVirtualRegister, HashSet<IrVirtualRegister>> G;

        Graph() {
            G = new HashMap<>();
        }

        Graph(Graph g) {
            G = new HashMap<>();
            for (IrVirtualRegister u : g.getAllRegisters()) {
                G.put(u, new HashSet<>(g.getAdjacents(u)));
            }
        }

        void addRegister(IrVirtualRegister register) {
            if (G.containsKey(register)) {
                return;
            }
            G.put(register, new HashSet<>());
        }

        void addRegisters(Collection<IrVirtualRegister> registers) {
            for (IrVirtualRegister register : registers) {
                addRegister(register);
            }
        }

        void addEdge(IrVirtualRegister u, IrVirtualRegister v) {
            if (u == v) return;
            G.get(u).add(v);
            G.get(v).add(u);
        }

        void removeEdge(IrVirtualRegister u, IrVirtualRegister v) {
            if (G.containsKey(u) && G.get(u).contains(v)) {
                G.get(u).remove(v);
                G.get(v).remove(u);
            }
        }

        void removeRegister(IrVirtualRegister u) {
            for (IrVirtualRegister v : getAdjacents(u)) {
                G.get(v).remove(u);
            }
            G.remove(u);
        }

        int getDegree(IrVirtualRegister register) {
            return G.containsKey(register) ? G.get(register).size() : 0;
        }

        boolean isLinked(IrVirtualRegister u, IrVirtualRegister v) {
            return G.containsKey(u) && G.get(u).contains(v);
        }

        void clear() {
            G.clear();
        }

        void forEach(BiConsumer<IrVirtualRegister, IrVirtualRegister> consumer) {
            for (IrVirtualRegister u : G.keySet()) {
                for (IrVirtualRegister v : G.get(u)) {
                    consumer.accept(u, v);
                }
            }
        }

        Collection<IrVirtualRegister> getAllRegisters() {
            return G.keySet();
        }

        Collection<IrVirtualRegister> getAdjacents(IrVirtualRegister register) {
            return G.getOrDefault(register, new HashSet<>());
        }
    }

    public HashMap<IrBasicBlock, HashSet<IrVirtualRegister>> liveOut;
    public HashMap<IrBasicBlock, HashSet<IrVirtualRegister>> usedRegisters;
    public HashMap<IrBasicBlock, HashSet<IrVirtualRegister>> defRegisters;

    private void init(IrFunction function) {
        liveOut = new HashMap<>();
        usedRegisters = new HashMap<>();
        defRegisters = new HashMap<>();
        for (IrBasicBlock basicBlock : function.basicBlocks) {
            liveOut.put(basicBlock, new HashSet<>());
            usedRegisters.put(basicBlock, new HashSet<>());
            defRegisters.put(basicBlock, new HashSet<>());
        }
    }

    private LinkedList<IrVirtualRegister> trans(Collection<IrRegister> regs) {
        LinkedList<IrVirtualRegister> ret = new LinkedList<>();
        for (IrRegister reg : regs) {
            ret.add((IrVirtualRegister) reg);
        }
        return ret;
    }

    private void initUsedAndDefRegisters(IrBasicBlock basicBlock, boolean isAfterAllocation) {
        HashSet<IrVirtualRegister> blockUsedRegisters = new HashSet<>();
        HashSet<IrVirtualRegister> blockDefRegisters = new HashSet<>();
        for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
            LinkedList<IrRegister> usedRegs;
            if (instruction instanceof IrCall && !isAfterAllocation) {
                usedRegs = ((IrCall) instruction).getCallUsed();
            } else {
                usedRegs = instruction.getUsedRegs();
            }
            for (IrVirtualRegister register : trans(usedRegs)) {
                if (!blockDefRegisters.contains(register)) {
                    blockUsedRegisters.add(register);
                }
            }
            blockDefRegisters.addAll(trans(instruction.getDefRegs()));
        }
        usedRegisters.put(basicBlock, blockUsedRegisters);
        defRegisters.put(basicBlock, blockDefRegisters);
    }

    private boolean isMoveBetweenRegisters(IrInstruction instruction) {
        if (instruction instanceof IrMove) {
            IrMove move = (IrMove) instruction;
            return move.dest instanceof IrVirtualRegister && move.src instanceof IrVirtualRegister;
        } else {
            return false;
        }
    }

    private void calcLiveOut(IrFunction function, boolean isAfterAllocation) {
        init(function);

        for (IrBasicBlock basicBlock : function.basicBlocks) {
            initUsedAndDefRegisters(basicBlock, isAfterAllocation);
        }

        boolean modified = true;
        while (modified) {
            modified = false;
            LinkedList<IrBasicBlock> basicBlocks = function.reversePostOrderOnReversedCFG;
            for (IrBasicBlock basicBlock : basicBlocks) {
                int oldSize = liveOut.get(basicBlock).size();
                for (IrBasicBlock successor : basicBlock.successors) {
                    HashSet<IrVirtualRegister> registers = new HashSet<>(liveOut.get(successor));
                    registers.removeAll(defRegisters.get(successor));
                    registers.addAll(usedRegisters.get(successor));
                    liveOut.get(basicBlock).addAll(registers);
                }
                modified = modified || liveOut.get(basicBlock).size() != oldSize;
            }
        }
    }

    public HashMap<IrBasicBlock, HashSet<IrVirtualRegister>> getLiveOut(IrFunction function) {
        calcLiveOut(function, false);
        return liveOut;
    }

    public void getInferenceGraph(IrFunction function, Graph inferenceGraph, Graph moveGraph) {
        calcLiveOut(function, true);

        inferenceGraph.clear();
        if (moveGraph != null) {
            moveGraph.clear();
        }

        for (IrBasicBlock basicBlock : function.basicBlocks) {
            for (IrInstruction instruction = basicBlock.head; instruction != null; instruction = instruction.next) {
                inferenceGraph.addRegisters(trans(instruction.getDefRegs()));
                inferenceGraph.addRegisters(trans(instruction.getUsedRegs()));
            }
        }

        for (IrBasicBlock basicBlock : function.basicBlocks) {
            HashSet<IrVirtualRegister> liveNow = new HashSet<>(liveOut.get(basicBlock));
            for (IrInstruction instruction = basicBlock.tail; instruction != null; instruction = instruction.prev) {
                boolean isMoveBetweenRegs = isMoveBetweenRegisters(instruction);
                for (IrVirtualRegister u : trans(instruction.getDefRegs())) {
                    for (IrVirtualRegister v : liveNow) {
                        if (isMoveBetweenRegs && moveGraph != null && ((IrMove) instruction).src == u) {
                            moveGraph.addEdge(u, v);
                            continue;
                        }
                        inferenceGraph.addEdge(u, v);
                    }
                }
                liveNow.removeAll(trans(instruction.getDefRegs()));
                liveNow.addAll(trans(instruction.getUsedRegs()));
            }
        }

        if (moveGraph != null) {
            inferenceGraph.forEach(moveGraph::removeEdge);
        }
    }
}
