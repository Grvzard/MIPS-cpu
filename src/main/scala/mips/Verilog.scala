package mips

import circt.stage.ChiselStage
import mips.general._
import mips.cpu._
import mips.memory._
import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage.FirtoolOption

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
    val args = Array("-td", "verilog")
    val firtoolOpts = Seq("-disable-all-randomization", "-strip-debug-info")
    (new ChiselStage).execute(
      Array("--target", "systemverilog", "--split-verilog") ++ args,
      Seq(ChiselGeneratorAnnotation(() => vModule)) ++ firtoolOpts.map(FirtoolOption(_))
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
