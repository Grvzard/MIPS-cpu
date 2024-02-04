    .set noat
    .set noreorder
.text

_start:
    nop     
    li      $1, 0xff
    movz    $2, $1,     $2
    movn    $3, $2,     $2
