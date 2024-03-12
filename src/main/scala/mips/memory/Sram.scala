package mips.memory

import chisel3._
import chisel3.util._
import dataclass.data
import java.io.{File, FileInputStream}
import chisel3.util.experimental.loadMemoryFromFile

object dataMask {
  def apply(data: UInt, mask: UInt): UInt = {
    Fill(8, mask(0)) & data(7, 0) |
      Fill(8, mask(1)) & data(15, 8) |
      Fill(8, mask(2)) & data(23, 16) |
      Fill(8, mask(3)) & data(31, 24)
  }
}

// object readBin {
//   def apply(file: String): Seq[UInt] = {
//     val binFile = new FileInputStream(new File(file))
//     val bytes = binFile.readAllBytes()
//     binFile.close()

//     Seq.from(bytes.map(ch => ch.U))
//     // println(bytes.map(ch => f"$ch%02x").mkString)
//   }
// }

class SramInterface extends Bundle {
  private val addrWidth = 8
  val en = Input(Bool())
  val wr = Input(Bool())
  val addr = Input(UInt(addrWidth.W))
  val wmask = Input(UInt(4.W))
  val wdata = Input(UInt(32.W))
  val rdata = Output(UInt(32.W))
}

class Sram(addrWidth: Int, initFile: String = "") extends Module {
  val io = IO(new SramInterface)
  val debug = IO(Input(Bool()))

  // private val mem = SyncReadMem(1 << addrWidth, UInt(32.W))
  val mem = RegInit({
    val binFile = new FileInputStream(new File(initFile))
    val bytes = binFile.readAllBytes()
    binFile.close()

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
        .padTo(1 << addrWidth, 0.U)
    )
  })
  io.rdata := DontCare

  when(debug) {
    printf(cf"${mem}\n")
    printf(cf"result(0x20): ${mem(20)}\n")
  }

  when(io.en) {
    val rwPort = mem(io.addr)
    when(io.wr) {
      rwPort := dataMask(io.wdata, io.wmask) | dataMask(rwPort, ~io.wmask)
    }.otherwise {
      io.rdata := rwPort
    }
  }
}
