package FarPathLogic

import chisel3._
import chisel3.util._
import FourTwoCompressor._

class FarPathLogic_v2 extends Module {
  val io = IO(new Bundle {
    val IN_AB_R = Input(UInt(48.W))
    val IN_AB_S = Input(UInt(43.W))
    val IN_CD_R = Input(UInt(48.W))
    val IN_CD_S = Input(UInt(43.W))
    val IN_EXP_COMP = Input(UInt(1.W)) //AB<CD -> 'H', CD<AB 'L'
    val IN_OP_SEL = Input(UInt(1.W))
    val IN_EXP_DIFF = Input(UInt(8.W))
    val OUT_R = Output(UInt(51.W))
    val OUT_S = Output(UInt(51.W))
  })

  //val Inverted_small_R = Wire(UInt(51.W))
  //val Inverted_small_S = Wire(UInt(51.W))

  val zero_vec = 0.U(13.W)

  val small_R = Wire(UInt(48.W))
  val small_S = Wire(UInt(43.W))
  val large_R = Wire(UInt(48.W))
  val large_S = Wire(UInt(43.W))
  val Aligned_small_R = Wire(UInt(51.W))
  val Aligned_small_S = Wire(UInt(51.W))
  val Aligned_large_R = Wire(UInt(51.W))
  val Aligned_large_S = Wire(UInt(51.W))
  val Inverted_small_R = Wire(UInt(51.W))
  val Inverted_small_S = Wire(UInt(51.W))

  Aligned_large_R := large_R ## zero_vec(2, 0)
  Aligned_large_S := large_S(41, 0) ## zero_vec(8, 0)


  small_R := Mux(io.IN_EXP_COMP === 1.U, io.IN_AB_R, io.IN_CD_R)
  small_S := Mux(io.IN_EXP_COMP === 1.U, io.IN_AB_S, io.IN_CD_S)
  large_R := Mux(io.IN_EXP_COMP === 1.U, io.IN_CD_R, io.IN_AB_R)
  large_S := Mux(io.IN_EXP_COMP === 1.U, io.IN_CD_S, io.IN_AB_S)

  val Align = Module(new MantissaAlignment_v2)
  Align.io.INAB_R := small_R
  Align.io.INAB_S := small_S
  Align.io.shift_val := io.IN_EXP_DIFF
  Aligned_small_R := Align.io.OUT_R
  Aligned_small_S := Align.io.OUT_S

  Inverted_small_R := Mux(io.IN_OP_SEL === 1.U, (~Aligned_small_R).asUInt() + 1.U, Aligned_small_R)
  Inverted_small_S := Mux(io.IN_OP_SEL === 1.U, (~Aligned_small_S).asUInt() + 1.U, Aligned_small_S)

  val FTC51 = Module(new FourTwoCompressor51)
  FTC51.io.A := Aligned_large_R
  FTC51.io.B := Aligned_large_S
  FTC51.io.C := Inverted_small_R
  FTC51.io.D := Inverted_small_S
  io.OUT_R := FTC51.io.R
  io.OUT_S := FTC51.io.S

  //io.OUT_R := Inverted_small_R
  //io.OUT_S := Inverted_small_S

}

// out= {carry out(3'b000), shift value((shift value)'b00...), shifted mantissa((22-shited value)bits), sticky bit(1bit)}
class MantissaAlignment_v2 extends Module {
  val io = IO(new Bundle {
    val INAB_R = Input(UInt(48.W))
    val INAB_S = Input(UInt(43.W))
    val shift_val = Input(UInt(8.W))
    val OUT_R = Output(UInt(51.W))
    val OUT_S = Output(UInt(51.W))
  })

  val zero_vec = 0.U(9.W)
  /*
    val sticky_AB_S = Wire(UInt(1.W))
    val sticky_AB_C = Wire(UInt(1.W))
    val sticky_CD_S = Wire(UInt(1.W))
    val sticky_CD_C = Wire(UInt(1.W))
    val sticky_EF_S = Wire(UInt(1.W))
    val sticky_EF_C = Wire(UInt(1.W))
    val sticky_GH_S = Wire(UInt(1.W))
    val sticky_GH_C = Wire(UInt(1.W))

    //Stick bitをOR treeではなくMuxを用いて表現

    sticky_AB_S := Mux(io.INAB_S(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_AB_C := Mux(io.INAB_C(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_CD_S := Mux(io.INCD_S(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_CD_C := Mux(io.INCD_C(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_EF_S := Mux(io.INEF_S(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_GH_C := Mux(io.INEF_C(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_GH_S := Mux(io.INGH_S(19, 0) =/= 0.U, 1.U, 0.U)
    sticky_GH_C := Mux(io.INGH_C(19, 0) =/= 0.U, 1.U, 0.U)
     */

  val AB_S_buf = Wire(UInt(51.W))
  val AB_R_buf = Wire(UInt(51.W))

  //printf("OUTAB_S = %b\n", io.OUT_S)
  //printf("OUTAB_C = %b\n", io.OUT_R)
  AB_S_buf := 0.U
  AB_R_buf := 0.U
  /*
  val AB_S_buf = 0.U(28.W)
  val AB_C_buf = 0.U(28.W)
  val CD_S_buf = 0.U(28.W)
  val CD_C_buf = 0.U(28.W)
  val EF_S_buf = 0.U(28.W)
  val EF_C_buf = 0.U(28.W)
  val GH_S_buf = 0.U(28.W)
  val GH_C_buf = 0.U(28.W)
     */
  /*
  switch (io.shift_AB) {
    is(0.U) {
      AB_S_buf := "b0000".U ## io.INAB_S ## zero_vec(1, 0) //4+22+2=28
      AB_C_buf := "b0000".U ## io.INAB_C ## zero_vec(1, 0)
    }
    is(1.U) {
      AB_S_buf := "b00000".U ## io.INAB_S ## zero_vec(0) //5+22+1=28
      AB_C_buf := "b00000".U ## io.INAB_C ## zero_vec(1)
    }
    is(2.U) {
      AB_S_buf := "b000000".U ## io.INAB_S //6+22=28
      AB_C_buf := "b000000".U ## io.INAB_C
    }
    is(3.U) {
      AB_S_buf := "b0000_000".U ## io.INAB_S(21, 2) ## (io.INAB_S(1) | io.INAB_S(0)) //7+20+1=28
      AB_C_buf := "b0000_000".U ## io.INAB_C(21, 2) ## (io.INAB_C(1) | io.INAB_C(0))
    }
    is(4.U) {
      AB_S_buf := "b0000_0000".U ## io.INAB_S(21, 3) ## (io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //8+19+1=28
      AB_C_buf := "b0000_0000".U ## io.INAB_C(21, 3) ## (io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(5.U) {
      AB_S_buf := "b0000_0000_0".U ## io.INAB_S(21, 4) ## (io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //9+18+1=28
      AB_C_buf := "b0000_0000_0".U ## io.INAB_C(21, 4) ## (io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(6.U) {
      AB_S_buf := "b0000_0000_00".U ## io.INAB_S(21, 5) ## (io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //10+17+1=28
      AB_C_buf := "b0000_0000_00".U ## io.INAB_C(21, 5) ## (io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(7.U) {
      AB_S_buf := "b0000_0000_000".U ## io.INAB_S(21, 6) ## (io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //11+16+1=28
      AB_C_buf := "b0000_0000_000".U ## io.INAB_C(21, 6) ## (io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(8.U) {
      AB_S_buf := "b0000_0000_0000".U ## io.INAB_S(21, 7) ## (io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //12+15+1=28
      AB_C_buf := "b0000_0000_0000".U ## io.INAB_C(21, 7) ## (io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(9.U) {
      AB_S_buf := "b0000_0000_0000_0".U ## io.INAB_S(21, 8) ## (io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //13+14+1=28
      AB_C_buf := "b0000_0000_0000_0".U ## io.INAB_C(21, 8) ## (io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(10.U) {
      AB_S_buf := "b0000_0000_0000_00".U ## io.INAB_S(21, 9) ## (io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //14+13+1=28
      AB_C_buf := "b0000_0000_0000_00".U ## io.INAB_C(21, 9) ## (io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(11.U) {
      AB_S_buf := "b0000_0000_0000_000".U ## io.INAB_S(21, 10) ## (io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //15+12+1=28
      AB_C_buf := "b0000_0000_0000_000".U ## io.INAB_C(21, 10) ## (io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(12.U) {
      AB_S_buf := "b0000_0000_0000_0000".U ## io.INAB_S(21, 11) ## (io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //16+11+1=28
      AB_C_buf := "b0000_0000_0000_0000".U ## io.INAB_C(21, 11) ## (io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(13.U) {
      AB_S_buf := "b0000_0000_0000_0000_0".U ## io.INAB_S(21, 12) ## (io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //17+10+1=28
      AB_C_buf := "b0000_0000_0000_0000_0".U ## io.INAB_C(21, 12) ## (io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(14.U) {
      AB_S_buf := "b0000_0000_0000_0000_00".U ## io.INAB_S(21, 13) ## (io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //18+9+1=28
      AB_C_buf := "b0000_0000_0000_0000_00".U ## io.INAB_C(21, 13) ## (io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(15.U) {
      AB_S_buf := "b0000_0000_0000_0000_000".U ## io.INAB_S(21, 14) ## (io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //19+8+1=28
      AB_C_buf := "b0000_0000_0000_0000_000".U ## io.INAB_C(21, 14) ## (io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(16.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000".U ## io.INAB_S(21, 15) ## (io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //20+7+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000".U ## io.INAB_C(21, 15) ## (io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(17.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0".U ## io.INAB_S(21, 16) ## (io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //21+6+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_0".U ## io.INAB_C(21, 16) ## (io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(18.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_00".U ## io.INAB_S(21, 17) ## (io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //22+5+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_00".U ## io.INAB_C(21, 17) ## (io.INAB_C(16) | io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(19.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_000".U ## io.INAB_S(21, 18) ## (io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //23+4+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_000".U ## io.INAB_C(21, 18) ## (io.INAB_C(17) | io.INAB_C(16) | io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(20.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INAB_S(21, 19) ## (io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //24+3+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INAB_C(21, 19) ## (io.INAB_C(18) | io.INAB_C(17) | io.INAB_C(16) | io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(21.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(21, 20) ## (io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //25+2+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INAB_C(21, 20) ## (io.INAB_C(19) | io.INAB_C(18) | io.INAB_C(17) | io.INAB_C(16) | io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(22.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(21) ## (io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //26+1+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INAB_C(21) ## (io.INAB_C(20) | io.INAB_C(19) | io.INAB_C(18) | io.INAB_C(17) | io.INAB_C(16) | io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
    is(23.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_C_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INAB_C(21) | io.INAB_C(20) | io.INAB_C(19) | io.INAB_C(18) | io.INAB_C(17) | io.INAB_C(16) | io.INAB_C(15) | io.INAB_C(14) | io.INAB_C(13) | io.INAB_C(12) | io.INAB_C(11) | io.INAB_C(10) | io.INAB_C(9) | io.INAB_C(8) | io.INAB_C(7) | io.INAB_C(6) | io.INAB_C(5) | io.INAB_C(4) | io.INAB_C(3) | io.INAB_C(2) | io.INAB_C(1) | io.INAB_C(0))
    }
  }

  switch (io.shift_CD) {
    is(0.U) {
      CD_S_buf := "b0000".U ## io.INCD_S ## zero_vec(1, 0) //4+22+2=28
      CD_C_buf := "b0000".U ## io.INCD_C ## zero_vec(1, 0)
    }
    is(1.U) {
      CD_S_buf := "b00000".U ## io.INCD_S ## zero_vec(0) //5+22+1=28
      CD_C_buf := "b00000".U ## io.INCD_C ## zero_vec(1)
    }
    is(2.U) {
      CD_S_buf := "b000000".U ## io.INCD_S //6+22=28
      CD_C_buf := "b000000".U ## io.INCD_C
    }
    is(3.U) {
      CD_S_buf := "b0000_000".U ## io.INCD_S(21, 2) ## (io.INCD_S(1) | io.INCD_S(0)) //7+20+1=28
      CD_C_buf := "b0000_000".U ## io.INCD_C(21, 2) ## (io.INCD_C(1) | io.INCD_C(0))
    }
    is(4.U) {
      CD_S_buf := "b0000_0000".U ## io.INCD_S(21, 3) ## (io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //8+19+1=28
      CD_C_buf := "b0000_0000".U ## io.INCD_C(21, 3) ## (io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(5.U) {
      CD_S_buf := "b0000_0000_0".U ## io.INCD_S(21, 4) ## (io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //9+18+1=28
      CD_C_buf := "b0000_0000_0".U ## io.INCD_C(21, 4) ## (io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(6.U) {
      CD_S_buf := "b0000_0000_00".U ## io.INCD_S(21, 5) ## (io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //10+17+1=28
      CD_C_buf := "b0000_0000_00".U ## io.INCD_C(21, 5) ## (io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(7.U) {
      CD_S_buf := "b0000_0000_000".U ## io.INCD_S(21, 6) ## (io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //11+16+1=28
      CD_C_buf := "b0000_0000_000".U ## io.INCD_C(21, 6) ## (io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(8.U) {
      CD_S_buf := "b0000_0000_0000".U ## io.INCD_S(21, 7) ## (io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //12+15+1=28
      CD_C_buf := "b0000_0000_0000".U ## io.INCD_C(21, 7) ## (io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(9.U) {
      CD_S_buf := "b0000_0000_0000_0".U ## io.INCD_S(21, 8) ## (io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //13+14+1=28
      CD_C_buf := "b0000_0000_0000_0".U ## io.INCD_C(21, 8) ## (io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(10.U) {
      CD_S_buf := "b0000_0000_0000_00".U ## io.INCD_S(21, 9) ## (io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //14+13+1=28
      CD_C_buf := "b0000_0000_0000_00".U ## io.INCD_C(21, 9) ## (io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(11.U) {
      CD_S_buf := "b0000_0000_0000_000".U ## io.INCD_S(21, 10) ## (io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //15+12+1=28
      CD_C_buf := "b0000_0000_0000_000".U ## io.INCD_C(21, 10) ## (io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(12.U) {
      CD_S_buf := "b0000_0000_0000_0000".U ## io.INCD_S(21, 11) ## (io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //16+11+1=28
      CD_C_buf := "b0000_0000_0000_0000".U ## io.INCD_C(21, 11) ## (io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(13.U) {
      CD_S_buf := "b0000_0000_0000_0000_0".U ## io.INCD_S(21, 12) ## (io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //17+10+1=28
      CD_C_buf := "b0000_0000_0000_0000_0".U ## io.INCD_C(21, 12) ## (io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(14.U) {
      CD_S_buf := "b0000_0000_0000_0000_00".U ## io.INCD_S(21, 13) ## (io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //18+9+1=28
      CD_C_buf := "b0000_0000_0000_0000_00".U ## io.INCD_C(21, 13) ## (io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(15.U) {
      CD_S_buf := "b0000_0000_0000_0000_000".U ## io.INCD_S(21, 14) ## (io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //19+8+1=28
      CD_C_buf := "b0000_0000_0000_0000_000".U ## io.INCD_C(21, 14) ## (io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(16.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000".U ## io.INCD_S(21, 15) ## (io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //20+7+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000".U ## io.INCD_C(21, 15) ## (io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(17.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_0".U ## io.INCD_S(21, 16) ## (io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //21+6+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_0".U ## io.INCD_C(21, 16) ## (io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(18.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_00".U ## io.INCD_S(21, 17) ## (io.INCD_S(16) | io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //22+5+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_00".U ## io.INCD_C(21, 17) ## (io.INCD_C(16) | io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(19.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_000".U ## io.INCD_S(21, 18) ## (io.INCD_S(17) | io.INCD_S(16) | io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //23+4+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_000".U ## io.INCD_C(21, 18) ## (io.INCD_C(17) | io.INCD_C(16) | io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(20.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INCD_S(21, 19) ## (io.INCD_S(18) | io.INCD_S(17) | io.INCD_S(16) | io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //24+3+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INCD_C(21, 19) ## (io.INCD_C(18) | io.INCD_C(17) | io.INCD_C(16) | io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(21.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INCD_S(21, 20) ## (io.INCD_S(19) | io.INCD_S(18) | io.INCD_S(17) | io.INCD_S(16) | io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //25+2+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INCD_C(21, 20) ## (io.INCD_C(19) | io.INCD_C(18) | io.INCD_C(17) | io.INCD_C(16) | io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(22.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INCD_S(21) ## (io.INCD_S(20) | io.INCD_S(19) | io.INCD_S(18) | io.INCD_S(17) | io.INCD_S(16) | io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //26+1+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INCD_C(21) ## (io.INCD_C(20) | io.INCD_C(19) | io.INCD_C(18) | io.INCD_C(17) | io.INCD_C(16) | io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
    is(23.U) {
      CD_S_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INCD_S(21) | io.INCD_S(20) | io.INCD_S(19) | io.INCD_S(18) | io.INCD_S(17) | io.INCD_S(16) | io.INCD_S(15) | io.INCD_S(14) | io.INCD_S(13) | io.INCD_S(12) | io.INCD_S(11) | io.INCD_S(10) | io.INCD_S(9) | io.INCD_S(8) | io.INCD_S(7) | io.INCD_S(6) | io.INCD_S(5) | io.INCD_S(4) | io.INCD_S(3) | io.INCD_S(2) | io.INCD_S(1) | io.INCD_S(0)) //27+0+1=28
      CD_C_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INCD_C(21) | io.INCD_C(20) | io.INCD_C(19) | io.INCD_C(18) | io.INCD_C(17) | io.INCD_C(16) | io.INCD_C(15) | io.INCD_C(14) | io.INCD_C(13) | io.INCD_C(12) | io.INCD_C(11) | io.INCD_C(10) | io.INCD_C(9) | io.INCD_C(8) | io.INCD_C(7) | io.INCD_C(6) | io.INCD_C(5) | io.INCD_C(4) | io.INCD_C(3) | io.INCD_C(2) | io.INCD_C(1) | io.INCD_C(0))
    }
  }
  switch (io.shift_EF) {
    is(0.U) {
      EF_S_buf := "b0000".U ## io.INEF_S ## zero_vec(1, 0) //4+22+2=28
      EF_C_buf := "b0000".U ## io.INEF_C ## zero_vec(1, 0)
    }
    is(1.U) {
      EF_S_buf := "b00000".U ## io.INEF_S ## zero_vec(0) //5+22+1=28
      EF_C_buf := "b00000".U ## io.INEF_C ## zero_vec(1)
    }
    is(2.U) {
      EF_S_buf := "b000000".U ## io.INEF_S //6+22=28
      EF_C_buf := "b000000".U ## io.INEF_C
    }
    is(3.U) {
      EF_S_buf := "b0000_000".U ## io.INEF_S(21, 2) ## (io.INEF_S(1) | io.INEF_S(0)) //7+20+1=28
      EF_C_buf := "b0000_000".U ## io.INEF_C(21, 2) ## (io.INEF_C(1) | io.INEF_C(0))
    }
    is(4.U) {
      EF_S_buf := "b0000_0000".U ## io.INEF_S(21, 3) ## (io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //8+19+1=28
      EF_C_buf := "b0000_0000".U ## io.INEF_C(21, 3) ## (io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(5.U) {
      EF_S_buf := "b0000_0000_0".U ## io.INEF_S(21, 4) ## (io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //9+18+1=28
      EF_C_buf := "b0000_0000_0".U ## io.INEF_C(21, 4) ## (io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(6.U) {
      EF_S_buf := "b0000_0000_00".U ## io.INEF_S(21, 5) ## (io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //10+17+1=28
      EF_C_buf := "b0000_0000_00".U ## io.INEF_C(21, 5) ## (io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(7.U) {
      EF_S_buf := "b0000_0000_000".U ## io.INEF_S(21, 6) ## (io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //11+16+1=28
      EF_C_buf := "b0000_0000_000".U ## io.INEF_C(21, 6) ## (io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(8.U) {
      EF_S_buf := "b0000_0000_0000".U ## io.INEF_S(21, 7) ## (io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //12+15+1=28
      EF_C_buf := "b0000_0000_0000".U ## io.INEF_C(21, 7) ## (io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(9.U) {
      EF_S_buf := "b0000_0000_0000_0".U ## io.INEF_S(21, 8) ## (io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //13+14+1=28
      EF_C_buf := "b0000_0000_0000_0".U ## io.INEF_C(21, 8) ## (io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(10.U) {
      EF_S_buf := "b0000_0000_0000_00".U ## io.INEF_S(21, 9) ## (io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //14+13+1=28
      EF_C_buf := "b0000_0000_0000_00".U ## io.INEF_C(21, 9) ## (io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(11.U) {
      EF_S_buf := "b0000_0000_0000_000".U ## io.INEF_S(21, 10) ## (io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //15+12+1=28
      EF_C_buf := "b0000_0000_0000_000".U ## io.INEF_C(21, 10) ## (io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(12.U) {
      EF_S_buf := "b0000_0000_0000_0000".U ## io.INEF_S(21, 11) ## (io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //16+11+1=28
      EF_C_buf := "b0000_0000_0000_0000".U ## io.INEF_C(21, 11) ## (io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(13.U) {
      EF_S_buf := "b0000_0000_0000_0000_0".U ## io.INEF_S(21, 12) ## (io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //17+10+1=28
      EF_C_buf := "b0000_0000_0000_0000_0".U ## io.INEF_C(21, 12) ## (io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(14.U) {
      EF_S_buf := "b0000_0000_0000_0000_00".U ## io.INEF_S(21, 13) ## (io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //18+9+1=28
      EF_C_buf := "b0000_0000_0000_0000_00".U ## io.INEF_C(21, 13) ## (io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(15.U) {
      EF_S_buf := "b0000_0000_0000_0000_000".U ## io.INEF_S(21, 14) ## (io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //19+8+1=28
      EF_C_buf := "b0000_0000_0000_0000_000".U ## io.INEF_C(21, 14) ## (io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(16.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000".U ## io.INEF_S(21, 15) ## (io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //20+7+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000".U ## io.INEF_C(21, 15) ## (io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(17.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_0".U ## io.INEF_S(21, 16) ## (io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //21+6+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_0".U ## io.INEF_C(21, 16) ## (io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(18.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_00".U ## io.INEF_S(21, 17) ## (io.INEF_S(16) | io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //22+5+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_00".U ## io.INEF_C(21, 17) ## (io.INEF_C(16) | io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(19.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_000".U ## io.INEF_S(21, 18) ## (io.INEF_S(17) | io.INEF_S(16) | io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //23+4+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_000".U ## io.INEF_C(21, 18) ## (io.INEF_C(17) | io.INEF_C(16) | io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(20.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INEF_S(21, 19) ## (io.INEF_S(18) | io.INEF_S(17) | io.INEF_S(16) | io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //24+3+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INEF_C(21, 19) ## (io.INEF_C(18) | io.INEF_C(17) | io.INEF_C(16) | io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(21.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INEF_S(21, 20) ## (io.INEF_S(19) | io.INEF_S(18) | io.INEF_S(17) | io.INEF_S(16) | io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //25+2+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INEF_C(21, 20) ## (io.INEF_C(19) | io.INEF_C(18) | io.INEF_C(17) | io.INEF_C(16) | io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(22.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INEF_S(21) ## (io.INEF_S(20) | io.INEF_S(19) | io.INEF_S(18) | io.INEF_S(17) | io.INEF_S(16) | io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //26+1+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INEF_C(21) ## (io.INEF_C(20) | io.INEF_C(19) | io.INEF_C(18) | io.INEF_C(17) | io.INEF_C(16) | io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
    is(23.U) {
      EF_S_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INEF_S(21) | io.INEF_S(20) | io.INEF_S(19) | io.INEF_S(18) | io.INEF_S(17) | io.INEF_S(16) | io.INEF_S(15) | io.INEF_S(14) | io.INEF_S(13) | io.INEF_S(12) | io.INEF_S(11) | io.INEF_S(10) | io.INEF_S(9) | io.INEF_S(8) | io.INEF_S(7) | io.INEF_S(6) | io.INEF_S(5) | io.INEF_S(4) | io.INEF_S(3) | io.INEF_S(2) | io.INEF_S(1) | io.INEF_S(0)) //27+0+1=28
      EF_C_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INEF_C(21) | io.INEF_C(20) | io.INEF_C(19) | io.INEF_C(18) | io.INEF_C(17) | io.INEF_C(16) | io.INEF_C(15) | io.INEF_C(14) | io.INEF_C(13) | io.INEF_C(12) | io.INEF_C(11) | io.INEF_C(10) | io.INEF_C(9) | io.INEF_C(8) | io.INEF_C(7) | io.INEF_C(6) | io.INEF_C(5) | io.INEF_C(4) | io.INEF_C(3) | io.INEF_C(2) | io.INEF_C(1) | io.INEF_C(0))
    }
  }
  switch (io.shift_GH) {
    is(0.U) {
      GH_S_buf := "b0000".U ## io.INGH_S ## zero_vec(1, 0) //4+22+2=28
      GH_C_buf := "b0000".U ## io.INGH_C ## zero_vec(1, 0)
    }
    is(1.U) {
      GH_S_buf := "b00000".U ## io.INGH_S ## zero_vec(0) //5+22+1=28
      GH_C_buf := "b00000".U ## io.INGH_C ## zero_vec(1)
    }
    is(2.U) {
      GH_S_buf := "b000000".U ## io.INGH_S //6+22=28
      GH_C_buf := "b000000".U ## io.INGH_C
    }
    is(3.U) {
      GH_S_buf := "b0000_000".U ## io.INGH_S(21, 2) ## (io.INGH_S(1) | io.INGH_S(0)) //7+20+1=28
      GH_C_buf := "b0000_000".U ## io.INGH_C(21, 2) ## (io.INGH_C(1) | io.INGH_C(0))
    }
    is(4.U) {
      GH_S_buf := "b0000_0000".U ## io.INGH_S(21, 3) ## (io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //8+19+1=28
      GH_C_buf := "b0000_0000".U ## io.INGH_C(21, 3) ## (io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(5.U) {
      GH_S_buf := "b0000_0000_0".U ## io.INGH_S(21, 4) ## (io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //9+18+1=28
      GH_C_buf := "b0000_0000_0".U ## io.INGH_C(21, 4) ## (io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(6.U) {
      GH_S_buf := "b0000_0000_00".U ## io.INGH_S(21, 5) ## (io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //10+17+1=28
      GH_C_buf := "b0000_0000_00".U ## io.INGH_C(21, 5) ## (io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(7.U) {
      GH_S_buf := "b0000_0000_000".U ## io.INGH_S(21, 6) ## (io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //11+16+1=28
      GH_C_buf := "b0000_0000_000".U ## io.INGH_C(21, 6) ## (io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(8.U) {
      GH_S_buf := "b0000_0000_0000".U ## io.INGH_S(21, 7) ## (io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //12+15+1=28
      GH_C_buf := "b0000_0000_0000".U ## io.INGH_C(21, 7) ## (io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(9.U) {
      GH_S_buf := "b0000_0000_0000_0".U ## io.INGH_S(21, 8) ## (io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //13+14+1=28
      GH_C_buf := "b0000_0000_0000_0".U ## io.INGH_C(21, 8) ## (io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(10.U) {
      GH_S_buf := "b0000_0000_0000_00".U ## io.INGH_S(21, 9) ## (io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //14+13+1=28
      GH_C_buf := "b0000_0000_0000_00".U ## io.INGH_C(21, 9) ## (io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(11.U) {
      GH_S_buf := "b0000_0000_0000_000".U ## io.INGH_S(21, 10) ## (io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //15+12+1=28
      GH_C_buf := "b0000_0000_0000_000".U ## io.INGH_C(21, 10) ## (io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(12.U) {
      GH_S_buf := "b0000_0000_0000_0000".U ## io.INGH_S(21, 11) ## (io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //16+11+1=28
      GH_C_buf := "b0000_0000_0000_0000".U ## io.INGH_C(21, 11) ## (io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(13.U) {
      GH_S_buf := "b0000_0000_0000_0000_0".U ## io.INGH_S(21, 12) ## (io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //17+10+1=28
      GH_C_buf := "b0000_0000_0000_0000_0".U ## io.INGH_C(21, 12) ## (io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(14.U) {
      GH_S_buf := "b0000_0000_0000_0000_00".U ## io.INGH_S(21, 13) ## (io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //18+9+1=28
      GH_C_buf := "b0000_0000_0000_0000_00".U ## io.INGH_C(21, 13) ## (io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(15.U) {
      GH_S_buf := "b0000_0000_0000_0000_000".U ## io.INGH_S(21, 14) ## (io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //19+8+1=28
      GH_C_buf := "b0000_0000_0000_0000_000".U ## io.INGH_C(21, 14) ## (io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(16.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000".U ## io.INGH_S(21, 15) ## (io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //20+7+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000".U ## io.INGH_C(21, 15) ## (io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(17.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_0".U ## io.INGH_S(21, 16) ## (io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //21+6+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_0".U ## io.INGH_C(21, 16) ## (io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(18.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_00".U ## io.INGH_S(21, 17) ## (io.INGH_S(16) | io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //22+5+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_00".U ## io.INGH_C(21, 17) ## (io.INGH_C(16) | io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(19.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_000".U ## io.INGH_S(21, 18) ## (io.INGH_S(17) | io.INGH_S(16) | io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //23+4+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_000".U ## io.INGH_C(21, 18) ## (io.INGH_C(17) | io.INGH_C(16) | io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(20.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INGH_S(21, 19) ## (io.INGH_S(18) | io.INGH_S(17) | io.INGH_S(16) | io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //24+3+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INGH_C(21, 19) ## (io.INGH_C(18) | io.INGH_C(17) | io.INGH_C(16) | io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(21.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INGH_S(21, 20) ## (io.INGH_S(19) | io.INGH_S(18) | io.INGH_S(17) | io.INGH_S(16) | io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //25+2+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INGH_C(21, 20) ## (io.INGH_C(19) | io.INGH_C(18) | io.INGH_C(17) | io.INGH_C(16) | io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(22.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INGH_S(21) ## (io.INGH_S(20) | io.INGH_S(19) | io.INGH_S(18) | io.INGH_S(17) | io.INGH_S(16) | io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //26+1+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INGH_C(21) ## (io.INGH_C(20) | io.INGH_C(19) | io.INGH_C(18) | io.INGH_C(17) | io.INGH_C(16) | io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
    is(23.U) {
      GH_S_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INGH_S(21) | io.INGH_S(20) | io.INGH_S(19) | io.INGH_S(18) | io.INGH_S(17) | io.INGH_S(16) | io.INGH_S(15) | io.INGH_S(14) | io.INGH_S(13) | io.INGH_S(12) | io.INGH_S(11) | io.INGH_S(10) | io.INGH_S(9) | io.INGH_S(8) | io.INGH_S(7) | io.INGH_S(6) | io.INGH_S(5) | io.INGH_S(4) | io.INGH_S(3) | io.INGH_S(2) | io.INGH_S(1) | io.INGH_S(0)) //27+0+1=28
      GH_C_buf := "b0000_0000_0000_0000_0000_0000_000".U ## (io.INGH_C(21) | io.INGH_C(20) | io.INGH_C(19) | io.INGH_C(18) | io.INGH_C(17) | io.INGH_C(16) | io.INGH_C(15) | io.INGH_C(14) | io.INGH_C(13) | io.INGH_C(12) | io.INGH_C(11) | io.INGH_C(10) | io.INGH_C(9) | io.INGH_C(8) | io.INGH_C(7) | io.INGH_C(6) | io.INGH_C(5) | io.INGH_C(4) | io.INGH_C(3) | io.INGH_C(2) | io.INGH_C(1) | io.INGH_C(0))
    }
  }
*/
  switch (io.shift_val) {
    is(0.U) {
      AB_S_buf := io.INAB_S(41, 0)  ## zero_vec(8, 0) //39+3=42
      AB_R_buf := io.INAB_R ## zero_vec(2, 0) //48+3=51
    }
    is(1.U) {
      AB_S_buf := "b0".U ## io.INAB_S(41, 0) ## zero_vec(7,0) //5+22+1=28
      AB_R_buf := "b0".U ## io.INAB_R ## zero_vec(1, 0)
    }
    is(2.U) {
      AB_S_buf := "b00".U ## io.INAB_S(41, 0)  ## zero_vec(6, 0) //6+22=28
      AB_R_buf := "b00".U ## io.INAB_R ## zero_vec(1)
    }
    is(3.U) {
      AB_S_buf := "b000".U ## io.INAB_S(41, 0) ## zero_vec(5, 0)   //7+20+1=28
      AB_R_buf := "b000".U ## io.INAB_R
    }
    is(4.U) {
      AB_S_buf := "b0000".U ## io.INAB_S(41, 0) ## zero_vec(4, 0)   //7+20+1=28
      AB_R_buf := "b0000".U ## io.INAB_R(47, 2) ## (io.INAB_R(1) | io.INAB_R(0))
    }
    is(5.U) {
      AB_S_buf := "b0000_0".U ## io.INAB_S(41, 0) ## zero_vec(3, 0)   //7+20+1=288
      AB_R_buf := "b0000_0".U ## io.INAB_R(47, 3) ## (io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(6.U) {
      AB_S_buf := "b0000_00".U ## io.INAB_S(41, 0) ## zero_vec(2, 0)   //7+20+1=288
      AB_R_buf := "b0000_00".U ## io.INAB_R(47, 4) ## (io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(7.U) {
      AB_S_buf := "b0000_000".U ## io.INAB_S(41, 0) ## zero_vec(1, 0)   //7+20+1=288
      AB_R_buf := "b0000_000".U ## io.INAB_R(47, 5) ## (io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(8.U) {
      AB_S_buf := "b0000_0000".U ## io.INAB_S(41, 0) ## zero_vec(0)   //7+20+1=288
      AB_R_buf := "b0000_0000".U ## io.INAB_R(47, 6) ## (io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(9.U) {
      AB_S_buf := "b0000_0000_0".U ## io.INAB_S(41, 0)  //7+20+1=288//13+14+1=28
      AB_R_buf := "b0000_0000_0".U ## io.INAB_R(47, 7) ## (io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(10.U) {
      AB_S_buf := "b0000_0000_00".U ## io.INAB_S(41, 2) ## (io.INAB_S(1) | io.INAB_S(0))  //7+20+1=288
      AB_R_buf := "b0000_0000_00".U ## io.INAB_R(47, 8) ## (io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(11.U) {
      AB_S_buf := "b0000_0000_000".U ## io.INAB_S(41, 3) ## (io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //7+20+1=288
      AB_R_buf := "b0000_0000_000".U ## io.INAB_R(47, 9) ## (io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(12.U) {
      AB_S_buf := "b0000_0000_0000".U ## io.INAB_S(41, 4) ## (io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0))   //7+20+1=288
      AB_R_buf := "b0000_0000_0000".U ## io.INAB_R(47, 10) ## (io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(13.U) {
      AB_S_buf := "b0000_0000_0000_0".U ## io.INAB_S(41, 5) ## (io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0))
      AB_R_buf := "b0000_0000_0000_0".U ## io.INAB_R(47, 11) ## (io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(14.U) {
      AB_S_buf := "b0000_0000_0000_00".U ## io.INAB_S(41, 6)  ## (io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0))
      AB_R_buf := "b0000_0000_0000_00".U ## io.INAB_R(47, 12) ## (io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(15.U) {
      AB_S_buf := "b0000_0000_0000_000".U ## io.INAB_S(41, 7) ## (io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //23+4+1=28
      AB_R_buf := "b0000_0000_0000_000".U ## io.INAB_R(47, 13) ## (io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(16.U) {
      AB_S_buf := "b0000_0000_0000_0000".U ## io.INAB_S(41, 8) ## (io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //24+3+1=28
      AB_R_buf := "b0000_0000_0000_0000".U ## io.INAB_R(47, 14) ## (io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(17.U) {
      AB_S_buf := "b0000_0000_0000_0000_0".U ## io.INAB_S(41, 9) ## (io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //25+2+1=28
      AB_R_buf := "b0000_0000_0000_0000_0".U ## io.INAB_R(47, 15) ## (io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(18.U) {
      AB_S_buf := "b0000_0000_0000_0000_00".U ## io.INAB_S(41, 10) ## (io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //26+1+1=28
      AB_R_buf := "b0000_0000_0000_0000_00".U ## io.INAB_R(47, 16) ## (io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(19.U) {
      AB_S_buf := "b0000_0000_0000_0000_000".U ## io.INAB_S(41,11) ## (io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0))
      AB_R_buf := "b0000_0000_0000_0000_000".U ## io.INAB_R(47, 17) ## (io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(20.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000".U ## io.INAB_S(41, 12) ## (io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0))
      AB_R_buf := "b0000_0000_0000_0000_0000".U ## io.INAB_R(47, 18) ## (io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(21.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 13) ## (io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //25+2+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 19) ## ( io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(22.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 14) ## (io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //26+1+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 20) ## (io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(23.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 15) ## (io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 21) ## (io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(24.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 16) ## (io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 22) ## (io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(25.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 17) ## (io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(13) | io.INAB_S(14) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 23) ## (io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(26.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 18) ## (io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 24) ## (io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(27.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 19) ## (io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 25) ## (io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(28.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 20) ## (io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 26) ## (io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(29.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 21) ## (io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 27) ## (io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(30.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 22) ## (io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 28) ## (io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(31.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 23) ## (io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 29) ## (io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(32.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 24) ## (io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 30) ## (io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(33.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 25) ## (io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 31) ## (io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(34.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 26) ## (io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 32) ## (io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(35.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 27) ## (io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 33) ## (io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(36.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 28) ## (io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 34) ## (io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(37.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 29) ## (io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 35) ## (io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(38.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 30) ## (io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 36) ## (io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(39.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 31) ## (io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 41) ## (io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(40.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 32) ## (io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 38) ## (io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(41.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 33) ## (io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 39) ## (io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(42.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 34) ## (io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 40) ## (io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(43.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 35) ## (io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 41) ## (io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }

    is(44.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 36) ## (io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 42) ## (io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }

    is(45.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41, 37) ## (io.INAB_S(36) | io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47, 43) ## (io.INAB_R(42) | io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(29) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(46.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_S(41, 38) ## (io.INAB_S(37) | io.INAB_S(36) | io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## io.INAB_R(47, 44) ## (io.INAB_R(43) | io.INAB_R(42) | io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(47.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_S(41, 39) ## (io.INAB_S(38) | io.INAB_S(37) | io.INAB_S(36) | io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_000".U ## io.INAB_R(47, 45) ## (io.INAB_R(44) | io.INAB_R(43) | io.INAB_R(42) | io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(48.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_S(41, 40) ## (io.INAB_S(39) | io.INAB_S(38) | io.INAB_S(37) | io.INAB_S(36) | io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000".U ## io.INAB_R(47, 46) ## (io.INAB_R(45) | io.INAB_R(44) | io.INAB_R(43) | io.INAB_R(42) | io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(49.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_S(41) ## (io.INAB_S(40) | io.INAB_S(39) | io.INAB_S(38) | io.INAB_S(37) | io.INAB_S(36) | io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0".U ## io.INAB_R(47) ## (io.INAB_R(46) | io.INAB_R(45) | io.INAB_R(44) | io.INAB_R(43) | io.INAB_R(42) | io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
    is(50.U) {
      AB_S_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## (io.INAB_S(41) | io.INAB_S(40) | io.INAB_S(39) | io.INAB_S(38) | io.INAB_S(37) | io.INAB_S(36) | io.INAB_S(35) | io.INAB_S(34) | io.INAB_S(33) | io.INAB_S(32) | io.INAB_S(31) | io.INAB_S(30) | io.INAB_S(29) | io.INAB_S(28) | io.INAB_S(27) | io.INAB_S(26) | io.INAB_S(25) | io.INAB_S(24) | io.INAB_S(23) | io.INAB_S(22) | io.INAB_S(21) | io.INAB_S(20) | io.INAB_S(19) | io.INAB_S(18) | io.INAB_S(17) | io.INAB_S(16) | io.INAB_S(15) | io.INAB_S(14) | io.INAB_S(13) | io.INAB_S(12) | io.INAB_S(11) | io.INAB_S(10) | io.INAB_S(9) | io.INAB_S(8) | io.INAB_S(7) | io.INAB_S(6) | io.INAB_S(5) | io.INAB_S(4) | io.INAB_S(3) | io.INAB_S(2) | io.INAB_S(1) | io.INAB_S(0)) //27+0+1=28
      AB_R_buf := "b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_00".U ## (io.INAB_R(47) | io.INAB_R(46) | io.INAB_R(44) | io.INAB_R(43) | io.INAB_R(42) | io.INAB_R(41) | io.INAB_R(40) | io.INAB_R(39) | io.INAB_R(38) | io.INAB_R(41) | io.INAB_R(36) | io.INAB_R(35) | io.INAB_R(34) | io.INAB_R(33) | io.INAB_R(32) | io.INAB_R(31) | io.INAB_R(30) | io.INAB_R(29) | io.INAB_R(28) | io.INAB_R(27) | io.INAB_R(26) | io.INAB_R(25) | io.INAB_R(24) | io.INAB_R(23) | io.INAB_R(22) | io.INAB_R(21) | io.INAB_R(20) | io.INAB_R(19) | io.INAB_R(18) | io.INAB_R(17) | io.INAB_R(16) | io.INAB_R(15) | io.INAB_R(14) | io.INAB_R(13) | io.INAB_R(12) | io.INAB_R(11) | io.INAB_R(10) | io.INAB_R(9) | io.INAB_R(8) | io.INAB_R(7) | io.INAB_R(6) | io.INAB_R(5) | io.INAB_R(4) | io.INAB_R(3) | io.INAB_R(2) | io.INAB_R(1) | io.INAB_R(0))
    }
  }


  io.OUT_S := AB_S_buf
  io.OUT_R := AB_R_buf

}
