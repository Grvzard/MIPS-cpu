package mips

import circt.stage.ChiselStage
import mips.general._
import mips.cpu._
import mips.memory._

trait VerilogDump extends App {
  def vModule: chisel3.RawModule
  def vDump() = {
    println(
      ChiselStage.emitSystemVerilog(
        vModule,
        firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
      )
    )
  }
  def vDumpFile() = {
    ChiselStage.emitSystemVerilogFile(
      vModule,
      Array("-td", "verilog"),
      firtoolOpts = Array("-disable-all-randomization")
    )
  }
}

object VerilogAdder32 extends VerilogDump {
  def vModule = new Adder32
  vDump()
}

object VerilogAlu extends VerilogDump {
  def vModule = new Alu
  vDump()
}

object VerilogMult32 extends VerilogDump {
  def vModule = new Mult32
  vDump()
}

object VerilogCsa extends VerilogDump {
  def vModule = new Csa(32)
  vDump()
}

object VerilogRegfile extends VerilogDump {
  def vModule = new Regfile
  vDump()
}

object VerilogRom extends VerilogDump {
  def vModule = new Rom(8)
  vDump()
}

object FileVerilogSoc extends VerilogDump {
  def vModule = new Soc
  vDumpFile()
}
