.text

_start:
    nop     
    clz     $3, $2
    li      $2, 0x80000000
    clo     $4, $2
    clz     $5, $2
