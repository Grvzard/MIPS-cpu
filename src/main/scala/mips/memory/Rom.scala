package mips.memory

import chisel3._

class RomInterface extends Bundle {
  val addr = Input(UInt(32.W))
  val rdata = Output(UInt(32.W))
}

class Rom(addrWidth: Int, initFile: String = "") extends Module {
  val io = IO(new RomInterface)
  val debug = IO(Input(Bool()))

  val inner = Module(new Sram(addrWidth, initFile))
  inner.io.en := true.B
  inner.io.wr := false.B
  inner.io.wmask := 0.U
  inner.io.wdata := 0.U
  inner.debug := debug

  inner.io.addr := io.addr
  io.rdata := inner.io.rdata
}
