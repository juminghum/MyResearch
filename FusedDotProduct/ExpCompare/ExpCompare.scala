package ExpCompare

//import LOD._
import chisel3._
import chisel3.util._

//class FPAdder(LATENCY:Int = 6) extends Module{

class ExpCompare extends Module{
  val io = IO(new Bundle{
    val Exp_A = Input(UInt(8.W))
    val Exp_B = Input(UInt(8.W))
    val Exp_C = Input(UInt(8.W))
    val Exp_D = Input(UInt(8.W))
    //val Sign_AB = Input(UInt(1.W))
    //val Sign_CD = Input(UInt(1.W))
    val Op_sel = Input(UInt(1.W))
    val Shift_Value = Output(UInt(8.W))
    val Exp_Diff = Output(SInt(10.W))
    val Exp_Out = Output(UInt(8.W))
    val Exp_comp = Output(UInt(1.W)) //AB < CD -> '1' else '0'
    val Path_sel = Output(UInt(1.W)) //cloth path -> '0' far path -> '1'
  })

  val first_AandB = Wire(UInt(9.W))
  val first_CandD = Wire(UInt(9.W))

  val ExpOut = Wire(UInt(9.W))

  val ExpDiff = Wire(SInt(10.W))
  //io.Exp_Diff := ExpDiff
  val ExpDiff_inv = Wire(UInt(10.W))
/*
  val AandB_exc = Reg(UInt(8.W))
  val CandD_exc = Reg(UInt(8.W))

  val large_Exp = Reg(UInt(8.W))
  val small_Exp = Reg(UInt(8.W))

  val ExpOut = Reg(UInt(8.W))
  val ShiftOut = Reg(UInt(8.W))

  io.Exp_Out := ExpOut
  io.Shift_Value := ShiftOut
*/
    first_AandB := io.Exp_A + io.Exp_B
    first_CandD := io.Exp_C + io.Exp_D

  ExpDiff := first_AandB.asSInt() - first_CandD.asSInt()
  io.Exp_Diff := ExpDiff
  io.Exp_comp := ExpDiff(9).asUInt()
  //io.Exp_Out := Mux(first_AandB > first_CandD, first_AandB - 127.U, first_CandD -127.U)
  ExpOut := Mux(first_AandB > first_CandD, first_AandB - 127.U, first_CandD -127.U)
  io.Exp_Out := ExpOut(7, 0)
  ExpDiff_inv := Mux(ExpDiff(9) === 1.B, (~ExpDiff).asUInt() + 1.U, ExpDiff.asUInt())
  io.Shift_Value := ExpDiff_inv(7, 0)
  val Path_sel = Wire(UInt(1.W))
  Path_sel := io.Op_sel & (ExpDiff_inv < 3.U) //| (ExpDiff < 0x3fd.S)
  io.Path_sel := Path_sel
    //AandB_exc := Mux(first_AandB(8) =/= 1.B | first_AandB =/= 0.U, Mux(first_AandB(8) =/= 1.B, first_AandB(7, 0).asUInt() + io.Mant_over_AB, 0xff.U), 0.U)
    //CandD_exc := Mux(first_CandD(8) =/= 1.B | first_CandD =/= 0.U, Mux(first_CandD(8) =/= 1.B, first_CandD(7, 0).asUInt() + io.Mant_over_CD, 0xff.U), 0.U)

  /*
        large_Exp := Mux(AandB_exc >= CandD_exc, AandB_exc, CandD_exc)
        small_Exp := Mux(AandB_exc < CandD_exc, AandB_exc, CandD_exc)

        ExpOut := large_Exp
        ShiftOut := large_Exp - small_Exp
    */

    //ExpOut := Mux(AandB_exc >= CandD_exc, AandB_exc, CandD_exc)
    //ShiftOut := Mux(AandB_exc >= CandD_exc, AandB_exc - CandD_exc, CandD_exc - AandB_exc)
}


/*
class ExpCompare extends Module{
  val io = IO(new Bundle{
    val Exp_A = Input(UInt(8.W))
    val Exp_B = Input(UInt(8.W))
    val Exp_C = Input(UInt(8.W))
    val Exp_D = Input(UInt(8.W))
    val Mant_over_AB = Input(UInt(1.W))
    val Mant_over_CD = Input(UInt(1.W))
    val Shift_Value = Output(UInt(8.W))
    val Exp_Out = Output(UInt(8.W))
  })

  val first_AandB = Reg(SInt(10.W))
  val first_CandD = Reg(SInt(10.W))

  val AandB_exc = Reg(UInt(8.W))
  val CandD_exc = Reg(UInt(8.W))

  val large_Exp = Reg(UInt(8.W))
  val small_Exp = Reg(UInt(8.W))

  val ExpOut = Reg(UInt(8.W))
  val ShiftOut = Reg(UInt(8.W))

  io.Exp_Out := ExpOut
  io.Shift_Value := ShiftOut

    first_AandB := ("b00".U ## io.Exp_A).asSInt() + ("b00".U ## io.Exp_B).asSInt() - 127.S
    first_CandD := ("b00".U ## io.Exp_C).asSInt() + ("b00".U ## io.Exp_D).asSInt() - 127.S

    AandB_exc := Mux(first_AandB(9) =/= 1.B | first_AandB =/= 0.S, Mux(first_AandB(8) =/= 1.B, first_AandB(7, 0).asUInt() + io.Mant_over_AB, 0xff.U), 0.U)
    CandD_exc := Mux(first_CandD(9) =/= 1.B | first_CandD =/= 0.S, Mux(first_CandD(8) =/= 1.B, first_CandD(7, 0).asUInt() + io.Mant_over_CD, 0xff.U), 0.U)
/*
    large_Exp := Mux(AandB_exc >= CandD_exc, AandB_exc, CandD_exc)
    small_Exp := Mux(AandB_exc < CandD_exc, AandB_exc, CandD_exc)

    ExpOut := large_Exp
    ShiftOut := large_Exp - small_Exp
*/
    ExpOut := Mux(AandB_exc >= CandD_exc, AandB_exc, CandD_exc)
    ShiftOut := Mux(AandB_exc >= CandD_exc, AandB_exc - CandD_exc, CandD_exc - AandB_exc)

}
*/