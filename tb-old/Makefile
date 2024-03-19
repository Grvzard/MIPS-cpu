%: %.sv
	@iverilog -g 2012 -Y .sv -y ../verilog -o tmp.vvp $<
	@vvp tmp.vvp -fst
	@rm tmp.vvp
	gtkwave dump.fst
