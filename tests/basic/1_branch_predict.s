    .set noreorder

_start:
    nop     
    addiu   $16,        $16,    0x5         # 0x26100005
    nop     
    nop     
    nop     
    nop     
    j       _br_cond                        # 0x0800000C
_br_body:
    addiu   $17,        $17,    0x1         # 0x26310001
    nop     
    nop     
    nop     
    nop     
_br_cond:
    bne     $16,        $17,    _br_body    # 0x1611FFFA
