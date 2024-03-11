package mips.cpu.stage

import chisel3._
import chisel3.util._
import mips.cpu.{ExeDataForward, ExeState, IdState, WriteBack}
import mips.cpu.InstrDecoder
import mips.cpu.Regfile

class Id extends Module {
  val io = IO(new Bundle {
    val id = Flipped(Decoupled(new IdState))
    val iramData = Input(UInt(32.W))
    val fw = Input(new ExeDataForward)
    val wb = Input(new WriteBack)
    val exe = Decoupled(new ExeState)
  })

  private val in = io.id.bits
  private val out = io.exe.bits
  val st = Reg(new IdState)
  // out := DontCare
  io.exe.valid := true.B

  // val hasStalled = Reg(Bool())
  // hasStalled := !io.exe.ready
  val hasStalled = RegNext(!io.exe.ready)
  val savedInstr = RegInit(0.U(32.W))

  when(io.exe.ready) {
    st := in
  }.elsewhen(!hasStalled) {
    // the start of stalling, save current instruction
    savedInstr := io.iramData
  }
  io.id.ready := io.exe.ready

  val regfile = Module(new Regfile)
  val dec = Module(new InstrDecoder)

  // In-module Wires
  private val wire = Wire(new Bundle {
    val branchOp = UInt(3.W)
    val regData0, regData1, imm16ex = UInt(32.W)
    val instr = UInt(32.W)
    val sigFromImm, sigExt, sigRegDst, sigRegWr = Bool()
    val aaOp = UInt(2.W)
    val rw = UInt(5.W)
  })
  wire.branchOp :=
    Fill(3, dec.op_beq) & "b000".U |
      Fill(3, dec.op_bgez) & "b001".U |
      Fill(3, dec.op_bgezal) & "b010".U |
      Fill(3, dec.op_bgtz) & "b011".U |
      Fill(3, dec.op_blez) & "b100".U |
      Fill(3, dec.op_bltz) & "b101".U |
      Fill(3, dec.op_bltzal) & "b110".U |
      Fill(3, dec.op_bne) & "b111".U
  wire.regData0 := regfile.io.raddr0
  wire.regData1 := regfile.io.raddr1
  wire.sigFromImm := dec.op_ori | dec.op_andi | dec.op_xori | dec.op_addiu | dec.op_addi |
    dec.op_slti | dec.op_sltiu | dec.op_load_ | dec.op_store_
  wire.sigExt := dec.op_addiu | dec.op_addi | dec.op_slti | dec.op_load_ | dec.op_store_
  wire.sigRegDst := dec.typeSP | dec.op_mul
  wire.imm16ex := Cat(Fill(16, wire.sigExt & dec.io.fields.imm16(15)), dec.io.fields.imm16)
  wire.aaOp :=
    Fill(2, dec.op_lb | dec.op_lbu | dec.op_sb) & "b00".U |
      Fill(2, dec.op_lw | dec.op_sw) & "b01".U |
      // Fill(2, uw) & "b10".U |
      Fill(2, dec.op_lh | dec.op_lhu | dec.op_sh) & "b11".U
  wire.instr := Mux(hasStalled, savedInstr, io.iramData)
  wire.sigRegWr := !(
    // Non-regWr
    dec.op_j | dec.op_beq | dec.op_bgez | dec.op_bltz | dec.op_bgtz | dec.op_blez | dec.op_bne |
      dec.op_store_ | (out.exeSigs.mduOp.orR & !dec.op_mul)
  )
  wire.rw := Mux(
    dec.op_branch_ & wire.sigRegWr | dec.op_jump_ & wire.sigRegWr & !dec.typeSP,
    31.U, // reg $ra
    Mux(wire.sigRegDst, dec.io.fields.rd, dec.io.fields.rt)
  )

  // Sub Modules Wiring
  dec.io.instr := wire.instr
  regfile.io.raddr0 := dec.io.fields.rs
  regfile.io.raddr1 := dec.io.fields.rt
  regfile.io.waddr := io.fw.sigs.rw
  regfile.io.wen := io.fw.sigs.regWr
  regfile.io.wdata := io.fw.data

  // Output Wiring
  out.nextPc := st.nextPc
  out.busA := wire.regData0
  out.busB := Mux(wire.sigFromImm, wire.imm16ex, wire.regData1)

  out.exeSigs.rw := wire.rw
  out.exeSigs.shamt := dec.io.fields.shamt
  out.exeSigs.sigMov := dec.op_movn | dec.op_movz
  out.exeSigs.memWr := dec.op_store_
  out.exeSigs.mem2reg := dec.op_load_
  out.exeSigs.sigBranch := dec.op_branch_ | dec.op_jump_
  out.exeSigs.aluOp :=
    Fill(5, dec.op_addu | dec.op_addiu) & "b00000".U |
      Fill(5, dec.op_add | dec.op_addi) & "b00001".U |
      Fill(5, dec.op_sll) & "b00100".U |
      Fill(5, dec.op_srl) & "b00110".U |
      Fill(5, dec.op_sra) & "b00111".U |
      Fill(5, dec.op_or | dec.op_ori) & "b01000".U |
      Fill(5, dec.op_and | dec.op_andi) & "b01001".U |
      Fill(5, dec.op_xor | dec.op_xori) & "b01010".U |
      Fill(5, dec.op_nor) & "b01011".U |
      Fill(5, dec.op_lui) & "b01100".U |
      Fill(5, dec.op_clz) & "b01111".U |
      Fill(5, dec.op_subu) & "b10000".U |
      Fill(5, dec.op_sub) & "b10001".U |
      Fill(5, dec.op_sltu | dec.op_sltiu) & "b10010".U |
      Fill(5, dec.op_slt | dec.op_slti) & "b10011".U |
      Fill(5, dec.op_sllv) & "b10100".U |
      Fill(5, dec.op_srlv) & "b10110".U |
      Fill(5, dec.op_srav) & "b10111".U |
      Fill(5, dec.op_clo) & "b11111".U
  out.exeSigs.mduOp :=
    Fill(4, dec.op_multu) & "b1000".U |
      Fill(4, dec.op_mult | dec.op_mul) & "b1001".U |
      Fill(4, dec.op_msubu) & "b1010".U |
      Fill(4, dec.op_msub) & "b1011".U |
      Fill(4, dec.op_maddu) & "b1100".U |
      Fill(4, dec.op_madd) & "b1101".U |
      Fill(4, dec.op_divu) & "b1110".U |
      Fill(4, dec.op_div) & "b1111".U |
      Fill(4, dec.op_mtlo) & "b0001".U |
      Fill(4, dec.op_mthi) & "b0010".U
  out.exeSigs.regWr := wire.sigRegWr
  out.exeSigs.sigMovAcc := dec.op_mfhi | dec.op_mflo | dec.op_mul
  out.exeSigs.aaOp := wire.aaOp
  out.generalSigs.gpSig := dec.op_movn | dec.op_mfhi | dec.op_lbu | dec.op_lhu
  out.memSigs.aaOp := wire.aaOp
  out.memSigs.regWr := wire.sigRegWr
  out.memSigs.mem2reg := dec.op_load_
  out.memSigs.rw := wire.rw
}
