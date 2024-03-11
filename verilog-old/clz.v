
module clz (
    input  wire [31:0] in,
    output wire [31:0] out
);
  wire [7:0] a;
  wire [1:0] Z [0:7];

  genvar i;
  generate
    for (i = 0; i < 8; i = i + 1) begin : NLCgen
      NLC_ nlc_network (
          .x(in[4*i+3:4*i]),
          .a(a[7-i]),
          .Z(Z[7-i])
      );
    end
  endgenerate

  wire Q = &a;
  wire [2:0] y;
  assign y[2] = a[0] & a[1] & a[2] & a[3];
  assign y[1] = a[0] & a[1] & (~a[2] | ~a[3] | (a[4] & a[5]));
  assign y[0] = (a[0] & (~a[1] | (a[2] & ~a[3]))) | (a[0] & a[2] & a[4] & (~a[5] | a[6]));

  assign out  = Q ? 32'h20 : {27'b0, y, Z[y]};

endmodule

module NLC_ (
    input wire [3:0] x,
    output wire a,
    output wire [1:0] Z
);
  assign a = ~|x;
  assign Z[1] = ~(x[3] | x[2]);
  assign Z[0] = ~((~x[2] & x[1]) | x[3]);
endmodule
