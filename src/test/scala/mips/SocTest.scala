package mips

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.Soc

class SocTest extends AnyFreeSpec {
  "Soc should pass" in {
    simulate(new Soc) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      dut.debugOpts.cpu.pc.poke(true.B)
      dut.clock.step(2)
      dut.debugOpts.dramDump.poke(true.B)
      dut.clock.step()
      // dut.debugOpts.dramDump.poke(false.B)
      // dut.clock.step()

      println("")
    }
  }
}
