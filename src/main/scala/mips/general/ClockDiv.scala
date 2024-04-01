package mips.general

import chisel3._
import chisel3.util._

// !this Module shouldn't affected by the implicit reset signal!
class ClockDiv(n: Int) extends RawModule {
  val io = IO(new Bundle {
    val clockIn = Input(Clock())
    val clockOut = Output(Clock())
  })

  assert(n % 2 == 0)

  if (n == 0) {
    io.clockOut := io.clockIn
  } else {
    val (_, warp) = withClockAndReset(io.clockIn, false.B) { Counter(true.B, n / 2) }
    val innerClk = withClockAndReset(io.clockIn, false.B) { RegInit(false.B) }

    when(warp) {
      innerClk := ~innerClk
    }
    io.clockOut := innerClk.asClock
  }
}
