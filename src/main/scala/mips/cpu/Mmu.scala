package mips.cpu

import chisel3._
import chisel3.util._
import board.Display7SegIn
import coursier.cache.internal.Downloader
import mips.memory.SramIn
import mips.memory.SramInterface

class MmuIo extends Bundle {
  val in = new SramIn
  val dram = Flipped(new SramInterface)
  val the7seg = Flipped(new Display7SegIn)
  val out = Output(UInt(32.W))
}

class Mmu extends Module {
  val io = IO(new MmuIo)

  val prevAddr = RegNext(io.in.addr)

  when(io.in.addr === "h_A000_0000".U) {
    io.dram.in.en := false.B
    io.dram.in.wr := DontCare
    io.dram.in.addr := DontCare
    io.dram.in.wmask := DontCare
    io.dram.in.wdata := DontCare

    io.the7seg.num := io.in.wdata
    io.the7seg.wrEn := io.in.wr

    // printf(cf"-: ${wdata}%x\n")
  }.otherwise {
    io.the7seg.wrEn := false.B
    io.the7seg.num := 0.U

    io.dram.in := io.in
  }

  when(prevAddr === "h_A000_0000".U) {
    io.out := 0.U
  }.otherwise {
    io.out := io.dram.rdata
  }
}
