package mips.cpu

import chisel3._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.simulator.EphemeralSimulator._
import mips.cpu.Cpu

class CpuTest extends AnyFreeSpec {
  "Cpu should pass" in {
    simulate(new Cpu) { dut =>
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
    }
  }
}
