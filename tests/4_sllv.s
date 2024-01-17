    .set noat
    .set noreorder
.text

_start:
    nop     
    addiu   $2, $2, 0xff
    addiu   $3, $3, 0x3
    sllv    $1, $2, $3
