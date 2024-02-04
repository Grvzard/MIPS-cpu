MEMORY
{
    IRAM : org = 0, len = 1024
    DRAM : org = 0x400, len = 1024
}
REGION_ALIAS("REGION_TEXT", IRAM);
REGION_ALIAS("REGION_DATA", DRAM);

INPUT(start.s.o)
ENTRY(_start)

SECTIONS
{
    . = 0x00000000;

    TEXT : {
        *(.text)
    } > REGION_TEXT

    DATA : {
        *(.rodata*)
        *(.data)
        *(.bss)
    } > REGION_DATA

    /DISCARD/ : {
        *(.MIPS.abiflags)
        *(.reginfo)
        *(.pdr)
     }
}
