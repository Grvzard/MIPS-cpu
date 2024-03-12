package mips.cpu

import chisel3._

class GeneralSignals extends Bundle {

  val gpSig = Output(Bool())
}

class ExeSignals extends Bundle {
  val regWr = Bool()
  val sigBranch = Bool()
  val mem2reg = Bool() // for forwarding
  val memWr = Bool() // for forwarding
  // val sigExt = Bool() // 1: sign extend, 0: zero extend (on imm16)
  // val sigFromImm = Bool() // (not the final result)
  // val sigRegDst = Bool() // 1: rd, 0: rt (not the final result)
  val aluOp = UInt(5.W)
  val mduOp = UInt(4.W)
  val sigMov = Bool()
  val sigMovAcc = Bool()
  val aaOp = UInt(2.W)
  val rw = UInt(5.W)
  val shamt = UInt(5.W)
}

class MemSignals extends Bundle {
  val aaOp = UInt(2.W)
  val regWr = Bool()
  val mem2reg = Bool()
  val rw = UInt(5.W)
}

class WbSignals extends Bundle {
  val mem2reg = Bool()
  val regWr = Bool()
  val rw = UInt(5.W)
}

class FwSignals extends Bundle {
  val regWr = Bool()
  val rw = UInt(5.W)
}

class IdState extends Bundle {
  val nextPc = UInt(32.W)
}

class ExeState extends Bundle {
  val nextPc = UInt(32.W)
  val busA = UInt(32.W)
  val busB = UInt(32.W)

  val generalSigs = new GeneralSignals
  val exeSigs = new ExeSignals
  val memSigs = new MemSignals
}

class ExeDataForward extends Bundle {
  val data = UInt(32.W)
  val sigs = new Bundle {
    val mem2reg = Bool()
    val regWr = Bool()
    val rw = UInt(5.W)
  }
}

class MemState extends Bundle {
  val data = UInt(32.W)

  val generalSigs = new GeneralSignals
  val memSigs = new MemSignals
}

class IdBranchForward extends Bundle {
  val jumpTarget = UInt(32.W)
  val sigJump = Bool()
}

class WriteBack extends Bundle {
  val data = UInt(32.W)
  val sigs = new Bundle {
    val regWr = Bool()
    val rw = UInt(5.W)
  }
}
