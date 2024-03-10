package mips.general

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.general.Adder32

class Adder32Test extends AnyFreeSpec {
  "Adder should pass" in {
    simulate(new Adder32) { dut =>
      dut.io.a.poke(2.U)
      dut.io.b.poke(4.U)
      dut.io.sigSub.poke(true.B)
      dut.io.out.expect(7.U)

      dut.io.a.poke("h_ffff_ffff".U)
      dut.io.b.poke(0.U)
      dut.io.sigSub.poke(true.B)
      dut.io.cout.expect(1.U)
      dut.io.flgO.expect(false.B)
      dut.io.flgZ.expect(true.B)

    }
  }
}
