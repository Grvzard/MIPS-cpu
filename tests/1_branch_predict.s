nop
addiu $16, $16, 0x5  ; 0x26100005
nop
nop
nop
nop
j 0x12  ; 0x0800000c
addiu $17, $17, 0x1  ; 0x26310001
nop
nop
nop
nop
bne $16, $17, -6  ; 0x1611FFFA
