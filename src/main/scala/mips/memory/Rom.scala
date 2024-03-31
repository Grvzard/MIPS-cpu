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
  inner.io.in.en := true.B
  inner.io.in.wr := false.B
  inner.io.in.wmask := 0.U
  inner.io.in.wdata := 0.U
  inner.debug := debug

  inner.io.in.addr := io.addr
  io.rdata := inner.io.rdata
}
