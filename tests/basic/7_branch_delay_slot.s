    .set noat
    # .set noreorder
.text

_start:
    nop     
    li      $1,     0x1
    li      $0,     0x1
_j: 
    j       _jal
_jal:
    jal     _jalr
_jalr:
    li      $5,     _br
    jalr    $31,    $5

_br:
    bne     $1,     $0,             _test2

_test2:
    bgezal  $2,     4
_bgezal_ret:
    li      $5,     _bgezal_ret
    beq     $31,    $5,             _test3
    li      $7,     0xeeeeeeee              #error

_test3:
    li      $3,     0x80000000
    bltzal  $3,     4
    bgtz    $2,     4
    li      $4,     0x11111111
