package FusedDotProduct

import ExpCompare._
import chisel3._
import chisel3.util._
import CarrySaveAdder._
import ClosePathLogic._
import FarPathLogic._
import ExponentAdjust._

//class FPAdder(LATENCY:Int = 6) extends Module{
class FusedDotProduct extends Module{
  val io = IO(new Bundle{
    val enable = Input(Bool())
    val INA = Input(UInt(32.W))
    val INB = Input(UInt(32.W))
    val INC = Input(UInt(32.W))
    val IND = Input(UInt(32.W))
    val Op_Sel = Input(UInt(5.W))
    val OUT = Output(UInt(32.W))
    val EXCEPTION = Output(UInt(1.W))
  })
  val zero_vec = 0.U(23.W)
  //val io.enable := 1.B
  //io.OUT := io.INA
  /*
  val Exception_zero = Reg(Vec(4, Bool()))
  val Exception_zero_fifo = Reg(Vec(9, Bool()))
  val Exception_inf = Reg(Vec(4, Bool()))
  val Exception_inf_fifo = Reg(Vec(9, Bool()))
     */
  val Op_Sel = Wire(UInt(1.W))
  val Op_Sel_reg = Reg(UInt(1.W))
  val Op_Sel_buf = Reg(UInt(1.W))
  val Sign_AB = Wire(UInt(1.W))
  val Sign_CD = Wire(UInt(1.W))
  val Op_forSign = Wire(UInt(1.W))
  val Op_forSign_buf0 = Reg(UInt(1.W))
  val Op_forSign_buf1 = Reg(UInt(1.W))
  Sign_AB := Mux(io.Op_Sel(2) === 0.U, io.INA(31) ^ io.INB(31), io.INA(31))
  //Sign_AB := Mux(io.Op_Sel(2) && io.Op_Sel(4) === 0.U, io.INA(31) ^ io.INB(31),
  //            Mux(io.Op_Sel(4) === 0.U, io.INA(31), 0.U))
  Sign_CD := Mux(io.Op_Sel(1) === 0.U, io.INC(31) ^ io.IND(31), io.INC(31))
  //Sign_CD := Mux(io.Op_Sel(1) && io.Op_Sel(3) === 0.U, io.INC(31) ^ io.IND(31),
  //  Mux(io.Op_Sel(3) === 0.U, io.INC(31), 0.U))
  Op_Sel := Mux(io.Op_Sel(0) === 0.U, Sign_AB ^ Sign_CD, !(Sign_AB ^ Sign_CD))
  Op_forSign := io.Op_Sel(0)
  val Sign_AB_buf0 = Reg(UInt(1.W))
  val Sign_CD_buf0 = Reg(UInt(1.W))
  val Sign_AB_buf1 = Reg(UInt(1.W))
  val Sign_CD_buf1 = Reg(UInt(1.W))

  //Exponent Compare Logic
  val ExpCompare = Module(new ExpCompare)
  val Exp_Out = Wire(UInt(8.W))
  val Exp_Diff = Wire(SInt(10.W))
  val Shift_Value = Wire(UInt(8.W))
  val Exp_Comp = Wire(UInt(1.W))
  val Path_Sel = Wire(UInt(1.W))
  ExpCompare.io.Exp_A := io.INA(30, 23)
  //ExpCompare.io.Exp_B := Mux(io.Op_Sel(2) === 0.U && io.Op_Sel(4) === 0.U, io.INB(30, 23),
  //                        Mux(io.Op_Sel(4) === 0.U, "b01111111".U, "b00000000".U))
  ExpCompare.io.Exp_B := Mux(io.Op_Sel(2) === 0.U, io.INB(30, 23), "b01111111".U)
  ExpCompare.io.Exp_C := io.INC(30, 23)
  //ExpCompare.io.Exp_D := Mux(io.Op_Sel(1) === 0.U && io.Op_Sel(3) === 0.U, io.IND(30, 23),
  //                        Mux(io.Op_Sel(3) === 0.U, "b01111111".U, "b00000000".U))
  ExpCompare.io.Exp_D := Mux(io.Op_Sel(1) === 0.U, io.IND(30, 23), "b01111111".U)
  //ExpCompare.io.Sign_AB := Sign_AB
  //ExpCompare.io.Sign_CD := Sign_CD
  ExpCompare.io.Op_sel := Op_Sel
  Exp_Out := ExpCompare.io.Exp_Out
  Shift_Value := ExpCompare.io.Shift_Value
  Exp_Diff := ExpCompare.io.Exp_Diff
  Exp_Comp := ExpCompare.io.Exp_comp
  Path_Sel := ExpCompare.io.Path_sel
  val Exp_Out_reg = Reg(UInt(8.W))
  val Exp_Diff_reg = Reg(SInt(10.W))
  val Shift_Value_reg = Reg(UInt(8.W))
  val Exp_Comp_reg = Reg(UInt(1.W))
  val Path_Sel_reg = Reg(UInt(1.W))

  val Exp_Out_buf = Reg(UInt(8.W))
  val Exp_Comp_buf = Reg(UInt(1.W))
  val Path_Sel_buf = Reg(UInt(1.W))

  //Multiplier Trees Logic
  val CSA0 = Module(new CarrySaveAdder_v2)
  val CSA1 = Module(new CarrySaveAdder_v2)
  val CSA0_R = Wire(UInt(48.W))
  val CSA0_S = Wire(UInt(39.W))
  val CSA1_R = Wire(UInt(48.W))
  val CSA1_S = Wire(UInt(39.W))
  val CSA0_R_reg = Reg(UInt(48.W))
  val CSA0_S_reg = Reg(UInt(39.W))
  val CSA1_R_reg = Reg(UInt(48.W))
  val CSA1_S_reg = Reg(UInt(39.W))
  CSA0.io.INA := ("b1".U ## io.INA(22, 0))
  CSA0.io.INB := Mux(io.Op_Sel(2) === 0.U, ("b1".U ## io.INB(22, 0)), ("b1".U ## zero_vec))
  //CSA0.io.INB := Mux(io.Op_Sel(2) && io.Op_Sel(4) === 0.U, ("b1".U ## io.INB(22, 0)),
  //                Mux(io.Op_Sel(4) === 0.U, ("b1".U ## zero_vec), 0.U))
  CSA0_R := CSA0.io.OUTR
  CSA0_S := CSA0.io.OUTS
  CSA1.io.INA := ("b1".U ## io.INC(22, 0))
  CSA1.io.INB := Mux(io.Op_Sel(1) === 0.U, ("b1".U ## io.IND(22, 0)), ("b1".U ## zero_vec))
  //CSA1.io.INB := Mux(io.Op_Sel(1) && io.Op_Sel(3) === 0.U, ("b1".U ## io.IND(22, 0)),
  //                Mux(io.Op_Sel(3) === 0.U, ("b1".U ## zero_vec), 0.U))
  CSA1_R := CSA1.io.OUTR
  CSA1_S := CSA1.io.OUTS

  //Far Path Logic
  val FarPathLogic = Module(new FarPathLogic)
  val Far_Sum = Reg(UInt(51.W))
  val Far_Carry = Reg(UInt(51.W))
  FarPathLogic.io.IN_AB_R := CSA0_R_reg
  FarPathLogic.io.IN_AB_S := CSA0_S_reg
  FarPathLogic.io.IN_CD_R := CSA1_R_reg
  FarPathLogic.io.IN_CD_S := CSA1_S_reg
  FarPathLogic.io.IN_EXP_COMP := Exp_Comp_reg
  FarPathLogic.io.IN_EXP_DIFF := Shift_Value_reg
  FarPathLogic.io.IN_OP_SEL := Op_Sel_reg

  //Close Path Logic
  val ClosePathLogic = Module(new ClosePathLogic)
  val Close_Sum = Reg(UInt(51.W))
  val Close_Carry = Reg(UInt(51.W))
  val Signif_Comp = Reg(UInt(1.W))
  val Norm_Shift = Reg(SInt(6.W))
  ClosePathLogic.io.IN_AB_R := CSA0_R_reg
  ClosePathLogic.io.IN_AB_S := CSA0_S_reg
  ClosePathLogic.io.IN_CD_R := CSA1_R_reg
  ClosePathLogic.io.IN_CD_S := CSA1_S_reg
  ClosePathLogic.io.IN_EXP_DIFF := Exp_Diff_reg
  ClosePathLogic.io.IN_OP_SEL := Op_Sel_reg

  val Sign = Reg(UInt(1.W))

  val Select_Sum = Wire(UInt(51.W))
  val Select_Carry = Wire(UInt(51.W))

  Select_Sum := Mux(Path_Sel_buf === 1.U, Close_Sum, Far_Sum)
  Select_Carry := Mux(Path_Sel_buf === 1.U, Close_Carry, Far_Carry)

  val Signif_Sum = Wire(UInt(53.W))
  val Far_adjust = Wire(UInt(1.W))
  Signif_Sum := ("b00".U ## Select_Sum) + ("b00".U ## Select_Carry(49, 0) ## zero_vec(0))
  Far_adjust := Mux(Path_Sel_buf === 0.U && Signif_Sum(52, 49) === 0.U, 1.U, 0.U)
  //Signif_Sum := ("b00".U ## Select_Sum) + ("b00".U ## Select_Carry)


  //Exponent Adjust Logic
  val ExponentAdjust = Module(new ExponentAdjust)
  val Exp_Result = Reg(UInt(8.W))
  val Exception = Reg(UInt(1.W))
  io.EXCEPTION := Exception

  ExponentAdjust.io.IN_EXP := Exp_Out_buf
  ExponentAdjust.io.IN_CARRY_OUT := Signif_Sum(52, 50)
  ExponentAdjust.io.IN_NORM_SHIFT := Norm_Shift
  ExponentAdjust.io.IN_OP_SEL := Op_Sel_buf
  ExponentAdjust.io.IN_PATH_SEL := Path_Sel_buf
  //Exp_Result := ExponentAdjust.io.OUT_EXP
  //io.EXCEPTION := ExponentAdjust.io.OUT_EXCEPTION

  val Round_Signif = Reg(UInt(23.W))
  //Round_Signif := Mux(Signif_Sum(52) === 1.U, Signif_Sum(51, 29),
  //                 Mux(Signif_Sum(51) === 1.U, Signif_Sum(50, 28), Signif_Sum(49, 27) ))

  io.OUT := Sign ## Exp_Result ## Round_Signif
  //val MulAB = Wire(UInt(48.W))
  //val MulCD = Wire(UInt(48.W))

  //MulAB := ("b1".U ## io.INA(22, 0)) * ("b1".U ## io.INB(22, 0))
  //MulCD := ("b1".U ## io.INC(22, 0)) * ("b1".U ## io.IND(22, 0))

  //printf("MulAB = %b\n", MulAB)
  //printf("MulCD = %b\n", MulCD)
  //printf("CSAAB = %b\n", Multi_AB)
  //printf("CSACD = %b\n", Multi_CD)
  /*
  printf("INA = %b\n", io.INA)
  printf("INB = %b\n", io.INB)
  printf("INC = %b\n", io.INC)
  printf("IND = %b\n", io.IND)
  printf("1st clock\n")
  printf("Exp_AB = %b\n", Exp_AB)
  printf("Exp_CD = %b\n", Exp_CD)
  printf("Multi_AB = %b\n", Multi_AB)
  printf("Multi_CD = %b\n", Multi_CD)
  printf("2nd clock\n")
  printf("Exp_AB_exc = %b\n", Exp_AB_exc)
  printf("Exp_CD_exc = %b\n", Exp_CD_exc)
  printf("Multi_AB_buf = %b\n", Multi_AB_buf1)
  printf("Multi_CD_buf = %b\n", Multi_CD_buf1)
  //3rd clock
  printf("3rd clock\n")
  printf("Exp_fifo(0) = %b\n", Exp_fifo(0))
  printf("Exp_diff = %b\n", Exp_diff)
  printf("Sign = %b\n", Sign)
  printf("Mant_large = %b\n", Mant_large)
  printf("Mant_small = %b\n", Mant_small)
  //4th clock
  printf("4th clock\n")
  printf("Exp_fifo(1) = %b\n", Exp_fifo(1))
  printf("Sign_shift_small = %b\n", Sign_shift_small)
  printf("Mant_large_shift = %b\n", Mant_large_shift)
  printf("Mant_small_shift = %b\n", Mant_small_shift)
  //5th clock
  printf("5th clock\n")
  printf("Exp_fifo(2) = %b\n", Exp_fifo(2))
  printf("Mant_result = %b\n", Mant_result)
  //6th clock
  printf("6th clock\n")
  printf("Exp_fifo(3) = %b\n", Exp_fifo(3))
  printf("Sign_fifo(0) = %b\n", Sign_fifo(0))
  printf("Mant_abs_fifo_0 = %b\n", Mant_abs_fifo_0)
  printf("LOD_result.io.IN = %b\n", LOD_result.io.IN)
  //7th clock
  printf("7th clock\n")
  printf("Exp_fifo(4) = %b\n", Exp_fifo(4))
  printf("Sign_fifo(1) = %b\n", Sign_fifo(1))
  printf("shift_value = %b\n", shift_value)
  //8th clock
  printf("8th clock\n")
  printf("Exp_fifo(5) = %b\n", Exp_fifo(5))
  printf("Sign_fifo(2) = %b\n", Sign_fifo(2))
  printf("Mant_out = %b\n", Mant_out)
  printf("Exp_shift = %b\n", Exp_shift)
  //9th clock
  printf("9th clock\n")
  printf("Exp_buf = %b\n", Exp_buf)
  printf("Sign_buf = %b\n", Sign_buf)
  printf("Mant_buf = %b\n", Mant_buf)
  ////10th clock
   */
  //printf("OUT = %b\n", result)
  /*
  printf("AB_exc = %b\n", AandB_exc)
  printf("CD_exc = %b\n", CandD_exc)
  printf("large = %b\n", large_Exp)
  printf("small = %b\n", small_Exp)
  printf("shift = %b\n", io.Result)

 */

  when(io.enable) {
    /*
        Exp_comp.io.Exp_A := io.INA(30, 23)
        Exp_comp.io.Exp_B := io.INB(30, 23)
        Exp_comp.io.Exp_C := io.INB(30, 23)
        Exp_comp.io.Exp_D := io.IND(30, 23)
        Exp_comp.io.Mant_over_AB := Multi_AB
        Exp_comp.io.Mant_over_CD := Multi_CD
        Exp_large := Exp_comp.io.Exp_Out
        Exp_shift := Exp_comp.io.Shift_Value
    */
    /*
    Exception_zero(0) := Mux(io.INA(30, 23) === 0.U, 1.B, 0.B)
    Exception_zero(1) := Mux(io.INB(30, 23) === 0.U, 1.B, 0.B)
    Exception_zero(2) := Mux(io.INC(30, 23) === 0.U, 1.B, 0.B)
    Exception_zero(3) := Mux(io.IND(30, 23) === 0.U, 1.B, 0.B)
    Exception_zero_fifo(0) := Exception_zero(0) || Exception_zero(1) || Exception_zero(2) || Exception_zero(3)
    Exception_zero_fifo(1) := Exception_zero_fifo(0)
    Exception_zero_fifo(2) := Exception_zero_fifo(1)
    Exception_zero_fifo(3) := Exception_zero_fifo(2)
    Exception_zero_fifo(4) := Exception_zero_fifo(3)
    Exception_zero_fifo(5) := Exception_zero_fifo(4)
    Exception_zero_fifo(6) := Exception_zero_fifo(5)
    Exception_zero_fifo(7) := Exception_zero_fifo(6)
    Exception_zero_fifo(8) := Exception_zero_fifo(7)
    //Exception_zero_fifo := Exception_zero_fifo(6) ## Exception_zero_fifo(5) ## Exception_zero_fifo(4) ## Exception_zero_fifo(3) ##
    //                       Exception_zero_fifo(2) ## Exception_zero_fifo(1) ## Exception_zero_fifo(0) ## (Exception_zero(1) || Exception_zero(0))

    Exception_inf(0) := Mux(io.INA(30, 23) === 0xff.U, 1.B, 0.B)
    Exception_inf(1) := Mux(io.INB(30, 23) === 0xff.U, 1.B, 0.B)
    Exception_inf(2) := Mux(io.INC(30, 23) === 0xff.U, 1.B, 0.B)
    Exception_inf(3) := Mux(io.IND(30, 23) === 0xff.U, 1.B, 0.B)
    //    Exception_zero_fifo := Exception_inf_fifo(6) ## Exception_inf_fifo(5) ## Exception_inf_fifo(4) ## Exception_inf_fifo(3) ##
    //    Exception_inf_fifo(2) ## Exception_inf_fifo(1) ## Exception_inf_fifo(0) ## (Exception_inf(1) || Exception_inf(0))
    Exception_inf_fifo(0) := Exception_inf(0) || Exception_inf(1) || Exception_inf(2) || Exception_inf(3)
    Exception_inf_fifo(1) := Exception_inf_fifo(0)
    Exception_inf_fifo(2) := Exception_inf_fifo(1)
    Exception_inf_fifo(3) := Exception_inf_fifo(2)
    Exception_inf_fifo(4) := Exception_inf_fifo(3)
    Exception_inf_fifo(5) := Exception_inf_fifo(4)
    Exception_inf_fifo(6) := Exception_inf_fifo(5)
    Exception_inf_fifo(7) := Exception_inf_fifo(6)
    Exception_inf_fifo(8) := Exception_inf_fifo(7)
         */
    ////1st stage ////
    //Op_Sel := Mux(io.Op_Sel === 1.U, !(Sign_AB ^ Sign_CD), Sign_AB ^ Sign_CD)
    Op_Sel_reg := Op_Sel
    Op_forSign_buf0 := Op_forSign
    Sign_AB_buf0 := Sign_AB
    Sign_CD_buf0 := Sign_CD

    Exp_Out_reg := Exp_Out
    Exp_Diff_reg := Exp_Diff
    Shift_Value_reg := Shift_Value
    Exp_Comp_reg := Exp_Comp
    Path_Sel_reg := Path_Sel

    CSA0_R_reg := CSA0_R
    CSA0_S_reg := CSA0_S
    CSA1_R_reg := CSA1_R
    CSA1_S_reg := CSA1_S
    ////2nd stage ////

    Far_Sum := FarPathLogic.io.OUT_R
    Far_Carry := FarPathLogic.io.OUT_S

    Close_Sum := ClosePathLogic.io.OUT_R
    Close_Carry := ClosePathLogic.io.OUT_S
    Signif_Comp := ClosePathLogic.io.SIGNIF_COMP
    Norm_Shift := ClosePathLogic.io.NORM_SHIFT

    Sign_AB_buf1 := Sign_AB_buf0
    Sign_CD_buf1 := Sign_CD_buf0
    Exp_Comp_buf := Exp_Comp_reg
    Op_Sel_buf := Op_Sel_reg
    Op_forSign_buf1 := Op_forSign_buf0
    Exp_Out_buf := Exp_Out_reg
    Path_Sel_buf := Path_Sel_reg
    ////3rd stage ////
    Sign := Mux(Op_Sel_buf === 0.U, (Sign_AB_buf1 & Sign_CD_buf1) | (Sign_AB_buf1 & Exp_Comp_buf) | (Sign_AB_buf1 & Signif_Comp) | (Sign_CD_buf1 & !Exp_Comp_buf & !Signif_Comp),
      (Sign_AB_buf1 & !Sign_CD_buf1) | (!Sign_AB_buf1 & Exp_Comp_buf) | (!Sign_AB_buf1 & Signif_Comp) | (Sign_CD_buf1 & !Exp_Comp_buf & !Signif_Comp))

    //Exp_Result := Mux(Signif_Sum(52) === 1.U, ExponentAdjust.io.OUT_EXP + 2.U, Mux(Signif_Sum(51) === 1.U, ExponentAdjust.io.OUT_EXP + 1.U, ExponentAdjust.io.OUT_EXP))
    Exp_Result := ExponentAdjust.io.OUT_EXP - Far_adjust
    Exception := ExponentAdjust.io.OUT_EXCEPTION
    Round_Signif := Mux(Signif_Sum(52) === 1.U, Signif_Sum(51, 29),
      Mux(Signif_Sum(51) === 1.U, Signif_Sum(50, 28),
        Mux(Signif_Sum(50) === 1.U, Signif_Sum(49, 27),
          Mux(Signif_Sum(49) === 1.U, Signif_Sum(48, 26), Signif_Sum(47, 25) ))))
  }
}


