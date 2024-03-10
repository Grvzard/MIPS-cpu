package mips.memory

import chisel3._
import chisel3.util._
import dataclass.data

object Sram {
  def dataMask(data: UInt, mask: UInt) = {
    Fill(8, mask(0)) & data(7, 0) |
      Fill(8, mask(1)) & data(15, 8) |
      Fill(8, mask(2)) & data(23, 16) |
      Fill(8, mask(3)) & data(31, 24)
  }
}

class Sram(width: Int) extends Module {
  import Sram._

  val io = IO(new Bundle {
    val en = Input(Bool())
    val wr = Input(Bool())
    val addr = Input(UInt(width.W))
    val wmask = Input(UInt(4.W))
    val wdata = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))
  })

  val mem = SyncReadMem(1 << width, UInt(32.W))
  io.rdata := DontCare

  when(io.en) {
    val rwPort = mem(io.addr)
    when(io.wr) {
      rwPort := dataMask(io.wdata, io.wmask) | dataMask(rwPort, ~io.wmask)
    }.otherwise {
      io.rdata := rwPort
    }
  }
}
