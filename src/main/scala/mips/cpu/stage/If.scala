package mips.cpu.stage

import chisel3._
import chisel3.util.Decoupled
import mips.cpu.{IdState, IdBranchForward}

class If extends Module {
  val io = IO(new Bundle {
    val id = Decoupled(new IdState)
    val fwIdBranch = Input(new IdBranchForward)
    val instrAddr = Output(UInt(32.W))
  })
  val debug = IO(Input(Bool()))

  private val pc = RegInit(0.U(32.W))

  val nextPc = pc + 4.U
  io.instrAddr := pc
  io.id.bits.nextPc := nextPc
  when(io.id.ready) {
    pc := Mux(io.fwIdBranch.sigJump, io.fwIdBranch.jumpTarget, nextPc)
  }
  io.id.valid := true.B

  when(debug) {
    printf(cf"pc: ${io.instrAddr}\n")
  }

}
