package mips.cpu

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import mips.cpu.Regfile

class RegfileTest extends AnyFreeSpec {
  "Regfile should pass" in {
    simulate(new Regfile) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      dut.io.raddr0.poke(0.U)
      dut.io.waddr.poke(0.U)
      dut.io.wdata.poke("h_eeee".U)
      dut.io.wen.poke(true.B)
      dut.clock.step()
      dut.io.rdata0.expect("h_0".U)

      for (i <- 1 to 31) {
        dut.io.raddr0.poke(i.U)
        dut.io.raddr1.poke(i.U)
        dut.io.rdata0.expect("h_0".U)
        dut.io.rdata1.expect("h_0".U)

        dut.io.raddr0.poke(i.U)
        dut.io.raddr1.poke(i.U)
        dut.io.waddr.poke(i.U)
        dut.io.wdata.poke("h_ff00_ff00".U)
        dut.io.wen.poke(true.B)
        dut.clock.step()
        dut.io.rdata0.expect("h_ff00_ff00".U)
        dut.io.rdata1.expect("h_ff00_ff00".U)
      }
    }
  }
}
