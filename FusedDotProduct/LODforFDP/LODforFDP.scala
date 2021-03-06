package LODforFDP

import chisel3._
import chisel3.util._

class LODforFDP extends Module {
  val io = IO(new Bundle {
    val IN = Input(UInt(49.W))
    val OUT = Output(UInt(49.W))
    //val value = Output(UInt(8.W))
  })

  val result = Wire(Vec(49, UInt(1.W)))
  val wire = Wire(Vec(47, UInt(1.W)))

  wire(46) := !io.IN(48) & !io.IN(47)
  wire(45) := wire(46) & !io.IN(46)
  wire(44) := wire(45) & !io.IN(45)
  wire(43) := wire(44) & !io.IN(44)
  wire(42) := wire(43) & !io.IN(43)
  wire(41) := wire(42) & !io.IN(42)
  wire(40) := wire(41) & !io.IN(41)
  wire(39) := wire(40) & !io.IN(40)
  wire(38) := wire(39) & !io.IN(39)
  wire(37) := wire(38) & !io.IN(38)
  wire(36) := wire(37) & !io.IN(37)
  wire(35) := wire(36) & !io.IN(36)
  wire(34) := wire(35) & !io.IN(35)
  wire(33) := wire(34) & !io.IN(34)
  wire(32) := wire(33) & !io.IN(33)
  wire(31) := wire(32) & !io.IN(32)
  wire(30) := wire(31) & !io.IN(31)
  wire(29) := wire(30) & !io.IN(30)
  wire(28) := wire(29) & !io.IN(29)
  wire(27) := wire(28) & !io.IN(28)
  wire(26) := wire(27) & !io.IN(27)
  wire(25) := wire(26) & !io.IN(26)
  wire(24) := wire(25) & !io.IN(25)
  wire(23) := wire(24) & !io.IN(24)
  wire(22) := wire(23) & !io.IN(23)
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

  result(48) := io.IN(48)
  result(47) := !io.IN(48) & io.IN(47)
  result(46) := wire(46) & io.IN(46)
  result(45) := wire(45) & io.IN(45)
  result(44) := wire(44) & io.IN(44)
  result(43) := wire(43) & io.IN(43)
  result(42) := wire(42) & io.IN(42)
  result(41) := wire(41) & io.IN(41)
  result(40) := wire(40) & io.IN(40)
  result(39) := wire(39) & io.IN(39)
  result(38) := wire(38) & io.IN(38)
  result(37) := wire(37) & io.IN(37)
  result(36) := wire(36) & io.IN(36)
  result(35) := wire(35) & io.IN(35)
  result(34) := wire(34) & io.IN(34)
  result(33) := wire(33) & io.IN(33)
  result(32) := wire(32) & io.IN(32)
  result(31) := wire(31) & io.IN(31)
  result(30) := wire(30) & io.IN(30)
  result(29) := wire(29) & io.IN(29)
  result(28) := wire(28) & io.IN(28)
  result(27) := wire(27) & io.IN(27)
  result(26) := wire(26) & io.IN(26)
  result(25) := wire(25) & io.IN(25)
  result(24) := wire(24) & io.IN(24)
  result(23) := wire(23) & io.IN(23)
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

        io.OUT := result(48) ## result(47) ## result(46) ## result(45) ## result(44) ## result(43) ## result(42) ## result(41) ## result(40) ##
          result(39) ## result(38) ## result(37) ## result(36) ## result(35) ## result(34) ## result(33) ## result(32) ## result(31) ## result(30) ##
          result(29) ## result(28) ## result(27) ## result(26) ## result(25) ## result(24) ## result(23) ## result(22) ## result(21) ## result(20) ##
          result(19) ## result(18) ## result(17) ## result(16) ## result(15) ## result(14) ## result(13) ## result(12) ## result(11) ## result(10) ##
          result(9) ## result(8) ## result(7) ## result(6) ## result(5) ## result(4) ## result(3) ## result(2) ## result(1) ## result(0)

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
