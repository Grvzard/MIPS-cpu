`timescale 1ns / 1ns

module test;
  reg [31:0] x_A;
  reg [31:0] x_B;
  reg x_Cin;
  wire [31:0] x_Result;
  wire x_flgSign;
  wire x_flgCarry;
  wire x_flgZero;
  wire x_flgOverflow;

  adder32 t (
      .A(x_A),
      .B(x_B),
      .Cin(x_Cin),
      .Result(x_Result),
      .flgSign(x_flgSign),
      .flgCarry(x_flgCarry),
      .flgZero(x_flgZero),
      .flgOverflow(x_flgOverflow)
  );

  initial begin
    $dumpvars;
  end

  initial begin
    x_A   = 32'b0;
    x_B   = 32'b0;
    x_Cin = 0;

    #5 x_A = 32'h66;
    x_B = 32'h66;
    #30 $finish;
  end

endmodule
