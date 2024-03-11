package mips.general

import chisel3._

// Carry Save Adder
class Csa(width: Int) extends Module {
  val io = IO(new Bundle {
    val a, b, c = Input(UInt(width.W))
    val f0 = Output(UInt(width.W)) // Sum
    val f1 = Output(UInt(width.W)) // Cout
  })

  val f0 = io.f0.asTypeOf(Vec(width, Bool()))
  val f1 = io.f1.asTypeOf(Vec(width, Bool()))

  for (i <- 0 until width) {
    f0(i) := io.a(i) ^ io.b(i) ^ io.c(i)
    f1(i) := (io.a(i) & io.b(i)) | (io.c(i) & (io.a(i) ^ io.b(i)))
  }
  io.f0 := f0.asUInt
  io.f1 := f1.asUInt << 1

  // printf(cf"${io.f0}")
  // printf(cf"${io.f1}")
}
