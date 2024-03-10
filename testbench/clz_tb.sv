`timescale 1ns / 1ns

module test;
  reg  [31:0] in;
  wire [31:0] out;

  initial begin
    $dumpvars;
  end

  clz t (
      .in (in),
      .out(out)
  );

  initial begin
    in = 32'h8000_ffff;

    for (int i = 0; i < 32; i++) begin
      #5 in = in >> 1;
    end
    #5 $finish;
  end

endmodule
