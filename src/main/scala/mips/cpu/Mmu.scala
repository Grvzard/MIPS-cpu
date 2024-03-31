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

  val addr = RegNext(io.in.addr)
  val wr = RegNext(io.in.wr)
  val wdata = RegNext(io.in.wdata)

  when(addr === "h_A000_0000".U) {
    io.dram.in.en := false.B
    io.dram := DontCare
    io.out := 0.U

    io.the7seg.num := wdata
    io.the7seg.wrEn := wr

    // printf(cf"-: ${wdata}%x\n")
  }.otherwise {
    io.the7seg.wrEn := false.B
    io.the7seg.num := 0.U

    io.dram.in := io.in
    io.out := io.dram.rdata
  }
}
