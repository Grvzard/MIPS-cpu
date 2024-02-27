
_start:
    nop     
    addiu   $t0,        $t0,    0x5         # 0x26100005
    j       _br_cond                        # 0x08000004
_br_body:
    addiu   $t1,        $t1,    0x1         # 0x26310001
_br_cond:
    bne     $t0,        $t1,    _br_body    # 0x1611FFFE

    # result: $t0 == $t1 == 0x5
