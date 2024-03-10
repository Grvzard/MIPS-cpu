package mips

import circt.stage.ChiselStage
import mips.general._
import mips.cpu._

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
}

object VerilogAdder32 extends VerilogDump {
  def vModule = new Adder32
  vDump()
}

object VerilogAlu extends VerilogDump {
  def vModule = new Alu
  vDump()
}
