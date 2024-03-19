
.PHONY: verilog
verilog:
	mill mips-chisel.runMain mips.FileVerilogSoc

compile: verilog
	verilator --trace -I./verilog -cc Soc.sv --exe --build ./testbench/soc_tb.cpp
	./obj_dir/VSoc

wave:
	gtkwave wave.vcd

gtkw:
	gtkwave wave.gtkw
