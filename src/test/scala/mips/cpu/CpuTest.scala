package mips.cpu

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.cpu.Cpu

class CpuTest extends AnyFreeSpec {
  "Cpu should pass" in {
    simulate(new Cpu) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      // dut.stgIf.io.instrAddr.expect(0.U)
      dut.clock.step()
      // dut.stgIf.io.instrAddr.expect(1.U)
      // dut.clock.step()
      // dut.clock.step()
      // dut.clock.step()
    }
  }
}
