    .set noat
    .set noreorder
.text
 
_start:
    nop     
    li      $1, 0x66
    sw      $1, 0($0)
    lw      $2, 0($0)
    add     $3, $1,     $2
