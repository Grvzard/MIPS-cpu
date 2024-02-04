    .set noat
    .set noreorder
.text

_start:
    nop     
    li      $1, 0xff0000ff
    li      $2, 0x3
    sllv    $3, $1,         $2
    srl     $4, $1,         0x3
    srlv    $5, $1,         $2
    sra     $6, $1,         0x3
    srav    $7, $1,         $2
