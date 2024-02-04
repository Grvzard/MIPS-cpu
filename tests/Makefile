
# TC_cc := mips-linux-gnu-gcc -mips32
# TC_objcopy := mips-linux-gnu-objcopy
# TC_ld := mips-linux-gnu-ld
TC_cc := clang -target mips -mcpu=mips32
TC_objcopy := llvm-objcopy
TC_ld := ld.lld

tests_b := $(wildcard basic/[0-9]_*.s)

all: $(tests_b:.s=.s.bin)

%.s.bin: %.s.o
	@${TC_objcopy} -j .text -O binary $< $@
	@rm $<

%.ins.bin: %.a
	@${TC_objcopy} -j TEXT -O binary $< $@

%.dat.bin: %.a
	@${TC_objcopy} -j DATA -O binary $< $@

%.c.o: %.c
	@${TC_cc} -c -o $@ $<

%.s.o: %.s
	@${TC_cc} -c -o $@ $<

%.a: %.c.o start.s.o
	@${TC_ld} -Tlinker.ld -static -o $@ $<
	@rm $<

%.s: %.c
	@${TC_cc} -S -fno-builtin -o $@ $<

%.o3.s: %.c
	@${TC_cc} -S -O3 -o $@ $<

clean:
	rm *.bin *.o

.PHONY: all clean
