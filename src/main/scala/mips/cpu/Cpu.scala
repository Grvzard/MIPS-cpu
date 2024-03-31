package mips.cpu

import chisel3._
import mips._
import mips.cpu.{stage => stg, _}
import mips.memory.{RomInterface, SramInterface}
import board.Display7SegIn

class CpuInterface extends Bundle {
  val iram = Flipped(new RomInterface)
  val dram = Flipped(new SramInterface)
  val the7seg = Flipped(new Display7SegIn)
}

class CpuDebugIn extends Bundle {
  val pc, regfile, fetch, decoder, exe = Input(Bool())
}

class Cpu extends Module {
  val io = IO(new CpuInterface)
  val debugOpts = IO(new CpuDebugIn)

  val pc = Module(new ProgramCounter)
  val stgIf = Module(new stg.If)
  val stgId = Module(new stg.Id)
  val stgExe = Module(new stg.Exe)
  val stgMem = Module(new stg.Mem)
  val mmu = Module(new Mmu)

  pc.debug := debugOpts.pc
  stgIf.debug := debugOpts.fetch
  stgId.debug := debugOpts.decoder
  stgExe.debug := debugOpts.exe

  stgIf.io.pc :<>= pc.io

  // IF/ID
  stgId.io.id <> stgIf.io.id

  // ID/EXE
  stgExe.io.exe <> stgId.io.exe

  // forwarding
  stgId.io.fwExeData := stgExe.io.fwExeData
  stgIf.io.fwIdBranch := stgId.io.fwIfBranch

  // EXE/MEM
  stgMem.io.mem := stgExe.io.mem

  stgId.io.wb := stgMem.io.wb

  // IRAM
  io.iram.addr := stgIf.io.instrAddr
  stgId.io.iramData := io.iram.rdata

  // DRAM
  mmu.io.dram :<>= io.dram
  mmu.io.in := stgExe.io.dram
  stgMem.io.dramData := mmu.io.out

  io.the7seg := mmu.io.the7seg

}
