module adder32(
  input wire [31:0] A,
  input wire [31:0] B,
  input wire Cin,
  output wire [31:0] Result,
  output wire flgSign,
  output wire flgCarry,
  output wire flgZero,
  output wire flgOverflow
);
wire [31:0] F;
wire Cout;

assign {Cout, F} = A + B + Cin;

assign Result = F;
assign flgSign = F[31];
assign flgCarry = Cin ^ Cout;
assign flgZero = ~|F;
assign flgOverflow = ~A[31] & ~B[31] & F[31] || A[31] & B[31] & ~F[31];

endmodule