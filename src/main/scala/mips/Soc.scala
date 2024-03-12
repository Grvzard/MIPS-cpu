package mips

import chisel3._
import mips.memory.{Rom, Sram}
import mips.cpu.Cpu
import mips.cpu.CpuDebugIn

class Soc extends Module {
  val iramWidth: Int = 8
  val dramWidth: Int = 8

  val debugOpts = IO(new Bundle {
    val iramDump = Input(Bool())
    val dramDump = Input(Bool())
    val cpu = new CpuDebugIn
  })

  val iram = Module(new Rom(iramWidth, "tests/fib.ins.bin")) // 256 instructions
  val dram = Module(new Sram(dramWidth, "tests/fib.dat.bin"))
  val cpu = Module(new Cpu)

  iram.debug := debugOpts.iramDump
  dram.debug := debugOpts.dramDump
  cpu.debugOpts := debugOpts.cpu
  
  cpu.io.iram :<>= iram.io
  cpu.io.dram :<>= dram.io
}
