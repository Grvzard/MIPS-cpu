package mips.cpu

import chisel3._
import chisel3.experimental.dataview._
import chisel3.experimental.conversions._
import scala.language.implicitConversions
import chisel3.util._

class InstrFields extends Bundle {
  val opcode = UInt(6.W)
  val func = UInt(6.W)
  val shamt = UInt(5.W)
  val rs = UInt(5.W)
  val rt = UInt(5.W)
  val rd = UInt(5.W)
  val imm16 = UInt(16.W)
}

object InstrFields {
  def apply(bits: UInt): InstrFields = {
    val instr = Wire(new InstrFields)
    instr.opcode := bits(31, 26)
    instr.rs := bits(25, 21)
    instr.rt := bits(20, 16)
    instr.rd := bits(15, 11)
    instr.shamt := bits(10, 6)
    instr.func := bits(6, 0)
    instr.imm16 := bits(15, 0)
    instr
  }
}

object Signal {
  def apply(sig: Bool): Bool = {
    val inner = IO(Output(Bool()))
    inner := sig
    inner
  }
}

// class InstrSignals extends Bundle {
//   val regWr = Bool()
//   val gpSig = Bool()
//   val sigJump = Bool()
//   val sigBranch = Bool()
//   val branchOp = UInt(3.W)
//   val mem2reg = Bool()
//   val memWr = Bool()
//   val sigExt = Bool() // 1: sign extend, 0: zero extend (on imm16)
//   val sigFromImm = Bool() // (not the final result)
//   val sigRegDst = Bool() // 1: rd, 0: rt (not the final result)
//   val sigMov = Bool()
//   val aluOp = UInt(5.W)
//   val mduOp = UInt(4.W)
//   val sigMovAcc = Bool()
//   val aaOp = UInt(2.W)
// }

class InstrDecoder extends Module {
  val io = IO(new Bundle {
    val instr = Input(UInt(32.W))
    val fields = Output(new InstrFields)
  })

  private val instr = InstrFields(io.instr)
  io.fields := instr

  val typeSP = Signal(instr.opcode === "b000000".U)
  val typeSP2 = Signal(instr.opcode === "b011100".U)

  val op_ori = Signal(instr.opcode === "b001101".U)
  val op_andi = Signal(instr.opcode === "b001100".U)
  val op_xori = Signal(instr.opcode === "b001110".U)
  val op_addiu = Signal(instr.opcode === "b001001".U)
  val op_addi = Signal(instr.opcode === "b001000".U)
  val op_slti = Signal(instr.opcode === "b001010".U)
  val op_sltiu = Signal(instr.opcode === "b001011".U)
  val op_lui = Signal(instr.opcode === "b001111".U)

  val op_lw = Signal(instr.opcode === "b100011".U)
  val op_lb = Signal(instr.opcode === "b100000".U)
  val op_lh = Signal(instr.opcode === "b100001".U)
  val op_lbu = Signal(instr.opcode === "b100100".U)
  val op_lhu = Signal(instr.opcode === "b100101".U)
  val op_sw = Signal(instr.opcode === "b101011".U)
  val op_sb = Signal(instr.opcode === "b101000".U)
  val op_sh = Signal(instr.opcode === "b101001".U)
  val op_load_ = Signal(op_lw | op_lb | op_lh | op_lbu | op_lhu)
  val op_store_ = Signal(op_sw | op_sb | op_sh)

  val op_j = Signal(instr.opcode === "b000010".U)
  val op_jal = Signal(instr.opcode === "b000011".U)
  val op_jr = Signal(typeSP & instr.func === "b001000".U)
  val op_jalr = Signal(typeSP & instr.func === "b001001".U)
  val op_jump_ = Signal(op_j | op_jal | op_jalr | op_jr)

  val op_beq = Signal(instr.opcode === "b000100".U)
  val op_bne = Signal(instr.opcode === "b000101".U)
  private val op_bx_ = instr.opcode === "b000001".U
  val op_bgez = Signal(op_bx_ & instr.rt === "b00001".U)
  val op_bgezal = Signal(op_bx_ & instr.rt === "b10001".U)
  val op_bltz = Signal(op_bx_ & instr.rt === "b00000".U)
  val op_bltzal = Signal(op_bx_ & instr.rt === "b10000".U)
  val op_bgtz = Signal(instr.opcode === "b000111".U)
  val op_blez = Signal(instr.opcode === "b000110".U)
  val op_branch_ = Signal(
    op_beq | op_bne | op_bgez | op_bgezal | op_bltz | op_bltzal | op_bgtz | op_blez
  )

  val op_add = Signal(typeSP & instr.func === "b100000".U)
  val op_addu = Signal(typeSP & instr.func === "b100001".U)
  val op_sub = Signal(typeSP & instr.func === "b100010".U)
  val op_subu = Signal(typeSP & instr.func === "b100011".U)
  val op_and = Signal(typeSP & instr.func === "b100100".U)
  val op_or = Signal(typeSP & instr.func === "b100101".U)
  val op_xor = Signal(typeSP & instr.func === "b100110".U)
  val op_nor = Signal(typeSP & instr.func === "b100111".U)
  val op_slt = Signal(typeSP & instr.func === "b101010".U)
  val op_sltu = Signal(typeSP & instr.func === "b101011".U)
  val op_sll = Signal(typeSP & instr.func === "b000000".U)
  val op_srl = Signal(typeSP & instr.func === "b000010".U)
  val op_sra = Signal(typeSP & instr.func === "b000011".U)
  val op_sllv = Signal(typeSP & instr.func === "b000100".U)
  val op_srlv = Signal(typeSP & instr.func === "b000110".U)
  val op_srav = Signal(typeSP & instr.func === "b000111".U)

  val op_movn = Signal(typeSP & instr.func === "b001011".U)
  val op_movz = Signal(typeSP & instr.func === "b001010".U)

  val op_mfhi = Signal(typeSP & instr.func === "b010000".U)
  val op_mflo = Signal(typeSP & instr.func === "b010010".U)
  val op_multu = Signal(typeSP & instr.func === "b011001".U)
  val op_mult = Signal(typeSP & instr.func === "b011000".U)
  val op_mtlo = Signal(typeSP & instr.func === "b010011".U)
  val op_mthi = Signal(typeSP & instr.func === "b010001".U)
  val op_divu = Signal(typeSP & instr.func === "b011011".U)
  val op_div = Signal(typeSP & instr.func === "b011010".U)
  val op_mul = Signal(typeSP2 & instr.func === "b010010".U)
  val op_msubu = Signal(typeSP2 & instr.func === "b000101".U)
  val op_msub = Signal(typeSP2 & instr.func === "b000100".U)
  val op_maddu = Signal(typeSP2 & instr.func === "b000001".U)
  val op_madd = Signal(typeSP2 & instr.func === "b000000".U)

  val op_clz = Signal(typeSP2 & instr.func === "b100000".U)
  val op_clo = Signal(typeSP2 & instr.func === "b100001".U)
}
