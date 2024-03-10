%: %.sv
	@iverilog -g 2012 -y ../src -o tmp.vvp $<
	@vvp tmp.vvp -fst
	@rm tmp.vvp
	gtkwave dump.fst
