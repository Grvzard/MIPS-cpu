    .set noat
    .set noreorder

_start:
    nop     
    addiu   $1, $1, 0x2     # 0x24210002
    addiu   $1, $1, 0x2     # 0x24210002
    sub     $2, $1, $0      # 0x00201022
    ori     $3, $1, 0x1000  # 0x34231000
    addiu   $4, $1, 0x1     # 0x24240001