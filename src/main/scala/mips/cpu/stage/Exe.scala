package mips.cpu.stage

import chisel3._
import chisel3.util._
import mips.cpu.{ExeDataForward, ExeState, MemState}
import mips.cpu.{Alu, InstrDecoder}
import mips.memory.SramPort

class Exe extends Module {
  implicit val ramWidth: Int = 8
  val io = IO(new Bundle {
    val exe = Flipped(Decoupled(new ExeState))
    val mem = Output(new MemState)
    val fw = Output(new ExeDataForward)
    val dram = Flipped(new SramPort)
  })

  val out = io.mem
  val st = Reg(new ExeState)
  val sigs = st.exeSigs
  io.exe.ready := true.B // for now
  when(io.exe.valid) {
    st := io.exe.bits
  }
  // out := DontCare

  val alu = Module(new Alu)
  val dataOut = Wire(UInt(32.W))

  // private val wire = Wire(new Bundle {})

  alu.io.a := Mux(sigs.sigBranch, st.nextPc, st.busA)
  alu.io.b := Mux(sigs.sigBranch, 4.U, st.busB)
  alu.io.opcode := sigs.aluOp
  alu.io.shamt := sigs.shamt
  alu.io.result := DontCare

  dataOut := Mux(sigs.sigMov, st.busA, alu.io.result)

  out.memSigs := st.memSigs
  out.generalSigs := st.generalSigs
  out.data := dataOut

  io.fw.data := dataOut
  io.fw.sigs.mem2reg := sigs.mem2reg
  io.fw.sigs.regWr := sigs.regWr
  io.fw.sigs.rw := sigs.rw
  io.dram.addr := dataOut
  io.dram.en := sigs.mem2reg | sigs.memWr
  io.dram.wr := sigs.memWr
  io.dram.wdata := st.busB
  io.dram.wmask := Cat(
    sigs.aaOp(1) ^ sigs.aaOp(0),
    sigs.aaOp(1) ^ sigs.aaOp(0),
    sigs.aaOp(1) | sigs.aaOp(0),
    1.U
  )

}
