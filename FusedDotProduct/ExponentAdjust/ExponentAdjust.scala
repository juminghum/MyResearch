package ExponentAdjust

import chisel3._

class ExponentAdjust  extends Module{
  val io = IO(new Bundle {
    val IN_CARRY_OUT = Input(UInt(3.W))
    val IN_EXP = Input(UInt(8.W))
    val IN_NORM_SHIFT = Input(SInt(6.W))
    val IN_OP_SEL = Input(UInt(6.W))
    val IN_PATH_SEL = Input(UInt(1.W))
    //val IN_Correcting = Input(1.W)
    val OUT_EXP = Output(UInt(8.W))
    val OUT_EXCEPTION = Output(UInt(1.W))
  })

  val Exp_Carry_Add = Wire(SInt(9.W))
  val Exp_Carry_Sub = Wire(SInt(9.W))
  val Exp_Carry_Norm_Shift = Wire(SInt(9.W))
  val Select_by_Op_Sel = Wire(SInt(9.W))
  val Select_by_Path_Sel = Wire(SInt(9.W))

  //Exp_Carry_Add := io.IN_EXP.asSInt() + io.IN_CARRY_OUT.asSInt()
  //Exp_Carry_Sub := io.IN_EXP.asSInt() - io.IN_CARRY_OUT.asSInt()
  Exp_Carry_Add := (io.IN_EXP + io.IN_CARRY_OUT).asSInt()
  Exp_Carry_Sub := (io.IN_EXP - io.IN_CARRY_OUT).asSInt()
  //Exp_Carry_Norm_Shift := io.IN_EXP.asSInt() - io.IN_NORM_SHIFT.asSInt()
  Exp_Carry_Norm_Shift := io.IN_EXP.asSInt() - io.IN_NORM_SHIFT

  Select_by_Op_Sel := Mux(io.IN_OP_SEL === 0.U, Exp_Carry_Add, Exp_Carry_Sub)
  Select_by_Path_Sel := Mux(io.IN_PATH_SEL === 0.U, Select_by_Op_Sel, Exp_Carry_Norm_Shift)


  io.OUT_EXP := Select_by_Path_Sel(7, 0).asUInt()
  io.OUT_EXCEPTION := Exp_Carry_Add(8) | Exp_Carry_Sub(8) | Exp_Carry_Norm_Shift(8)
}
