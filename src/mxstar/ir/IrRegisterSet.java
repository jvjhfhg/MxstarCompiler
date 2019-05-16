package mxstar.ir;

import mxstar.ir.operand.IrPhysicalRegister;
import mxstar.ir.operand.IrVirtualRegister;

import java.util.LinkedList;

public class IrRegisterSet {
    public static IrPhysicalRegister rax;
    public static IrPhysicalRegister rcx;
    public static IrPhysicalRegister rdx;
    public static IrPhysicalRegister rbx;
    public static IrPhysicalRegister rsp;
    public static IrPhysicalRegister rbp;
    public static IrPhysicalRegister rsi;
    public static IrPhysicalRegister rdi;
    public static IrPhysicalRegister r8;
    public static IrPhysicalRegister r9;
    public static IrPhysicalRegister r10;
    public static IrPhysicalRegister r11;
    public static IrPhysicalRegister r12;
    public static IrPhysicalRegister r13;
    public static IrPhysicalRegister r14;
    public static IrPhysicalRegister r15;

    public static LinkedList<IrPhysicalRegister> allRegs;
    public static LinkedList<IrPhysicalRegister> callerSave;
    public static LinkedList<IrPhysicalRegister> calleeSave;
    public static LinkedList<IrPhysicalRegister> args;

    public static IrVirtualRegister vrax;
    public static IrVirtualRegister vrcx;
    public static IrVirtualRegister vrdx;
    public static IrVirtualRegister vrbx;
    public static IrVirtualRegister vrsp;
    public static IrVirtualRegister vrbp;
    public static IrVirtualRegister vrsi;
    public static IrVirtualRegister vrdi;
    public static IrVirtualRegister vr8;
    public static IrVirtualRegister vr9;
    public static IrVirtualRegister vr10;
    public static IrVirtualRegister vr11;
    public static IrVirtualRegister vr12;
    public static IrVirtualRegister vr13;
    public static IrVirtualRegister vr14;
    public static IrVirtualRegister vr15;

    public static LinkedList<IrVirtualRegister> vAllRegs;
    public static LinkedList<IrVirtualRegister> vCalleeSave;
    public static LinkedList<IrVirtualRegister> vCallerSave;
    public static LinkedList<IrVirtualRegister> vArgs;

    public static void init() {
        allRegs = new LinkedList<>();
        calleeSave = new LinkedList<>();
        callerSave = new LinkedList<>();
        args = new LinkedList<>();
        vAllRegs = new LinkedList<>();
        vCalleeSave = new LinkedList<>();
        vCallerSave = new LinkedList<>();
        vArgs = new LinkedList<>();
        String[] names = new String[] {
                "rax", "rcx", "rdx", "rbx", "rsp", "rbp", "rsi", "rdi", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15"
        };
        Boolean[] isCallerSave = new Boolean[] {
                true, true, true, false, null, null, true, true, true, true, true, true, false, false, false, false
        };

        for (int i = 0; i < 16; i++) {
            IrPhysicalRegister physicalRegister = new IrPhysicalRegister(names[i]);
            IrVirtualRegister virtualRegister = new IrVirtualRegister("v" + names[i]);
            virtualRegister.allocatedPlace = physicalRegister;
            allRegs.add(physicalRegister);
            vAllRegs.add(virtualRegister);
            if (isCallerSave[i] != null) {
                if (isCallerSave[i]) {
                    callerSave.add(physicalRegister);
                    vCallerSave.add(virtualRegister);
                } else {
                    calleeSave.add(physicalRegister);
                    vCalleeSave.add(virtualRegister);
                }
            }
        }

        rax = allRegs.get(0);   vrax = vAllRegs.get(0);
        rcx = allRegs.get(1);   vrcx = vAllRegs.get(1);
        rdx = allRegs.get(2);   vrdx = vAllRegs.get(2);
        rbx = allRegs.get(3);   vrbx = vAllRegs.get(3);
        rsp = allRegs.get(4);   vrsp = vAllRegs.get(4);
        rbp = allRegs.get(5);   vrbp = vAllRegs.get(5);
        rsi = allRegs.get(6);   vrsi = vAllRegs.get(6);
        rdi = allRegs.get(7);   vrdi = vAllRegs.get(7);
        r8  = allRegs.get(8);   vr8  = vAllRegs.get(8);
        r9  = allRegs.get(9);   vr9  = vAllRegs.get(9);
        r10 = allRegs.get(10);  vr10 = vAllRegs.get(10);
        r11 = allRegs.get(11);  vr11 = vAllRegs.get(11);
        r12 = allRegs.get(12);  vr12 = vAllRegs.get(12);
        r13 = allRegs.get(13);  vr13 = vAllRegs.get(13);
        r14 = allRegs.get(14);  vr14 = vAllRegs.get(14);
        r15 = allRegs.get(15);  vr15 = vAllRegs.get(15);
        args.add(rdi);          vArgs.add(vrdi);
        args.add(rsi);          vArgs.add(vrsi);
        args.add(rdx);          vArgs.add(vrdx);
        args.add(rcx);          vArgs.add(vrcx);
        args.add(r8);           vArgs.add(vr8);
        args.add(r9);           vArgs.add(vr9);
    }
}
