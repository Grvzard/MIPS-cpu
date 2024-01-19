module regfiles (
    input wire aclr,
    input wire clk,
    input wire [7:0] addr_a,
    input wire [7:0] addr_b,
    input wire [7:0] addr_w,
    input wire wren,
    input wire [31:0] data_w,
    output wire [31:0] data_a,
    output wire [31:0] data_b
);
  integer i;
  reg [31:0] gpregs[0:31];

  always @(posedge clk, posedge aclr) begin
    if (aclr) begin
      for (i = 0; i < 32; i = i + 1) begin
        gpregs[i] <= 32'b0;
      end
    end
    else if (wren && addr_w != 5'b0) begin
      gpregs[addr_w] <= data_w;
    end
  end

  assign data_a = gpregs[addr_a];
  assign data_b = gpregs[addr_b];

endmodule
