





default rel

global __print
global __println
global __getString
global __getInt
global __toString
global __string_length
global __string_substring
global __string_parseInt
global __string_ord
global __stringConcat
global __stringCompare
global main

extern strcmp
extern __sprintf_chk
extern __stack_chk_fail
extern memcpy
extern malloc
extern __isoc99_scanf
extern puts
extern __printf_chk


SECTION .text   6

__print:
        lea     rdx, [rdi+8H]
        mov     esi, L_012
        mov     edi, 1
        xor     eax, eax
        jmp     __printf_chk


        nop





ALIGN   16

__println:
        add     rdi, 8
        jmp     puts






ALIGN   8

__getString:
        push    rbp
        push    rbx
        mov     esi, __buffer.3526
        mov     edi, L_012
        xor     eax, eax
        mov     ebx, __buffer.3526
        sub     rsp, 8
        call    __isoc99_scanf
L_001:  mov     edx, dword [rbx]
        add     rbx, 4
        lea     eax, [rdx-1010101H]
        not     edx
        and     eax, edx
        and     eax, 80808080H
        jz      L_001
        mov     edx, eax
        shr     edx, 16
        test    eax, 8080H
        cmove   eax, edx
        lea     rdx, [rbx+2H]
        mov     ecx, eax
        cmove   rbx, rdx
        add     cl, al
        sbb     rbx, L_011
        lea     edi, [rbx+8H]
        movsxd  rdi, edi
        call    malloc
        mov     rbp, rax
        lea     rdx, [rbx+1H]
        movsxd  rax, ebx
        lea     rdi, [rbp+8H]
        mov     qword [rbp], rax
        mov     esi, __buffer.3526
        call    memcpy
        add     rsp, 8
        mov     rax, rbp
        pop     rbx
        pop     rbp
        ret







ALIGN   16

__getInt:
        sub     rsp, 24
        mov     edi, L_013


        mov     rax, qword [fs:abs 28H]
        mov     qword [rsp+8H], rax
        xor     eax, eax
        mov     rsi, rsp
        call    __isoc99_scanf
        mov     rdx, qword [rsp+8H]


        xor     rdx, qword [fs:abs 28H]
        mov     rax, qword [rsp]
        jnz     L_002
        add     rsp, 24
        ret

L_002:  call    __stack_chk_fail
        nop
ALIGN   16

__toString:
        push    rbp
        push    rbx
        mov     rbp, rdi
        mov     edi, 32
        sub     rsp, 8
        call    malloc
        lea     rdi, [rax+8H]
        mov     rbx, rax
        mov     r8, rbp
        mov     ecx, L_013
        mov     edx, 24
        mov     esi, 1
        xor     eax, eax
        call    __sprintf_chk
        cdqe
        mov     qword [rbx], rax
        add     rsp, 8
        mov     rax, rbx
        pop     rbx
        pop     rbp
        ret







ALIGN   16

__string_length:
        mov     rax, qword [rdi]
        ret







ALIGN   16

__string_substring:
        push    r14
        push    r13
        mov     r13, rdi
        push    r12
        movsxd  r12, esi
        push    rbp
        sub     edx, r12d
        push    rbx
        lea     edi, [rdx+0AH]
        lea     r14d, [rdx+1H]
        mov     ebx, edx
        movsxd  rdi, edi
        call    malloc
        test    r14d, r14d
        mov     rbp, rax
        movsxd  rax, r14d
        mov     qword [rbp], rax
        jle     L_003
        mov     edx, ebx
        lea     rdi, [rbp+8H]
        lea     rsi, [r13+r12+8H]
        add     rdx, 1
        call    memcpy
L_003:  add     ebx, 9
        mov     rax, rbp
        movsxd  rbx, ebx
        mov     byte [rbp+rbx], 0
        pop     rbx
        pop     rbp
        pop     r12
        pop     r13
        pop     r14
        ret






ALIGN   8

__string_parseInt:
        movsx   edx, byte [rdi+8H]
        cmp     dl, 45
        jz      L_006
        lea     eax, [rdx-30H]
        cmp     al, 9
        ja      L_007
        lea     rcx, [rdi+8H]
        xor     edi, edi
L_004:  xor     eax, eax




ALIGN   16
L_005:  sub     edx, 48
        lea     rax, [rax+rax*4]
        add     rcx, 1
        movsxd  rdx, edx
        lea     rax, [rdx+rax*2]
        movsx   edx, byte [rcx]
        lea     esi, [rdx-30H]
        cmp     sil, 9
        jbe     L_005
        mov     rdx, rax
        neg     rdx
        test    edi, edi
        cmovne  rax, rdx
        ret





ALIGN   8
L_006:  movsx   edx, byte [rdi+9H]
        lea     rcx, [rdi+9H]
        lea     eax, [rdx-30H]
        cmp     al, 9
        ja      L_007
        mov     edi, 1
        jmp     L_004

L_007:  xor     eax, eax
        ret






ALIGN   8

__string_ord:
        movsx   rax, byte [rdi+rsi+8H]
        ret







ALIGN   16

__stringConcat:
        push    r15
        push    r14
        mov     r14, rdi
        push    r13
        push    r12
        mov     r13, rsi
        push    rbp
        push    rbx
        sub     rsp, 8
        mov     rbx, qword [rdi]
        mov     rbp, qword [rsi]
        lea     r15, [rbx+rbp]
        lea     rdi, [r15+9H]
        call    malloc
        test    rbx, rbx
        mov     r12, rax
        mov     qword [rax], r15
        jle     L_008
        lea     rdi, [rax+8H]
        lea     rsi, [r14+8H]
        mov     rdx, rbx
        call    memcpy
L_008:  test    rbp, rbp
        jle     L_010
        add     rbx, 8
        lea     rsi, [r13+8H]
        mov     rdx, rbp
        lea     rdi, [r12+rbx]
        call    memcpy
L_009:  add     rbp, r12
        mov     rax, r12
        mov     byte [rbp+rbx], 0
        add     rsp, 8
        pop     rbx
        pop     rbp
        pop     r12
        pop     r13
        pop     r14
        pop     r15
        ret





ALIGN   8
L_010:  add     rbx, 8
        jmp     L_009






ALIGN   8

__stringCompare:
        sub     rsp, 8
        add     rsi, 8
        add     rdi, 8
        call    strcmp
        add     rsp, 8
        cdqe
        ret



SECTION .data   


SECTION .bss    align=32

__buffer.3526:
        resb    3

L_011:
        resb    1048573


SECTION .text.unlikely 


SECTION .text.startup 6

main:
        xor     eax, eax
        jmp     __init



SECTION .rodata.str1.1 

L_012:
        db 25H, 73H, 00H

L_013:
        db 25H, 6CH, 64H, 00H


