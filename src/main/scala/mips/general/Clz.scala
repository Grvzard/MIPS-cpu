package mips.general

import chisel3._
import chisel3.util.HasBlackBoxResource

class Clz extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })
  addResource("/Clz.v")
}
