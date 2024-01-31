
# TC_cc := mips-linux-gnu-gcc -mips32
# TC_objcopy := mips-linux-gnu-objcopy
TC_cc := clang -target mips -mcpu=mips32
TC_objcopy := llvm-objcopy

tests := $(wildcard *.s)

all: $(tests:.s=.s.bin)

%.s.bin: %.s
	@${TC_cc} -c -o $<.o $<
	@${TC_objcopy} -j .text -O binary $<.o $@
	@rm $<.o

%.s: %.c
	@${TC_cc} -S -o $@ $<

%.o3.s: %.c
	@${TC_cc} -S -O3 -o $@ $<

clean:
	rm *.bin

.PHONY: all clean
