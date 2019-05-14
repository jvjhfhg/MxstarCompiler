package mxstar.ir;

import mxstar.ir.instruction.*;
import mxstar.ir.operand.*;

public interface IIrVisitor {
    void visit(IrProgram program);
    void visit(IrFunction function);
    void visit(IrBasicBlock basicBlock);

    void visit(IrBinaryInstruction instruction);
    void visit(IrUnaryInstruction instruction);
    void visit(IrMove instruction);
    void visit(IrPush instruction);
    void visit(IrPop instruction);
    void visit(IrCjump instruction);
    void visit(IrJump instruction);
    void visit(IrLea instruction);
    void visit(IrReturn instruction);
    void visit(IrCall instruction);
    void visit(IrCdq instruction);
    void visit(IrLeave instruction);

    void visit(IrMemory operand);
    void visit(IrVirtualRegister operand);
    void visit(IrPhysicalRegister operand);
    void visit(IrStackSlot operand);
    void visit(IrImmidiate operand);
    void visit(IrStaticData operand);
    void visit(IrFunctionAddress operand);
}
