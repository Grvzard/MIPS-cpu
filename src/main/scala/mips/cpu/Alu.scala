package mips.cpu

import chisel3._
import chisel3.util._
import mips.general.Adder32
import mips.general.Clz

case class AluOp(opcode: UInt, opmux: UInt)

object AluOp {
  val ADDU = AluOp("b00000".U, "b0000".U)
  val ADD = AluOp("b00001".U, "b0000".U)
  val SLL = AluOp("b00100".U, "b0011".U)
  val SRL = AluOp("b00110".U, "b0100".U)
  val SRA = AluOp("b00111".U, "b0101".U)
  val OR = AluOp("b01000".U, "b0001".U)
  val AND = AluOp("b01001".U, "b0010".U)
  val XOR = AluOp("b01010".U, "b0110".U)
  val NOR = AluOp("b01011".U, "b1011".U)
  val LUI = AluOp("b01100".U, "b0111".U)
  val CLZ = AluOp("b01111".U, "b1000".U)
  val SUBU = AluOp("b10000".U, "b0000".U)
  val SUB = AluOp("b10001".U, "b0000".U)
  val SLTU = AluOp("b10010".U, "b1010".U)
  val SLT = AluOp("b10011".U, "b1001".U)
  val SLLV = AluOp("b10100".U, "b0011".U)
  val SRLV = AluOp("b10110".U, "b0100".U)
  val SRAV = AluOp("b10111".U, "b0101".U)
  val CLO = AluOp("b11111".U, "b1000".U)

  def getMux(opcode: UInt): UInt =
    (Fill(5, opcode === ADDU.opcode) & ADDU.opmux) |
      (Fill(5, opcode === ADD.opcode) & ADD.opmux) |
      (Fill(5, opcode === SLL.opcode) & SLL.opmux) |
      (Fill(5, opcode === SRL.opcode) & SRL.opmux) |
      (Fill(5, opcode === SRA.opcode) & SRA.opmux) |
      (Fill(5, opcode === OR.opcode) & OR.opmux) |
      (Fill(5, opcode === AND.opcode) & AND.opmux) |
      (Fill(5, opcode === XOR.opcode) & XOR.opmux) |
      (Fill(5, opcode === NOR.opcode) & NOR.opmux) |
      (Fill(5, opcode === LUI.opcode) & LUI.opmux) |
      (Fill(5, opcode === CLZ.opcode) & CLZ.opmux) |
      (Fill(5, opcode === SUBU.opcode) & SUBU.opmux) |
      (Fill(5, opcode === SUB.opcode) & SUB.opmux) |
      (Fill(5, opcode === SLTU.opcode) & SLTU.opmux) |
      (Fill(5, opcode === SLT.opcode) & SLT.opmux) |
      (Fill(5, opcode === SLLV.opcode) & SLLV.opmux) |
      (Fill(5, opcode === SRLV.opcode) & SRLV.opmux) |
      (Fill(5, opcode === SRAV.opcode) & SRAV.opmux) |
      (Fill(5, opcode === CLO.opcode) & CLO.opmux)
}

class Alu extends Module {
  val io = IO(new Bundle {
    val a, b = Input(UInt(32.W))
    val opcode = Input(UInt(5.W))
    val shamt = Input(UInt(5.W))
    val result = Output(UInt(32.W))
    // flagZero, flagOverflow
    val flgZ, flgO = Output(Bool())
  })

  val op = io.opcode
  val sel = io.opcode(4)
  val sigOV = (op == AluOp.ADD || op == AluOp.SUB).B // control Overflow if op: add / sub
  val shamt = Mux(sel, io.a(4, 0), io.shamt)

  val adder = Module(new Adder32)
  adder.io.a := io.a
  adder.io.b := io.b
  adder.io.sigSub := sel
  val clz = Module(new Clz)
  // op clo has sel set to true, which then negates the input A
  clz.io.in := io.a ^ Fill(32, sel)

  val res_adder = adder.io.out
  val res_slt = Cat(0.U(31.W), adder.io.flgO ^ adder.io.flgS)
  val res_sltu = Cat(0.U(31.W), ~adder.io.cout)

  val res_or = io.a | io.b
  val res_xor = io.a ^ io.b
  val res_and = io.a & io.b
  val res_nor = ~(io.a | io.b)

  val res_sll = io.b << shamt
  val res_srl = io.b >> shamt
  val res_sra = (io.b.asSInt >> shamt).asUInt

  val res_lui = io.b << 16
  val res_clz = clz.io.out

  val opmux = AluOp.getMux(op)

  io.flgZ := adder.io.flgZ
  io.flgO := adder.io.flgO & sigOV
  io.result :=
    (Fill(32, "b0000".U === opmux) & res_adder) |
      (Fill(32, "b0001".U === opmux) & res_or) |
      (Fill(32, "b0010".U === opmux) & res_and) |
      (Fill(32, "b0011".U === opmux) & res_sll) |
      (Fill(32, "b0100".U === opmux) & res_srl) |
      (Fill(32, "b0101".U === opmux) & res_sra) |
      (Fill(32, "b0110".U === opmux) & res_xor) |
      (Fill(32, "b0111".U === opmux) & res_nor) |
      (Fill(32, "b1000".U === opmux) & res_clz) |
      (Fill(32, "b1001".U === opmux) & res_slt) |
      (Fill(32, "b1010".U === opmux) & res_sltu) |
      (Fill(32, "b1011".U === opmux) & res_lui)
}
