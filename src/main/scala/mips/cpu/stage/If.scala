package mips.cpu.stage

import chisel3._
import chisel3.util._
import mips.cpu.{IdBranchForward, IdState}
import mips.cpu.PcInterface

class If extends Module {
  val io = IO(new Bundle {
    val pc = Flipped(new PcInterface)
    val id = Decoupled(new IdState)
    val fwIdBranch = Input(new IdBranchForward)
    val instrAddr = Output(UInt(32.W))
  })
  val debug = IO(Input(Bool()))

  io.instrAddr := io.pc.out
  io.id.bits.nextPc := io.pc.out + 4.U
  io.pc.in := Mux(io.fwIdBranch.sigJump, io.fwIdBranch.jumpTarget, io.id.bits.nextPc)
  io.pc.en := io.id.ready
  io.id.valid := true.B

  when(debug) {
    printf(
      cf"if- r: ${io.id.ready}, " +
        cf"jump: ${io.fwIdBranch.sigJump}, target: 0x${io.fwIdBranch.jumpTarget >> 2}%x\n"
    )
  }

}
