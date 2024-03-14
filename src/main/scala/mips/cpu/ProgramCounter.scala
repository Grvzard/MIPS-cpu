package mips.cpu

import chisel3._

class PcInterface extends Bundle {
  val in = Input(UInt(32.W))
  val en = Input(Bool())
  val out = Output(UInt(32.W))
}

class ProgramCounter extends Module {
  val io = IO(new PcInterface)
  val debug = IO(Input(Bool()))

  // val pc = Reg(UInt(32.W))
  val pc = RegInit(0.U(32.W))

  when(io.en) {
    pc := io.in
  }

  io.out := pc

  when(debug) {
    printf(
      cf"pc: 0x${pc >> 2}%x\n"
    )
  }

}
