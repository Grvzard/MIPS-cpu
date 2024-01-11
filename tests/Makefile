all: 1.bin 2.bin 3.bin

%.bin: %_*.s
	@mips-linux-gnu-as -mips32 -o $<.o $<
	@mips-linux-gnu-objcopy -j .text $<.o $<.o
	@mips-linux-gnu-objcopy -O binary $<.o $<.bin
	@rm $<.o

.PHONY: clean
clean:
	rm *.bin
