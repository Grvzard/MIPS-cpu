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

  val busW =
    Fill(32, sigs.aaOp === "b00".U) & Cat(
      Fill(24, st.generalSigs.gpSig & io.dramData(7)),
      io.dramData(7, 0)
    ) |
      Fill(32, sigs.aaOp === "b01".U) &
      io.dramData |
      Fill(32, sigs.aaOp === "b11".U) & Cat(
        Fill(16, st.generalSigs.gpSig & io.dramData(15)),
        io.dramData(15, 0)
      )

  io.wb.data := Mux(sigs.mem2reg, busW, st.data)
  io.wb.sigs := st.memSigs
}
