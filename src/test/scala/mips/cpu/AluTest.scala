package mips.cpu

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import mips.cpu.Alu

class AluTest extends AnyFreeSpec {
  "ALU should pass" in {
    simulate(new Alu) { dut =>
      dut.io.a.poke("h_0000_ffff".U)
      dut.io.b.poke(0.U)
      dut.io.opcode.poke("b01111".U)
      dut.io.result.expect(16.U)
    }
  }
}
