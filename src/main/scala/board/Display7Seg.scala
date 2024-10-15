package board

import chisel3._
import chisel3.util._

class Display7SegIn extends Bundle {
  val num = Input(UInt(32.W)) // encoded in BCD
  val wrEn = Input(Bool())
}

class Display7SegOut extends Bundle {
  val segEn = Output(UInt(8.W))
  val segCtl = Output(UInt(8.W)) // 7..1 -> a..g, 0 -> point
}

object Display7Seg {
  def num2ctl(num: UInt) = {
    Cat(
      num === 1.U || num === 4.U || num === 11.U || num === 12.U || num === 13.U,
      num === 5.U || num === 6.U || num === 11.U || num === 12.U || num === 14.U || num === 15.U,
      num === 2.U || num === 12.U || num === 14.U || num === 15.U,
      num === 1.U || num === 4.U || num === 7.U || num === 9.U || num === 10.U || num === 15.U,
      num === 1.U || num === 3.U || num === 4.U || num === 5.U || num === 7.U,
      num === 1.U || num === 2.U || num === 3.U || num === 7.U || num === 12.U || num === 13.U,
      num === 0.U || num === 1.U || num === 7.U,
      1.B
    )
  }
}

class Display7Seg extends Module {
  import Display7Seg._

  val io = IO(new Bundle {
    val in = new Display7SegIn
    val out = new Display7SegOut
  })

  val num = RegInit(0.U(32.W))
  val en = RegInit("b_1111_1110".U)
  when(io.in.wrEn) {
    num := io.in.num
  }
  en := Cat(en(6, 0), en(7))

  io.out.segCtl := DontCare
  when(en(0) === 0.B) {
    io.out.segCtl := num2ctl(num(3, 0))
  }.elsewhen(en(1) === 0.B) {
    io.out.segCtl := num2ctl(num(7, 4))
  }.elsewhen(en(2) === 0.B) {
    io.out.segCtl := num2ctl(num(11, 8))
  }.elsewhen(en(3) === 0.B) {
    io.out.segCtl := num2ctl(num(15, 12))
  }.elsewhen(en(4) === 0.B) {
    io.out.segCtl := num2ctl(num(19, 16))
  }.elsewhen(en(5) === 0.B) {
    io.out.segCtl := num2ctl(num(23, 20))
  }.elsewhen(en(6) === 0.B) {
    io.out.segCtl := num2ctl(num(27, 24))
  }.elsewhen(en(7) === 0.B) {
    io.out.segCtl := num2ctl(num(31, 28))
  }
  io.out.segEn := en

  // printf(cf"-: ${io.segEn}%b\n")
  // printf(cf" : ${io.segCtl}%x\n")
}
