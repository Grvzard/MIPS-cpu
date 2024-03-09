package mips.general

import chisel3._
import mips.general.Csa
import chisel3.experimental.conversions._
import scala.language.implicitConversions

object WallaceReduction {
  val width = 64
  private def compress3to2(a: UInt, b: UInt, c: UInt): (UInt, UInt) = {
    val csa = Module(new Csa(64))
    csa.io.a := a
    csa.io.b := b
    csa.io.c := c
    (csa.io.f0, csa.io.f1)
  }
}

class WallaceReduction extends Module {
  import WallaceReduction._

  val io = IO(new Bundle {
    val vecIn = Input(Vec(17, UInt(64.W)))
    val out = Output(Vec(2, UInt(64.W)))
  })

  val lyr0 = io.vecIn
  val lyr1 = Wire(Vec(12, UInt(64.W)))
  // val lyr2 = Vec(8, UInt(64.W))
  // val lyr3 = Vec(6, UInt(64.W))
  // val lyr4 = Vec(4, UInt(64.W))
  // val lyr5 = Vec(3, UInt(64.W))
  // val lyr6 = Vec(2, UInt(64.W))

  // TODO

  (lyr1(0), lyr1(1)) := compress3to2(lyr0(0), lyr0(1), lyr0(2))
  (lyr1(2), lyr1(3)) := compress3to2(lyr0(3), lyr0(4), lyr0(5))
  (lyr1(4), lyr1(5)) := compress3to2(lyr0(6), lyr0(7), lyr0(8))
  (lyr1(6), lyr1(7)) := compress3to2(lyr0(9), lyr0(10), lyr0(11))
  (lyr1(8), lyr1(9)) := compress3to2(lyr0(12), lyr0(13), lyr0(14))
  lyr1(10) := lyr0(15)
  lyr1(11) := lyr0(16)

  io.out(0) :=
    lyr1(0) +
      lyr1(1) +
      lyr1(2) +
      lyr1(3) +
      lyr1(4) +
      lyr1(5) +
      lyr1(6) +
      lyr1(7) +
      lyr1(8) +
      lyr1(9) +
      lyr1(10)
  io.out(1) :=
    lyr1(11)
}
