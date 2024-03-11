package mips.cpu

import chisel3._
import mips._
import mips.cpu.{stage => stg, _}

object Rom {
  def apply(width: Int) = {
    val mem = Module(new memory.Sram(width))
    mem.io.en := true.B
    mem.io.wr := false.B
    mem.io.wmask := 0.U
    mem.io.wdata := 0.U
    mem
  }
}

class Cpu extends Module {
  implicit val iramWidth: Int = 8
  // TODO: move Ram out
  val iram = Rom(iramWidth) // 256 instructions
  val dram = Module(new memory.Sram(8))

  val stgIf = Module(new stg.If)
  val stgId = Module(new stg.Id)
  val stgExe = Module(new stg.Exe)
  val stgMem = Module(new stg.Mem)

  // IF/ID
  stgId.io.id <> stgIf.io.id

  // IRAM
  iram.io.addr := stgIf.io.instrAddr
  stgId.io.iramData := iram.io.rdata

  // ID/EXE
  stgExe.io.exe <> stgId.io.exe

  // EXE/ID forwarding
  stgId.io.fw := stgExe.io.fw

  // EXE/MEM
  stgMem.io.mem := stgExe.io.mem

  stgId.io.wb := stgMem.io.wb

  // DRAM
  dram.io <> stgExe.io.dram
  stgMem.io.dramData := dram.io.rdata

  printf("-----------\n")
  printf(cf"${stgId.io.id}\n")
  printf(cf"${stgExe.io.exe}\n")
  printf(cf"${stgMem.io.mem}\n")
  printf(cf"${stgMem.io.wb}\n")
  printf("-----------\n")

}
