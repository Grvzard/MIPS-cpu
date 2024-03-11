package mips.cpu.stage

import chisel3._
import chisel3.util.Decoupled
import mips.cpu.IdState

class If(implicit iramWidth: Int) extends Module {
  // val fwIdJtarget = Input(UInt(32.W))
  // val fwIdSigJump = Input(Bool())
  val io = IO(new Bundle {
    val id = Decoupled(new IdState)
    val instrAddr = Output(UInt(iramWidth.W))
  })

  val pc = RegInit(0.U(32.W)) // TODO: change this to 0xbfc00000

  val nextPc = pc + 4.U
  io.instrAddr := pc
  io.id.bits.nextPc := nextPc
  io.id.valid := true.B
  when(io.id.ready) {
    pc := nextPc
  }

}
