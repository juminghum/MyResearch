package LOD

import chisel3._
import chisel3.util._

class LOD extends Module {
  val io = IO(new Bundle {
    val IN = Input(UInt(25.W))
    val OUT = Output(UInt(25.W))
    //val value = Output(UInt(8.W))
  })

  val result = Wire(Vec(25, UInt(1.W)))
  val wire = Wire(Vec(23, UInt(1.W)))
  val result_wire = Wire(UInt(25.W))

  wire(22) := !io.IN(24) & !io.IN(23)
  wire(21) := wire(22) & !io.IN(22)
  wire(20) := wire(21) & !io.IN(21)
  wire(19) := wire(20) & !io.IN(20)
  wire(18) := wire(19) & !io.IN(19)
  wire(17) := wire(18) & !io.IN(18)
  wire(16) := wire(17) & !io.IN(17)
  wire(15) := wire(16) & !io.IN(16)
  wire(14) := wire(15) & !io.IN(15)
  wire(13) := wire(14) & !io.IN(14)
  wire(12) := wire(13) & !io.IN(13)
  wire(11) := wire(12) & !io.IN(12)
  wire(10) := wire(11) & !io.IN(11)
  wire(9) := wire(10) & !io.IN(10)
  wire(8) := wire(9) & !io.IN(9)
  wire(7) := wire(8) & !io.IN(8)
  wire(6) := wire(7) & !io.IN(7)
  wire(5) := wire(6) & !io.IN(6)
  wire(4) := wire(5) & !io.IN(5)
  wire(3) := wire(4) & !io.IN(4)
  wire(2) := wire(3) & !io.IN(3)
  wire(1) := wire(2) & !io.IN(2)
  wire(0) := wire(1) & !io.IN(1)

  result(24) := io.IN(24)
  result(23) := !io.IN(24) & io.IN(23)
  result(22) := wire(22) & io.IN(22)
  result(21) := wire(21) & io.IN(21)
  result(20) := wire(20) & io.IN(20)
  result(19) := wire(19) & io.IN(19)
  result(18) := wire(18) & io.IN(18)
  result(17) := wire(17) & io.IN(17)
  result(16) := wire(16) & io.IN(16)
  result(15) := wire(15) & io.IN(15)
  result(14) := wire(14) & io.IN(14)
  result(13) := wire(13) & io.IN(13)
  result(12) := wire(12) & io.IN(12)
  result(11) := wire(11) & io.IN(11)
  result(10) := wire(10) & io.IN(10)
  result(9) := wire(9) & io.IN(9)
  result(8) := wire(8) & io.IN(8)
  result(7) := wire(7) & io.IN(7)
  result(6) := wire(6) & io.IN(6)
  result(5) := wire(5) & io.IN(5)
  result(4) := wire(4) & io.IN(4)
  result(3) := wire(3) & io.IN(3)
  result(2) := wire(2) & io.IN(2)
  result(1) := wire(1) & io.IN(1)
  result(0) := wire(0) & io.IN(0)

  result_wire := result(24) ## result(23) ## result(22) ## result(21) ## result(20) ## result(19) ## result(18) ## result(17) ## result(16) ## result(15) ##
    result(14) ## result(13) ## result(12) ## result(11) ## result(10) ## result(9) ## result(8) ## result(7) ## result(6) ## result(5) ##
    result(4) ## result(3) ## result(2) ## result(1) ## result(0)

  //val value_reg = Reg(UInt(8.W))
  //io.value := value_reg
/*
  //switch (io.OUT) {
  switch (result_wire) {
    is ("b1000000000000000000000000".U) { value_reg := 0.U}
    is ("b0100000000000000000000000".U) { value_reg := 1.U}
    is ("b0010000000000000000000000".U) { value_reg := 2.U}
    is ("b0001000000000000000000000".U) { value_reg := 3.U}
    is ("b0000100000000000000000000".U) { value_reg := 4.U}
    is ("b0000010000000000000000000".U) { value_reg := 5.U}
    is ("b0000001000000000000000000".U) { value_reg := 6.U}
    is ("b0000000100000000000000000".U) { value_reg := 7.U}
    is ("b0000000010000000000000000".U) { value_reg := 8.U}
    is ("b0000000001000000000000000".U) { value_reg := 9.U}
    is ("b0000000000100000000000000".U) { value_reg := 10.U}
    is ("b0000000000010000000000000".U) { value_reg := 11.U}
    is ("b0000000000001000000000000".U) { value_reg := 12.U}
    is ("b0000000000000100000000000".U) { value_reg := 13.U}
    is ("b0000000000000010000000000".U) { value_reg := 14.U}
    is ("b0000000000000001000000000".U) { value_reg := 15.U}
    is ("b0000000000000000100000000".U) { value_reg := 16.U}
    is ("b0000000000000000010000000".U) { value_reg := 17.U}
    is ("b0000000000000000001000000".U) { value_reg := 18.U}
    is ("b0000000000000000000100000".U) { value_reg := 19.U}
    is ("b0000000000000000000010000".U) { value_reg := 20.U}
    is ("b0000000000000000000001000".U) { value_reg := 21.U}
    is ("b0000000000000000000000100".U) { value_reg := 22.U}
    is ("b0000000000000000000000010".U) { value_reg := 23.U}
    is ("b0000000000000000000000001".U) { value_reg := 24.U}
  }*/
}
