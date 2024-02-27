.text
main:
    nop     
    li      $a2,    0           # used to store the DRAM pointer

    li      $a0,    -16
    li      $a1,    3
    mult    $a0,    $a1
    jal     dump

    li      $a0,    0xff
    li      $a1,    0x8
    multu   $a0,    $a1
    jal     dump

    li      $a0,    5
    li      $a1,    3
    maddu   $a0,    $a1
    jal     dump

    msubu   $a0,    $a1
    jal     dump

    li      $a0,    -5
    li      $a1,    3
    msub    $a0,    $a1
    jal     dump

    madd    $a0,    $a1
    jal     dump

    mul     $t0,    $a0,    $a1
    sw      $t0,    0($a2)

    li      $a2,    64

    li      $a0,    10
    li      $a1,    3
    divu    $a0,    $a1
    jal     dump

    li      $a0,    10
    li      $a1,    -3
    div     $a0,    $a1
    jal     dump

    li      $a0,    -10
    li      $a1,    3
    div     $a0,    $a1
    jal     dump

    li      $a0,    -10
    li      $a1,    -3
    div     $a0,    $a1
    jal     dump

    j       end

dump:
    mflo    $t0
    mfhi    $t1
    sw      $t0,    0($a2)
    sw      $t1,    4($a2)
    addiu   $a2,    $a2,    8
    jr      $ra

end:
    nop     

    # mul results:
    # 00: ffffffd0 ffffffff 000007f8 00000000 00000807 00000000 000007f8 00000000
    # 08: 00000807 00000000 000007f8 00000000 fffffff1

    # div results:
    # 10: 00000003 00000001 fffffffd 00000001 fffffffd ffffffff 00000003 ffffffff
