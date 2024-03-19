package mips.cpu

import chisel3._

class Regfile(debug: Boolean = false) extends Module with RequireSyncReset {
  val io = IO(new Bundle {
    val raddr0, raddr1, waddr = Input(UInt(5.W))
    val wdata = Input(UInt(32.W))
    val wen = Input(Bool())
    val rdata0, rdata1 = Output(UInt(32.W))
  })

  val gpRegs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  io.rdata0 := gpRegs(io.raddr0)
  io.rdata1 := gpRegs(io.raddr1)

  when(io.wen & !(io.waddr === 0.U)) {
    gpRegs(io.waddr) := io.wdata
  }

  if (debug) {
    printf(cf"regfile- ${gpRegs}\n")
  }
}
