    .set reorder
.text
    .globl _start
_start:
    nop     
    addiu   $sp,    $zero,  0x400
    jal     _main
loop:
    b       loop
