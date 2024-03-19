package mips.general

import chisel3._
import chisel3.util._

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
  val a = io.a
  val b = io.b ^ Fill(32, cin)

  val sum = a +& b +& cin

  io.out := sum(31, 0)
  io.cout := sum(32)
  io.flgS := sum(31)
  io.flgZ := ~io.out.orR
  io.flgO := (~a(31) & ~b(31) & sum(31)) | (a(31) & b(31) & ~sum(31))
}
