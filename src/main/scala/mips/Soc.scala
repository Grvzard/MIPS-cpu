package mips

import chisel3._
import mips.memory.{Rom, Sram}
import mips.cpu.Cpu
import mips.cpu.CpuDebugIn
import mips.general.ClockDiv
import board.Display7SegOut
import board.Display7Seg

class Soc extends Module {
  val io = IO(new Bundle {
    val the7seg = new Display7SegOut
  })

  val iramWidth: Int = 9
  val dramWidth: Int = 10

  val debugOpts = IO(new Bundle {
    val iramDump = Input(Bool())
    val dramDump = Input(Bool())
    val cpu = new CpuDebugIn
  })
  val clockDiv = Module(new ClockDiv(2))
  clockDiv.io.clockIn := clock

  // 256 instructions
  val iram = withClock(clockDiv.io.clockOut) { Module(new Rom(iramWidth, "tests/fib.ins.bin")) }
  val dram = withClock(clockDiv.io.clockOut) { Module(new Sram(dramWidth, "tests/fib.dat.bin")) }
  val the7seg = withClock(clockDiv.io.clockOut) { Module(new Display7Seg) }
  val cpu = withClock(clockDiv.io.clockOut) { Module(new Cpu) }

  iram.debug := debugOpts.iramDump
  dram.debug := debugOpts.dramDump
  cpu.debugOpts := debugOpts.cpu

  cpu.io.iram :<>= iram.io
  cpu.io.dram :<>= dram.io

  the7seg.io.in := cpu.io.the7seg
  io.the7seg := the7seg.io.out
}
