// rom256x32
module pseudo_rom (
    input  wire [ 7:0] addr,
    output wire [31:0] data
);

  reg [31:0] rom[0:255];
  assign data = rom[addr];

endmodule
