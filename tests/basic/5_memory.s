    .set noreorder
.text

_start:
    nop     
    li      $t0,    0xffffffff
    sw      $t0,    0($0)
    sh      $t0,    4($0)
    sb      $t0,    8($0)

    lw      $t1,    0($0)
    lh      $t2,    0($0)
    lhu     $t3,    0($0)
    lb      $t4,    0($0)
    lbu     $t5,    0($0)
