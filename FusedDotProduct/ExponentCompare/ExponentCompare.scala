package ExponentCompare

import chisel3._
import chisel3.util._

class ExponentCompare extends Module {
  val io = IO(new Bundle {
    //val enable = Input(Bool())
    val A = Input(UInt(5.W))
    val B = Input(UInt(5.W))
    val C = Input(UInt(5.W))
    val D = Input(UInt(5.W))
    val E = Input(UInt(5.W))
    val F = Input(UInt(5.W))
    val G = Input(UInt(5.W))
    val H = Input(UInt(5.W))
    val ExpMax = Output(UInt(5.W))
    val shift_AB = Output(UInt(5.W))
    val shift_CD = Output(UInt(5.W))
    val shift_EF = Output(UInt(5.W))
    val shift_GH = Output(UInt(5.W))
  })

  val exp_AB = Wire(UInt(5.W))
  val exp_CD = Wire(UInt(5.W))
  val exp_EF = Wire(UInt(5.W))
  val exp_GH = Wire(UInt(5.W))

  val comp_AB_CD = Wire(Bool())
  val comp_CD_EF = Wire(Bool())
  val comp_EF_GH = Wire(Bool())
  val comp_GH_AB = Wire(Bool())
  val comp_AB_EF = Wire(Bool())
  val comp_CD_GH = Wire(Bool())

  val diff_AB_CD = Wire(UInt(5.W))
  val diff_CD_EF = Wire(UInt(5.W))
  val diff_EF_GH = Wire(UInt(5.W))
  val diff_GH_AB = Wire(UInt(5.W))
  val diff_AB_EF = Wire(UInt(5.W))
  val diff_CD_GH = Wire(UInt(5.W))

  exp_AB := io.A + io.B
  exp_CD := io.C + io.D
  exp_EF := io.E + io.F
  exp_GH := io.G + io.H

  comp_AB_CD := exp_AB >= exp_CD
  comp_CD_EF := exp_CD >= exp_EF
  comp_EF_GH := exp_EF >= exp_GH
  comp_GH_AB := exp_GH >= exp_AB
  comp_AB_EF := exp_AB >= exp_EF
  comp_CD_GH := exp_CD >= exp_GH

  diff_AB_CD := exp_AB - exp_CD
  diff_CD_EF := exp_CD - exp_EF
  diff_EF_GH := exp_EF - exp_GH
  diff_GH_AB := exp_GH - exp_AB
  diff_AB_EF := exp_AB - exp_EF
  diff_CD_GH := exp_CD - exp_GH

  printf("ExpMax = %b\n", io.ExpMax)
  printf("shift_AB = %b\n", io.shift_AB)
  printf("shift_CD = %b\n", io.shift_CD)
  printf("shift_EF = %b\n", io.shift_EF)
  printf("shift_GH = %b\n", io.shift_GH)

  when(!(comp_AB_CD | comp_CD_EF | comp_EF_GH) & comp_GH_AB){
    io.ExpMax := exp_GH
    io.shift_AB := diff_GH_AB
    io.shift_CD := diff_CD_GH
    io.shift_EF := diff_EF_GH
    io.shift_GH := 0.U
  }.elsewhen(!(comp_AB_CD | comp_CD_EF | comp_GH_AB) & comp_EF_GH){
    io.ExpMax := exp_EF
    io.shift_AB := diff_AB_EF
    io.shift_CD := diff_CD_EF
    io.shift_EF := 0.U
    io.shift_GH := diff_EF_GH
  }.elsewhen(!(comp_AB_CD | comp_CD_EF) & (comp_EF_GH & comp_GH_AB)){
    io.ExpMax := exp_EF
    io.shift_AB := diff_AB_EF
    io.shift_CD := diff_CD_EF
    io.shift_EF := 0.U
    io.shift_GH := diff_EF_GH
  }.elsewhen(!(comp_AB_CD | comp_GH_AB | comp_EF_GH) & comp_CD_EF){
    io.ExpMax := exp_CD
    io.shift_AB := diff_AB_CD
    io.shift_CD := 0.U
    io.shift_EF := diff_CD_EF
    io.shift_GH := diff_CD_GH
  }.elsewhen(!(comp_AB_CD | comp_EF_GH | comp_CD_GH) & (comp_CD_EF & comp_GH_AB)){
    io.ExpMax := exp_GH
    io.shift_AB := diff_GH_AB
    io.shift_CD := diff_CD_GH
    io.shift_EF := diff_EF_GH
    io.shift_GH := 0.U
  }.elsewhen(!(comp_AB_CD | comp_EF_GH) & (comp_CD_EF & comp_GH_AB & comp_CD_GH)){
    io.ExpMax := exp_CD
    io.shift_AB := diff_AB_CD
    io.shift_CD := 0.U
    io.shift_EF := diff_CD_EF
    io.shift_GH := diff_CD_GH
  }.elsewhen(!(comp_AB_CD | comp_GH_AB) & (comp_CD_EF & comp_EF_GH)){
    io.ExpMax := exp_CD
    io.shift_AB := diff_AB_CD
    io.shift_CD := 0.U
    io.shift_EF := diff_CD_EF
    io.shift_GH := diff_CD_GH
  }.elsewhen(!comp_AB_CD & (comp_CD_EF & comp_EF_GH & comp_GH_AB)){
    io.ExpMax := exp_CD
    io.shift_AB := diff_AB_CD
    io.shift_CD := 0.U
    io.shift_EF := diff_CD_EF
    io.shift_GH := diff_CD_GH
  }.elsewhen(comp_AB_CD & !(comp_CD_EF | comp_EF_GH | comp_GH_AB)){
    io.ExpMax := exp_AB
    io.shift_AB := 0.U
    io.shift_CD := diff_AB_CD
    io.shift_EF := diff_AB_EF
    io.shift_GH := diff_GH_AB
  }.elsewhen((comp_AB_CD & comp_GH_AB) & !(comp_CD_EF | comp_EF_GH)){
    io.ExpMax := exp_GH
    io.shift_AB := diff_GH_AB
    io.shift_CD := diff_CD_GH
    io.shift_EF := diff_EF_GH
    io.shift_GH := 0.U
  }.elsewhen((comp_AB_CD & comp_EF_GH) & !(comp_CD_EF | comp_GH_AB | comp_AB_EF)){
    io.ExpMax := exp_EF
    io.shift_AB := diff_AB_EF
    io.shift_CD := diff_CD_EF
    io.shift_EF := 0.U
    io.shift_GH := diff_EF_GH
  }.elsewhen((comp_AB_CD & comp_EF_GH & comp_AB_EF) & !(comp_CD_EF | comp_GH_AB)){
    io.ExpMax := exp_AB
    io.shift_AB := 0.U
    io.shift_CD := diff_AB_CD
    io.shift_EF := diff_AB_EF
    io.shift_GH := diff_GH_AB
  }.elsewhen((comp_AB_CD & comp_EF_GH & comp_GH_AB) & !comp_CD_EF){
    io.ExpMax := exp_EF
    io.shift_AB := diff_AB_EF
    io.shift_CD := diff_CD_EF
    io.shift_EF := 0.U
    io.shift_GH := diff_EF_GH
  }.elsewhen((comp_AB_CD & comp_CD_EF) & !(comp_EF_GH | comp_GH_AB)){
    io.ExpMax := exp_AB
    io.shift_AB := 0.U
    io.shift_CD := diff_AB_CD
    io.shift_EF := diff_AB_EF
    io.shift_GH := diff_GH_AB
  }.elsewhen((comp_AB_CD & comp_CD_EF & comp_GH_AB) & !comp_EF_GH){
    io.ExpMax := exp_GH
    io.shift_AB := diff_GH_AB
    io.shift_CD := diff_CD_GH
    io.shift_EF := diff_EF_GH
    io.shift_GH := 0.U
  }.elsewhen((comp_AB_CD & comp_CD_EF & comp_EF_GH) & !comp_GH_AB){
    io.ExpMax := exp_AB
    io.shift_AB := 0.U
    io.shift_CD := diff_AB_CD
    io.shift_EF := diff_AB_EF
    io.shift_GH := diff_GH_AB
  }.elsewhen(comp_AB_CD & comp_CD_EF & comp_EF_GH & comp_GH_AB){
    io.ExpMax := exp_AB
    io.shift_AB := 0.U
    io.shift_CD := 0.U
    io.shift_EF := 0.U
    io.shift_GH := 0.U
  }.otherwise{
    io.ExpMax := "h1f".U
    io.shift_AB := 0.U
    io.shift_CD := 0.U
    io.shift_EF := 0.U
    io.shift_GH := 0.U
  }
}