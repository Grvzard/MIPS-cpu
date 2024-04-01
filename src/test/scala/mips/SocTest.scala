package mips

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.Soc

class SocTest extends AnyFreeSpec {
  "Soc should pass" in {
    simulate(new Soc) { dut =>
      val cycle = 2
      dut.reset.poke(true.B)
      dut.clock.step(5 * cycle)
      dut.reset.poke(false.B)

      dut.clock.step(240 * cycle)
      dut.debugOpts.cpu.pc.poke(true.B)
      dut.debugOpts.cpu.decoder.poke(true.B)
      dut.debugOpts.cpu.exe.poke(true.B)
      dut.clock.step(10 * cycle)

      dut.debugOpts.dramDump.poke(true.B)
      dut.clock.step(cycle)

      println("")
    }
  }
}
