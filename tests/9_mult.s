.text   
main:   
    li      $a0,    -16
    li      $a1,    3
    mult    $a0,    $a1
    mflo    $t0
    mfhi    $t1
    sw      $t0,    0
    sw      $t1,    4

    li      $a0,    0xff
    li      $a1,    0x8
    multu   $a0,    $a1
    mflo    $t0
    mfhi    $t1
    sw      $t0,    8
    sw      $t1,    12

    li      $a0,    5
    li      $a1,    3
    maddu   $a0,    $a1
    mflo    $t0
    mfhi    $t1
    sw      $t0,    16
    sw      $t1,    20

    msubu   $a0,    $a1
    mflo    $t0
    mfhi    $t1
    sw      $t0,    24
    sw      $t1,    28

    li      $a0,    -5
    li      $a1,    3
    msub    $a0,    $a1
    mflo    $t0
    mfhi    $t1
    sw      $t0,    32
    sw      $t1,    36

    madd    $a0,    $a1
    mflo    $t0
    mfhi    $t1
    sw      $t0,    40
    sw      $t1,    44
