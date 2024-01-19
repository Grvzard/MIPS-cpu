`timescale 1ns / 1ns

module test;
  reg aclr;
  reg clk;
  reg [7:0] addr_a;
  reg [7:0] addr_b;
  reg [7:0] addr_w;
  reg wren;
  reg [31:0] data_w;
  wire [31:0] data_a;
  wire [31:0] data_b;

  regfiles t (
      .aclr(aclr),
      .clk(clk),
      .addr_a(addr_a),
      .addr_b(addr_b),
      .addr_w(addr_w),
      .wren(wren),
      .data_w(data_w),
      .data_a(data_a),
      .data_b(data_b)
  );

  initial begin
    clk = 0;
    forever #5 clk = ~clk;
  end

  integer i;
  initial begin
    $dumpvars;
    for (i = 0; i < 32; i = i + 1) $dumpvars(0, t.gpregs[i]);
  end

  initial begin
    aclr   = 1;
    addr_a = 5'b0;
    addr_b = 5'b0;
    addr_w = 5'b0;
    wren   = 0;
    data_w = 32'b0;

    @(posedge clk) aclr = 0;
    addr_w = 5'b00000;
    data_w = 32'hf0f0f0f0;
    wren   = 1;

    @(posedge clk) addr_w = 5'b00001;
    data_w = 32'hf0f0f0f0;
    wren   = 1;

    @(posedge clk) addr_a = 5'b00001;
    addr_b = 5'b00001;
    wren   = 0;

    @(negedge clk) $finish;
  end

endmodule
