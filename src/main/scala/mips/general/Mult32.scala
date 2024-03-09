package mips.general

import chisel3._
import chisel3.util._

class Mult32 extends Module {
  val io = IO(new Bundle {
    val x = Input(UInt(32.W))
    val y = Input(UInt(32.W))
    val sigSigned = Input(Bool())
    val result = Output(UInt(64.W))
  })

  val bitwidth = 32

  val x = Cat(Fill(64, io.sigSigned & io.x(31)), io.x)
  val neg_x = ~x + 1.U
  val double_x = x << 1

  val wallace = Module(new WallaceReduction)

  val part0 = wallace.io.vecIn
  val vecT = Wire(Vec(17, UInt(1.W)))
  vecT(0) := 0.U

  for (i <- 0 until 16) {
    val y_i1 = io.y(2 * i + 1)
    val y_i = io.y(2 * i)
    val tmp = Cat(0.U, y_i1, y_i) + vecT(i)
    vecT(i + 1) := y_i1 & (y_i | vecT(i))

    part0(i) := Fill(64, tmp(1, 0) === "b01".U) & (x << 2 * i) |
      Fill(64, tmp(1, 0) === "b10".U) & (double_x << 2 * i) |
      Fill(64, tmp(1, 0) === "b11".U) & (neg_x << 2 * i)
  }
  part0(16) := Mux(io.y(31, 30) === "b10".U, neg_x << bitwidth, 0.U)

  io.result := wallace.io.out(0) + wallace.io.out(1)

  // printf(cf"\n$part0\n")
  // printf(cf"result: ${io.result}\n")
}
