%: %.sv
	@iverilog -y ../src -o tmp.vvp $<
	@vvp tmp.vvp -fst
	@rm tmp.vvp
	gtkwave dump.fst
