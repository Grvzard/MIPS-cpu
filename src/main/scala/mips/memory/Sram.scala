package mips.memory

import chisel3._
import chisel3.util._
import dataclass.data
import java.io.{File, FileInputStream}
import chisel3.util.experimental.loadMemoryFromFile

object dataMask {
  def apply(data: UInt, mask: UInt): UInt =
    Cat(Fill(8, mask(3)), Fill(8, mask(2)), Fill(8, mask(1)), Fill(8, mask(0))) & data
}

class SramInterface extends Bundle {
  val en = Input(Bool())
  val wr = Input(Bool())
  val addr = Input(UInt(32.W))
  val wmask = Input(UInt(4.W))
  val wdata = Input(UInt(32.W))
  val rdata = Output(UInt(32.W))
}

class Sram(addrWidth: Int, initFile: String = "") extends Module {
  val io = IO(new SramInterface)
  val debug = IO(Input(Bool()))

  val outBuf = RegInit(0.U(32.W))

  // private val mem = SyncReadMem(1 << addrWidth, UInt(32.W))
  val mem = RegInit({
    val bytes = {
      if (initFile != "") {
        val binFile = new FileInputStream(new File(initFile))
        val ret = binFile.readAllBytes()
        binFile.close()
        ret
      } else {
        Array()
      }
    }

    VecInit(
      Seq
        .from(
          bytes
            .padTo(bytes.length / 4 + 1, 0.toByte)
            .sliding(4, 4)
            .map(chs => {
              ("h" + chs.map(ch => f"$ch%02x").mkString).U(32.W)
            })
        )
        .padTo(1 << (addrWidth - 2), 0.U)
    )
  })
  io.rdata := outBuf

  when(debug) {
    printf(cf"${mem}\n")
    printf(cf"result(0x20): ${mem(32)}\n")
  }

  when(io.en) {
    val rwPort = mem(io.addr(addrWidth - 1, 2))
    when(io.wr) {
      rwPort := dataMask(io.wdata, io.wmask) | dataMask(rwPort, ~io.wmask)
    }.otherwise {
      outBuf := rwPort
    }
  }
}
