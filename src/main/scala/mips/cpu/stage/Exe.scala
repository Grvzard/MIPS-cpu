package mips.cpu.stage

import chisel3._
import chisel3.util._
import mips.cpu.{ExeDataForward, ExeState, MemState}
import mips.cpu.{Alu, InstrDecoder}
import mips.memory.SramInterface

class Exe extends Module {
  implicit val ramWidth: Int = 8
  val io = IO(new Bundle {
    val exe = Flipped(Decoupled(new ExeState))
    val mem = Output(new MemState)
    val fwExeData = Output(new ExeDataForward)
    val dram = Flipped(new SramInterface)
  })
  val debug = IO(Input(Bool()))

  val out = io.mem
  val st = Reg(new ExeState)
  val sigs = st.exeSigs

  val alu = Module(new Alu)

  private val wire = Wire(new Bundle {
    val mduBusy = Bool()
    val dataOut = UInt(32.W)
  })

  io.exe.ready := !wire.mduBusy
  when(io.exe.fire) {
    st := io.exe.bits
  }.elsewhen(io.exe.ready) {
    st.exeSigs.aluOp := 0.U
    st.exeSigs.mduOp := 0.U
    st.exeSigs.regWr := false.B
    st.exeSigs.mem2reg := false.B
    st.exeSigs.memWr := false.B
  }

  wire.mduBusy := false.B

  alu.io.a := Mux(sigs.sigBranch, st.nextPc, st.busA)
  alu.io.b := Mux(sigs.sigBranch, 4.U, Mux(sigs.sigFromImm, st.imm, st.busB))
  alu.io.opcode := sigs.aluOp
  alu.io.shamt := sigs.shamt

  wire.dataOut := Mux(sigs.sigMov, st.busA, alu.io.result)

  out.memSigs := st.memSigs
  out.generalSigs := st.generalSigs
  out.data := wire.dataOut

  io.fwExeData.data := wire.dataOut
  io.fwExeData.sigs.mem2reg := sigs.mem2reg
  io.fwExeData.sigs.regWr := sigs.regWr
  io.fwExeData.sigs.rw := sigs.rw
  io.dram.addr := wire.dataOut
  io.dram.en := sigs.mem2reg | sigs.memWr
  io.dram.wr := sigs.memWr
  io.dram.wdata := st.busB
  io.dram.wmask := Cat(
    sigs.aaOp(1) ^ sigs.aaOp(0),
    sigs.aaOp(1) ^ sigs.aaOp(0),
    sigs.aaOp(1) | sigs.aaOp(0),
    1.U
  )

  when(debug) {
    printf(cf"exe- A: ${alu.io.a}, B: ${alu.io.b}, aluOp: ${alu.io.opcode}, aluOut: ${wire.dataOut}")
    when(st.exeSigs.regWr & st.exeSigs.rw =/= 0.U) {
      printf(cf", rw: ${sigs.rw}")
    }
    printf("\n")
  }
}
