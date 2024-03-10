package mips.cpu

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import mips.cpu.InstrDecoder

class InstrDecoderTest extends AnyFreeSpec {
  "InstrDecoder should pass" in {
    simulate(new InstrDecoder) { dut =>
      dut.io.instr.poke("h_0000_0000".U)
      dut.clock.step()
      dut.clock.step()
    }
  }
}
