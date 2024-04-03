package mips.cpu

import chisel3._
import chisel3.util._
import mips.general.Adder32
import mips.general.Clz

case class AluOp(opcode: UInt)

object AluOp {
  val ADDU = AluOp("b00000".U)
  val ADD = AluOp("b00001".U)
  val SLL = AluOp("b00100".U)
  val SRL = AluOp("b00110".U)
  val SRA = AluOp("b00111".U)
  val OR = AluOp("b01000".U)
  val AND = AluOp("b01001".U)
  val XOR = AluOp("b01010".U)
  val NOR = AluOp("b01011".U)
  val LUI = AluOp("b01100".U)
  val CLZ = AluOp("b01111".U)
  val SUBU = AluOp("b10000".U)
  val SUB = AluOp("b10001".U)
  val SLTU = AluOp("b10010".U)
  val SLT = AluOp("b10011".U)
  val SLLV = AluOp("b10100".U)
  val SRLV = AluOp("b10110".U)
  val SRAV = AluOp("b10111".U)
  val CLO = AluOp("b11111".U)

  def getMux(opcode: UInt): UInt = opcode(3, 0)
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
    (Fill(32, op === AluOp.ADDU.opcode) & res_adder) |
      (Fill(32, op === AluOp.ADD.opcode) & res_adder) |
      (Fill(32, op === AluOp.SLL.opcode) & res_sll) |
      (Fill(32, op === AluOp.SRL.opcode) & res_srl) |
      (Fill(32, op === AluOp.SRA.opcode) & res_sra) |
      (Fill(32, op === AluOp.OR.opcode) & res_or) |
      (Fill(32, op === AluOp.AND.opcode) & res_and) |
      (Fill(32, op === AluOp.XOR.opcode) & res_xor) |
      (Fill(32, op === AluOp.NOR.opcode) & res_nor) |
      (Fill(32, op === AluOp.LUI.opcode) & res_lui) |
      (Fill(32, op === AluOp.CLZ.opcode) & res_clz) |
      (Fill(32, op === AluOp.SUBU.opcode) & res_adder) |
      (Fill(32, op === AluOp.SUB.opcode) & res_adder) |
      (Fill(32, op === AluOp.SLTU.opcode) & res_sltu) |
      (Fill(32, op === AluOp.SLT.opcode) & res_slt) |
      (Fill(32, op === AluOp.SLLV.opcode) & res_sll) |
      (Fill(32, op === AluOp.SRLV.opcode) & res_srl) |
      (Fill(32, op === AluOp.SRAV.opcode) & res_sra) |
      (Fill(32, op === AluOp.CLO.opcode) & res_clz)
}
