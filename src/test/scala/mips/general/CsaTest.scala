package mips.general

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.general.Csa

class CsaTest extends AnyFreeSpec {
  "CSA should pass" in {
    simulate(new Csa(64)) { dut =>
      dut.io.a.poke(2.U)
      dut.io.b.poke(4.U)
      dut.io.c.poke(6.U)
      dut.clock.step()
    }
  }
}
