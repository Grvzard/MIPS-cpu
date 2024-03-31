package board

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import board.Display7Seg

class Display7SegTest extends AnyFreeSpec {
  "Soc should pass" in {
    simulate(new Display7Seg) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step(1)
      dut.reset.poke(false.B)

      dut.io.in.wrEn.poke(true.B)
      dut.io.in.num.poke("h_1234_6789".U)
      dut.clock.step()
      dut.io.in.wrEn.poke(false.B)

      dut.clock.step(8)

      println("")
    }
  }
}
