package mips.general

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.general.Mult32

class Mult32Test extends AnyFreeSpec {
  "Mult should pass" in {
    simulate(new Mult32) { dut =>
      dut.io.x.poke(6.U)
      dut.io.y.poke(7.U)
      dut.io.sigSigned.poke(false.B)
      dut.clock.step()
      dut.io.result.expect(42.U)

      dut.io.x.poke(6.U)
      dut.io.y.poke("h_ffff_fff9".U) // -7
      dut.io.sigSigned.poke(true.B)
      dut.clock.step()
      dut.io.result.expect("h_ffff_ffff_ffff_ffd6".U) // -42

      dut.io.x.poke("h_ffff_fffa".U) // -6
      dut.io.y.poke(7.U)
      dut.io.sigSigned.poke(true.B)
      dut.clock.step()
      dut.io.result.expect("h_ffff_ffff_ffff_ffd6".U) // -42

      dut.io.x.poke("h_ffff_fffa".U)
      dut.io.y.poke("h_ffff_fff9".U)
      dut.io.sigSigned.poke(true.B)
      dut.clock.step()
      dut.io.result.expect(42.U)
    }
  }
}
