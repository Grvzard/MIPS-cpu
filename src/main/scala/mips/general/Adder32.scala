package mips.general

import chisel3._

class Adder32 extends Module {
  val io = IO(new Bundle {
    val a, b = Input(UInt(32.W))
    val sigSub = Input(Bool())
    val out = Output(UInt(32.W))
    val cout = Output(UInt(1.W))
    // flags: Sign, Zero, Overflow
    val flgS, flgZ, flgO = Output(Bool())
  })

  val cin = io.sigSub.asUInt
  val sum = io.a +& io.b +& cin

  io.out := sum(31, 0)
  io.cout := sum(32)
  io.flgS := io.out(31)
  io.flgZ := ~io.out.orR
  io.flgO := (~io.a(31) & ~io.b(31) & io.out(31)) | (io.a(31) & io.b(31) & ~io.out(31))
}
