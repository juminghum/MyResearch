package ClosePathLogic

import chisel3._
import chisel3.util._
import FourTwoCompressor._
import LZA4._


class ClosePathLogic_v2 extends Module {
  val io = IO(new Bundle {
    val IN_AB_R = Input(UInt(48.W))
    val IN_AB_S = Input(UInt(43.W))
    val IN_CD_R = Input(UInt(48.W))
    val IN_CD_S = Input(UInt(43.W))
    val IN_OP_SEL = Input(UInt(1.W))
    val IN_EXP_DIFF = Input(SInt(10.W))
    val OUT_R = Output(UInt(51.W))
    val OUT_S = Output(UInt(51.W))
    val SIGNIF_COMP = Output(UInt(1.W))
    val NORM_SHIFT = Output(UInt(6.W))
  })

  val zero_vec = 0.U(32.W)
  val Aligned_AB_R = Wire(UInt(51.W))
  val Aligned_AB_S = Wire(UInt(51.W))
  val Aligned_CD_R = Wire(UInt(51.W))
  val Aligned_CD_S = Wire(UInt(51.W))

  val Align = Module(new MantissaAlignment_v2)
  Align.io.INAB_R := io.IN_AB_R
  Align.io.INAB_S := io.IN_AB_S
  Align.io.INCD_R := io.IN_CD_R
  Align.io.INCD_S := io.IN_CD_S
  Align.io.EXP_DIFF := io.IN_EXP_DIFF
  Aligned_AB_R := Align.io.OUT_AB_R
  Aligned_AB_S := Align.io.OUT_AB_S
  Aligned_CD_R := Align.io.OUT_CD_R
  Aligned_CD_S := Align.io.OUT_CD_S

  val AB_R_inv = Wire(UInt(51.W))
  val AB_S_inv = Wire(UInt(51.W))
  val CD_R_inv = Wire(UInt(51.W))
  val CD_S_inv = Wire(UInt(51.W))

  AB_R_inv := Mux(io.IN_OP_SEL === 1.U, (~Aligned_AB_R).asUInt() + 1.U, Aligned_AB_R)
  AB_S_inv := Mux(io.IN_OP_SEL === 1.U, (~Aligned_AB_S).asUInt() + 1.U, Aligned_AB_S)
  CD_R_inv := Mux(io.IN_OP_SEL === 1.U, (~Aligned_CD_R).asUInt() + 1.U, Aligned_CD_R)
  CD_S_inv := Mux(io.IN_OP_SEL === 1.U, (~Aligned_CD_S).asUInt() + 1.U, Aligned_CD_S)

  val CSA_A_R = Wire(UInt(51.W))
  val CSA_A_S = Wire(UInt(51.W))
  val CSA_B_R = Wire(UInt(51.W))
  val CSA_B_S = Wire(UInt(51.W))

  val FTC51_A = Module(new FourTwoCompressor51)
  FTC51_A.io.A := AB_R_inv
  FTC51_A.io.B := AB_S_inv
  FTC51_A.io.C := Aligned_CD_R
  FTC51_A.io.D := Aligned_CD_S
  CSA_A_R := FTC51_A.io.R
  CSA_A_S := FTC51_A.io.S

  val FTC51_B = Module(new FourTwoCompressor51)
  FTC51_B.io.A := Aligned_AB_R
  FTC51_B.io.B := Aligned_AB_S
  FTC51_B.io.C := CD_R_inv
  FTC51_B.io.D := CD_S_inv
  CSA_B_R := FTC51_B.io.R
  CSA_B_S := FTC51_B.io.S

  val sig_comp = Wire(UInt(1.W))
  sig_comp := CSA_A_R(50) & CSA_A_S(50)
  io.SIGNIF_COMP := sig_comp

  val Norm_R = Wire(UInt(51.W))
  val Norm_S = Wire(UInt(51.W))

  Norm_R := Mux(sig_comp === 1.U, CSA_B_R, CSA_A_R)
  Norm_S := Mux(sig_comp === 1.U, CSA_B_S, CSA_A_S)


  val Norm_shift = Wire(UInt(6.W))
  val Correction = Wire(UInt(1.W))

  val LZA = Module(new LZA4)
  LZA.io.INA := Aligned_AB_R(50, 3)
  LZA.io.INB := Aligned_AB_S(50, 3)
  LZA.io.INC := Aligned_CD_R(50, 3)
  LZA.io.IND := Aligned_CD_S(50, 3)
  Norm_shift := LZA.io.SHIFT_VALUE
  Correction := LZA.io.CorrectionFlag

  // Coarse Shifter //
  val Norm_R_1stage = Wire(UInt(51.W))
  val Norm_S_1stage = Wire(UInt(51.W))
  val Norm_R_2stage = Wire(UInt(51.W))
  val Norm_S_2stage = Wire(UInt(51.W))
  val Norm_R_3stage = Wire(UInt(51.W))
  val Norm_S_3stage = Wire(UInt(51.W))
  val Norm_R_4stage = Wire(UInt(51.W))
  val Norm_S_4stage = Wire(UInt(51.W))
  val Norm_R_5stage = Wire(UInt(51.W))
  val Norm_S_5stage = Wire(UInt(51.W))
  val Norm_R_6stage = Wire(UInt(51.W))
  val Norm_S_6stage = Wire(UInt(51.W))
  val Norm_R_7stage = Wire(UInt(51.W))
  val Norm_S_7stage = Wire(UInt(51.W))
  /*
    Norm_R_1stage := Mux(Norm_shift(5) === 1.U, zero_vec ## Norm_R(50, 32), Norm_R)
    Norm_S_1stage := Mux(Norm_shift(5) === 1.U, zero_vec ## Norm_S(50, 32), Norm_S)

    Norm_R_2stage := Mux(Norm_shift(4) === 1.U, zero_vec(31,16) ## Norm_R_1stage(50, 16), Norm_R_1stage)
    Norm_S_2stage := Mux(Norm_shift(4) === 1.U, zero_vec(31,16) ## Norm_S_1stage(50, 16), Norm_S_1stage)

    Norm_R_3stage := Mux(Norm_shift(3) === 1.U, zero_vec(15,8) ## Norm_R_2stage(50, 8), Norm_R_2stage)
    Norm_S_3stage := Mux(Norm_shift(3) === 1.U, zero_vec(15,8) ## Norm_S_2stage(50, 8), Norm_S_2stage)

    Norm_R_4stage := Mux(Norm_shift(2) === 1.U, zero_vec(7,4) ## Norm_R_3stage(50, 4), Norm_R_3stage)
    Norm_S_4stage := Mux(Norm_shift(2) === 1.U, zero_vec(7,4) ## Norm_S_3stage(50, 4), Norm_S_3stage)

    Norm_R_5stage := Mux(Norm_shift(1) === 1.U, zero_vec(3,2) ## Norm_R_4stage(50, 2), Norm_R_4stage)
    Norm_S_5stage := Mux(Norm_shift(1) === 1.U, zero_vec(3,2) ## Norm_S_4stage(50, 2), Norm_S_4stage)

    Norm_R_6stage := Mux(Norm_shift(0) === 1.U, zero_vec(1) ## Norm_R_5stage(50, 1), Norm_R_5stage)
    Norm_S_6stage := Mux(Norm_shift(0) === 1.U, zero_vec(1) ## Norm_S_5stage(50, 1), Norm_S_5stage)

    Norm_R_7stage := Mux(Correction === 1.U, zero_vec(0) ## Norm_R_5stage(50, 1), Norm_R_6stage)
    Norm_S_7stage := Mux(Correction === 1.U, zero_vec(0) ## Norm_S_5stage(50, 1), Norm_S_6stage)
  */
  Norm_R_1stage := Mux(Norm_shift(5) === 1.U, Norm_R(50, 32) ## zero_vec, Norm_R)
  Norm_S_1stage := Mux(Norm_shift(5) === 1.U, Norm_S(50, 32) ## zero_vec, Norm_S)

  Norm_R_2stage := Mux(Norm_shift(4) === 1.U, Norm_R_1stage(50, 16) ## zero_vec(31,16), Norm_R_1stage)
  Norm_S_2stage := Mux(Norm_shift(4) === 1.U, Norm_S_1stage(50, 16) ## zero_vec(31,16), Norm_S_1stage)

  Norm_R_3stage := Mux(Norm_shift(3) === 1.U, Norm_R_2stage(50, 8) ## zero_vec(15,8) , Norm_R_2stage)
  Norm_S_3stage := Mux(Norm_shift(3) === 1.U, Norm_S_2stage(50, 8) ## zero_vec(15,8), Norm_S_2stage)

  Norm_R_4stage := Mux(Norm_shift(2) === 1.U, Norm_R_3stage(50, 4) ## zero_vec(7,4), Norm_R_3stage)
  Norm_S_4stage := Mux(Norm_shift(2) === 1.U, Norm_S_3stage(50, 4) ## zero_vec(7,4), Norm_S_3stage)

  Norm_R_5stage := Mux(Norm_shift(1) === 1.U, Norm_R_4stage(50, 2) ## zero_vec(3,2), Norm_R_4stage)
  Norm_S_5stage := Mux(Norm_shift(1) === 1.U, Norm_S_4stage(50, 2) ## zero_vec(3,2), Norm_S_4stage)

  Norm_R_6stage := Mux(Norm_shift(0) === 1.U, Norm_R_5stage(50, 1) ## zero_vec(1), Norm_R_5stage)
  Norm_S_6stage := Mux(Norm_shift(0) === 1.U, Norm_S_5stage(50, 1) ## zero_vec(1), Norm_S_5stage)

  Norm_R_7stage := Mux(Correction === 1.U, Norm_R_5stage(50, 1) ## zero_vec(0), Norm_R_6stage)
  Norm_S_7stage := Mux(Correction === 1.U, Norm_S_5stage(50, 1) ## zero_vec(0), Norm_S_6stage)

  io.OUT_R := Norm_R_7stage
  io.OUT_S := Norm_S_7stage
  io.NORM_SHIFT := Norm_shift
}


class MantissaAlignment_v2 extends Module {
  val io = IO(new Bundle {
    val INAB_R = Input(UInt(48.W))
    val INAB_S = Input(UInt(43.W))
    val INCD_R = Input(UInt(48.W))
    val INCD_S = Input(UInt(43.W))
    val EXP_DIFF = Input(SInt(10.W))
    val OUT_AB_R = Output(UInt(51.W))
    val OUT_AB_S = Output(UInt(51.W))
    val OUT_CD_R = Output(UInt(51.W))
    val OUT_CD_S = Output(UInt(51.W))
  })

  val zero_vec = 0.U(2.W)

  val AB_S_buf = Wire(UInt(51.W))
  val AB_R_buf = Wire(UInt(51.W))

  //printf("OUTAB_S = %b\n", io.OUT_S)
  //printf("OUTAB_C = %b\n", io.OUT_R)
  AB_S_buf := 0.U
  AB_R_buf := 0.U

  val CD_S_buf = Wire(UInt(51.W))
  val CD_R_buf = Wire(UInt(51.W))
  CD_S_buf := 0.U
  CD_R_buf := 0.U

  switch (io.EXP_DIFF) {
    //is(-2.S) {
    is(0x3ff.S) {
      AB_S_buf := zero_vec(1, 0) ## io.INAB_S(41, 0) ## zero_vec(6, 0)
      AB_R_buf := zero_vec(1, 0) ## io.INAB_R ## zero_vec(0)
      CD_S_buf := io.INCD_S(41, 0) ## zero_vec(8, 0)
      CD_R_buf := io.INCD_R ## zero_vec(2, 0)
    }
    //is(-1.S) {/*
    is(0x3fe.S) {
      AB_S_buf := zero_vec(0) ## io.INAB_S(41, 0) ## zero_vec(7, 0)
      AB_R_buf := zero_vec(0) ## io.INAB_R ## zero_vec(1, 0)
      CD_S_buf := io.INCD_S(41, 0) ## zero_vec(8, 0)
      CD_R_buf := io.INCD_R ## zero_vec(2, 0)

      /*
            AB_S_buf := io.INAB_S(37, 0) ## zero_vec(12, 0)
            AB_R_buf := io.INAB_R ## zero_vec(2, 0)
            CD_S_buf := zero_vec(0) ## io.INCD_S(37, 0) ## zero_vec(11, 0)
            CD_R_buf := zero_vec(0) ## io.INCD_R ## zero_vec(1, 0)
             */
    }
    is(0.S) {
      AB_S_buf := io.INAB_S(41, 0) ## zero_vec(8, 0)
      AB_R_buf := io.INAB_R ## zero_vec(2, 0)
      CD_S_buf := io.INCD_S(41, 0) ## zero_vec(8, 0)
      CD_R_buf := io.INCD_R ## zero_vec(2, 0)
    }
    is(1.S) {
      AB_S_buf := io.INAB_S(41, 0) ## zero_vec(8, 0)
      AB_R_buf := io.INAB_R ## zero_vec(2, 0)
      CD_S_buf := zero_vec(0) ## io.INCD_S(41, 0) ## zero_vec(7, 0)
      CD_R_buf := zero_vec(0) ## io.INCD_R ## zero_vec(1, 0)
    }
    is(2.S) {
      AB_S_buf := io.INAB_S(41, 0) ## zero_vec(8, 0)
      AB_R_buf := io.INAB_R ## zero_vec(2, 0)
      CD_S_buf := zero_vec(1, 0) ## io.INCD_S(41, 0) ## zero_vec(6, 0)
      CD_R_buf := zero_vec(1, 0) ## io.INCD_R ## zero_vec(0)
    }
  }

  io.OUT_AB_S := AB_S_buf
  io.OUT_AB_R := AB_R_buf
  io.OUT_CD_S := CD_S_buf
  io.OUT_CD_R := CD_R_buf
}