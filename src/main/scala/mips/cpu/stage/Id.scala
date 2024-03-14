package mips.cpu.stage

import chisel3._
import chisel3.util._
import mips.cpu.{ExeDataForward, ExeState, IdBranchForward, IdState, WriteBack}
import mips.cpu.InstrDecoder
import mips.cpu.Regfile

class Id extends Module {
  val io = IO(new Bundle {
    val id = Flipped(Decoupled(new IdState))
    val iramData = Input(UInt(32.W))
    val fwExeData = Input(new ExeDataForward)
    val wb = Input(new WriteBack)
    val exe = Decoupled(new ExeState)
    val fwIfBranch = Output(new IdBranchForward)
  })
  val debug = IO(Input(Bool()))

  private val in = io.id.bits
  private val out = io.exe.bits
  val st = Reg(new IdState)

  val hasStalled = RegNext(!io.exe.ready)
  val savedInstr = RegInit(0.U(32.W))

  private val wire = Wire(new Bundle {
    val regData0, regData1, imm16ex, pcAdd8 = UInt(32.W)
    val instr = UInt(32.W)
    val sigFromImm, sigExt, sigRegDst, sigRegWr, branchMet, movInvalid = Bool()
    val sigStall, hazardLoadUse = Bool()
    val aaOp = UInt(2.W)
    val rw = UInt(5.W)
    val jTarget = UInt(32.W)
  })

  when(!wire.sigStall) {
    st := in
  }.elsewhen(!hasStalled) {
    // the start of stalling, save current instruction
    savedInstr := io.iramData
  }
  io.id.ready := !wire.sigStall

  val regfile = Module(new Regfile)
  val dec = Module(new InstrDecoder)

  // In-module Wires
  wire.regData0 := Mux(
    dec.io.fields.rs === 0.U,
    0.U,
    Mux(
      dec.io.fields.rs === io.fwExeData.sigs.rw & io.fwExeData.sigs.regWr,
      io.fwExeData.data,
      Mux(dec.io.fields.rs === io.wb.sigs.rw & io.wb.sigs.regWr, io.wb.data, regfile.io.raddr0)
    )
  )
  wire.regData1 := Mux(
    dec.io.fields.rt === 0.U,
    0.U,
    Mux(
      dec.io.fields.rt === io.fwExeData.sigs.rw & io.fwExeData.sigs.regWr,
      io.fwExeData.data,
      Mux(dec.io.fields.rt === io.wb.sigs.rw & io.wb.sigs.regWr, io.wb.data, regfile.io.raddr0)
    )
  )
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
  wire.jTarget := Cat(st.nextPc(31, 28), wire.instr(25, 0), 0.U(2.W))
  wire.pcAdd8 := st.nextPc + 4.U
  wire.branchMet :=
    dec.op_beq & out.busA === out.busA |
      (dec.op_bgez | dec.op_bgezal) & out.busA >= 0.U |
      dec.op_bgtz & out.busA > 0.U |
      dec.op_blez & out.busA <= 0.U |
      (dec.op_bltz | dec.op_bltzal) & out.busA < 0.U |
      dec.op_bne & out.busA =/= out.busB
  wire.movInvalid := dec.op_movn & wire.regData1 === 0.U | dec.op_movz & wire.regData1 =/= 0.U
  wire.hazardLoadUse :=
    (dec.io.fields.rs === io.fwExeData.sigs.rw | dec.io.fields.rt === io.fwExeData.sigs.rw) &
      io.fwExeData.sigs.regWr & io.fwExeData.sigs.mem2reg
  wire.sigStall := !io.exe.ready | wire.hazardLoadUse

  // Sub Modules Wiring
  dec.io.instr := wire.instr
  regfile.io.raddr0 := dec.io.fields.rs
  regfile.io.raddr1 := dec.io.fields.rt
  regfile.io.waddr := io.fwExeData.sigs.rw
  regfile.io.wen := io.fwExeData.sigs.regWr
  regfile.io.wdata := io.fwExeData.data

  // Output Wiring
  io.exe.valid := !(wire.movInvalid | wire.hazardLoadUse)

  out.nextPc := st.nextPc
  out.busA := wire.regData0
  out.busB := Mux(wire.sigFromImm, wire.imm16ex, wire.regData1)

  io.fwIfBranch.sigJump := wire.branchMet | dec.op_jump_
  io.fwIfBranch.jumpTarget :=
    Fill(32, dec.op_branch_) & wire.pcAdd8 |
      Fill(32, dec.op_j | dec.op_jal) & wire.jTarget |
      Fill(32, dec.op_jr | dec.op_jalr) & out.busA

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

  when(debug) {
    printf(cf"id- r: ${io.id.ready}, instr: ${io.iramData}%x, nextpc: ${st.nextPc >> 2}\n")
  }
}
