
# TC_as := mips-linux-gnu-as -mips32
# TC_cc := mips-linux-gnu-gcc -mips32
# TC_objcopy := mips-linux-gnu-objcopy
# TC_ld := mips-linux-gnu-ld
TC_as := clang -target mips -mcpu=mips32 -c
TC_cc := clang -target mips -mcpu=mips32
TC_objcopy := llvm-objcopy
TC_ld := ld.lld
# TC_as := zig cc -target mips-linux-gnueabi -c
# TC_cc := zig cc -target mips-linux-gnueabi
# TC_objcopy := zig objcopy
# TC_ld := zig cc -target mips-linux-gnueabi

tests_b := $(wildcard basic/[0-9]_*.s)

all: $(tests_b:.s=.s.bin) fib.c.bin

%.s.bin: %.s.o
	@${TC_objcopy} -j .text -O binary $< $@
	@rm $<

fib.c.bin: fib.ins.bin fib.dat.bin

%.ins.bin: %.a
	@${TC_objcopy} -j TEXT -O binary $< $@

%.dat.bin: %.a
	@${TC_objcopy} -j DATA -O binary $< $@

%.c.o: %.c
	@${TC_cc} -c -o $@ $<

%.s.o: %.s
	@${TC_as} -o $@ $<

%.a: %.c.o start.s.o
	@${TC_ld} -Tlinker.ld -static -o $@ $<

%.s: %.c
	@${TC_cc} -S -fno-builtin -o $@ $<

%.o3.s: %.c
	@${TC_cc} -S -O3 -o $@ $<

clean:
	rm *.bin basic/*.bin

.PHONY: all clean
