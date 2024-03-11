package mips.cpu.stage

import chisel3._
import chisel3.util._
import mips.cpu.{MemState, WriteBack}
import mips.cpu.InstrDecoder
import mips.cpu.Regfile

class Mem extends Module {
  val io = IO(new Bundle {
    val mem = Input(new MemState)
    val dramData = Input(UInt(32.W))
    val wb = Output(new WriteBack)
  })

  val st = Reg(new MemState)
  st := io.mem
  val sigs = st.memSigs

  io.wb.data := Mux(sigs.mem2reg, io.dramData, io.mem.data)
  io.wb.sigs := sigs
}
