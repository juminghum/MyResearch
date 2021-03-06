package CarrySaveAdder

import chisel3._
import CPPadd._
import CSA._
import FourTwoCompressor._
import chisel3.util._

class CarrySaveAdder_v2 extends Module {
  val io = IO(new Bundle {
    //val enable = Input(Bool())
    val INA = Input(UInt(24.W))
    val INB = Input(UInt(24.W))
    //val OUT = Output(UInt(48.W))
    val OUTR = Output(UInt(48.W))
    val OUTS = Output(UInt(39.W))
  })

  val P0 = Wire(UInt(1.W))
  val P1 = Wire(UInt(3.W))
  val P2 = Wire(UInt(4.W))
  val P3 = Wire(UInt(5.W))
  val P4 = Wire(UInt(6.W))
  val P5 = Wire(UInt(7.W))
  val P6 = Wire(UInt(8.W))
  val P7 = Wire(UInt(9.W))
  val zero_vector = 0.U(10.W)

  val P8 = Wire(UInt(10.W))
  val P9 = Wire(UInt(11.W))
  val P10 = Wire(UInt(12.W))

  val P11 = Wire(UInt(13.W))
  val P12 = Wire(UInt(14.W))

  val P13 = Wire(UInt(15.W))
  val P14 = Wire(UInt(16.W))

  val P15 = Wire(UInt(17.W))
  val P16 = Wire(UInt(18.W))
  val P17 = Wire(UInt(19.W))
  val P18 = Wire(UInt(20.W))
  val P19 = Wire(UInt(21.W))
  val P20 = Wire(UInt(22.W))
  val P21 = Wire(UInt(23.W))
  val P22 = Wire(UInt(24.W))
  val P23 = Wire(UInt(25.W))

  //io.OUT := P22 ## P22

  /*
    printf("P0 = %b\n", P0)
    printf("P1 = %b\n", P1)
    printf("P2 = %b\n", P2)
    printf("P3 = %b\n", P3)
    printf("P4 = %b\n", P4)
    printf("P5 = %b\n", P5)
    printf("P6 = %b\n", P6)
    printf("P7 = %b\n", P7)
    printf("P8 = %b\n", P8)
    printf("P9 = %b\n", P9)
    printf("P10 = %b\n", P10)
    printf("P11 = %b\n", P11)
    printf("P12 = %b\n", P12)
    printf("P13 = %b\n", P13)
    printf("P14 = %b\n", P14)
    printf("P15 = %b\n", P15)
    printf("P16 = %b\n", P16)
    printf("P17 = %b\n", P17)
    printf("P18 = %b\n", P18)
    printf("P19 = %b\n", P19)
    printf("P20 = %b\n", P20)
    printf("P21 = %b\n", P21)
    printf("P22 = %b\n", P22)
    printf("P23 = %b\n", P23)


   */

  val S = Wire(UInt(25.W))
  S := ("b0".U ## io.INA) + ("b0".U ## io.INB)

  P0 := io.INA(0) & io.INB(0)

  val P1_9 = Module(new P1to9)
  val P10_13 = Module(new P10to13)
  val P14_16 = Module(new P14to16)
  val P17_18 = Module(new P17to18)
  val P19_20 = Module(new P19to20)
  val P21_22 = Module(new P21to22)
  val P23_23 = Module(new P23to23)

  P1_9.io.INA := io.INA(9, 0)
  P1_9.io.INB := io.INB(9, 0)
  P1_9.io.INS := S(9, 0)

  P10_13.io.INA := io.INA(13, 0)
  P10_13.io.INB := io.INB(13, 0)
  P10_13.io.INS := S(13, 0)

  P14_16.io.INA := io.INA(16, 0)
  P14_16.io.INB := io.INB(16, 0)
  P14_16.io.INS := S(16, 0)

  P17_18.io.INA := io.INA(18, 0)
  P17_18.io.INB := io.INB(18, 0)
  P17_18.io.INS := S(18, 0)

  P19_20.io.INA := io.INA(20, 0)
  P19_20.io.INB := io.INB(20, 0)
  P19_20.io.INS := S(20, 0)

  P21_22.io.INA := io.INA(22, 0)
  P21_22.io.INB := io.INB(22, 0)
  P21_22.io.INS := S(22, 0)

  P23_23.io.INA := io.INA(23, 0)
  P23_23.io.INB := io.INB(23, 0)
  P23_23.io.INS := S(24, 0)

  P1 := P1_9.io.P1
  P2 := P1_9.io.P2
  P3 := P1_9.io.P3
  P4 := P1_9.io.P4
  P5 := P1_9.io.P5
  P6 := P1_9.io.P6
  P7 := P1_9.io.P7
  P8 := P1_9.io.P8
  P9 := P1_9.io.P9

  P10 := P10_13.io.P10
  P11 := P10_13.io.P11
  P12 := P10_13.io.P12
  P13 := P10_13.io.P13

  P14 := P14_16.io.P14
  P15 := P14_16.io.P15
  P16 := P14_16.io.P16

  P17 := P17_18.io.P17
  P18 := P17_18.io.P18

  P19 := P19_20.io.P19
  P20 := P19_20.io.P20

  P21 := P21_22.io.P21
  P22 := P21_22.io.P22

  P23 := P23_23.io.P23
  /*
    val CSA0 = Module(new CSA)
    val CSA1 = Module(new CSA)
    val CSA2 = Module(new CSA)
    val CSA3 = Module(new CSA)
    val CSA4 = Module(new CSA)
    val CSA5 = Module(new CSA)
    val CSA6 = Module(new CSA)
    val CSA7 = Module(new CSA)
    val CSA8 = Module(new CSA)
    val CSA9 = Module(new CSA)
    val CSA10 = Module(new CSA)

    val CSA11 = Module(new CSA)
    val CSA12 = Module(new CSA)
    val CSA13 = Module(new CSA)
    val CSA14 = Module(new CSA)
    val CSA15 = Module(new CSA)
    val CSA16 = Module(new CSA)
    val CSA17 = Module(new CSA)
    val CSA18 = Module(new CSA)
    val CSA19 = Module(new CSA)
    val CSA20 = Module(new CSA)
    val CSA21 = Module(new CSA)

   */
  /*
   val FTC0 = Module(new FourTwoCompressor)
   val FTC1 = Module(new FourTwoCompressor)
   val FTC2 = Module(new FourTwoCompressor)
   val FTC3 = Module(new FourTwoCompressor)
   val FTC4 = Module(new FourTwoCompressor)
   val FTC5 = Module(new FourTwoCompressor)
   val FTC6 = Module(new FourTwoCompressor)
   val FTC7 = Module(new FourTwoCompressor)
   val FTC8= Module(new FourTwoCompressor)
   val FTC9 = Module(new FourTwoCompressor)
   val FTC10 = Module(new FourTwoCompressor)
   val FTC11 = Module(new FourTwoCompressor)
 */
  /*
    printf("CSA0.io.A = %b\n", CSA0.io.A)
    printf("CSA0.io.B = %b\n", CSA0.io.B)
    printf("CSA0.io.C = %b\n", CSA0.io.C)
    printf("R0 = %b\n", R0)
    printf("S0 = %b\n", S0)
    printf("CSA1.io.A = %b\n", CSA1.io.A)
    printf("CSA1.io.B = %b\n", CSA1.io.B)
    printf("CSA1.io.C = %b\n", CSA1.io.C)
    printf("R1 = %b\n", R1)
    printf("S1 = %b\n", S1)
    printf("CSA2.io.A = %b\n", CSA2.io.A)
    printf("CSA2.io.B = %b\n", CSA2.io.B)
    printf("CSA2.io.C = %b\n", CSA2.io.C)
    printf("R2 = %b\n", R2)
    printf("S2 = %b\n", S2)
    printf("CSA3.io.A = %b\n", CSA3.io.A)
    printf("CSA3.io.B = %b\n", CSA3.io.B)
    printf("CSA3.io.C = %b\n", CSA3.io.C)
    printf("R3 = %b\n", R3)
    printf("S3 = %b\n", S3)

    printf("R4 = %b\n", R4)
    printf("S4 = %b\n", S4)
    printf("R5 = %b\n", R5)
    printf("S5 = %b\n", S5)

   */
  // printf("test = %b\n", test)

  //val Rvec = Wire(Vec(22, UInt(48.W)))
  //val Svec = Wire(Vec(22, UInt(48.W)))
  // val Pvec = Wire(Vec(12, UInt(48.W)))
  /*
    CSA0.io.A := (zero_vector(46,0) ## P0)
    CSA0.io.B := (zero_vector(43,0)  ## P1 ## zero_vector(0) )
    CSA0.io.C := (zero_vector(41,0) ## P2 ## zero_vector(1,0) )
    Rvec(0) := CSA0.io.R
    Svec(0) := (CSA0.io.S(46,0) ## zero_vector(46))

    CSA1.io.A := (zero_vector(39,0) ## P3 ## zero_vector(2,0) )
    CSA1.io.B := (zero_vector(37,0)  ## P4 ## zero_vector(3,0) )
    CSA1.io.C := (zero_vector(35,0) ## P5 ## zero_vector(4,0) )
    Rvec(1) := CSA1.io.R
    Svec(1) := (CSA1.io.S(46,0) ## zero_vector(45))

    CSA2.io.A := (zero_vector(33,0) ## P6 ## zero_vector(5,0) )
    CSA2.io.B := (zero_vector(31,0) ## P7 ## zero_vector(6,0) )
    CSA2.io.C := (zero_vector(29,0) ## P8 ## zero_vector(7,0) )
    Rvec(2) := CSA2.io.R
    Svec(2) := (CSA2.io.S(46,0) ## zero_vector(44))

    CSA3.io.A := (zero_vector(27,0) ## P9 ## zero_vector(8,0) )
    CSA3.io.B := (zero_vector(25,0)  ## P10 ## zero_vector(9,0) )
    CSA3.io.C := (zero_vector(23,0) ## P11 ## zero_vector(10,0) )
    Rvec(3) := CSA3.io.R
    Svec(3) := (CSA3.io.S(46,0) ## zero_vector(43))

    CSA4.io.A := (zero_vector(21,0) ## P12 ## zero_vector(11,0) )
    CSA4.io.B := (zero_vector(19,0) ## P13 ## zero_vector(12,0) )
    CSA4.io.C := (zero_vector(17,0) ## P14 ## zero_vector(13,0) )
    Rvec(4) := CSA4.io.R
    Svec(4) := (CSA4.io.S(46,0) ## zero_vector(42))

    CSA5.io.A := (zero_vector(15,0) ## P15 ## zero_vector(14,0) )
    CSA5.io.B := (zero_vector(13,0) ## P16 ## zero_vector(15,0) )
    CSA5.io.C :=(zero_vector(11,0) ## P17 ## zero_vector(16,0) )
    Rvec(5) := CSA5.io.R
    Svec(5) := (CSA5.io.S(46,0) ## zero_vector(41))

    CSA6.io.A := Rvec(0)
    CSA6.io.B := Svec(0)
    CSA6.io.C := Rvec(1)
    Rvec(6) := CSA6.io.R
    Svec(6) := (CSA6.io.S(46,0) ## zero_vector(40))

    CSA7.io.A := Svec(1)
    CSA7.io.B := Rvec(2)
    CSA7.io.C := Svec(2)
    Rvec(7) := CSA7.io.R
    Svec(7) := (CSA7.io.S(46,0) ## zero_vector(39))

    CSA8.io.A := Rvec(3)
    CSA8.io.B := Svec(3)
    CSA8.io.C := Rvec(4)
    Rvec(8) := CSA8.io.R
    Svec(8) := (CSA8.io.S(46,0) ## zero_vector(38))

    CSA9.io.A := Svec(4)
    CSA9.io.B := Rvec(5)
    CSA9.io.C := Svec(5)
    Rvec(9) := CSA9.io.R
    Svec(9) := (CSA9.io.S(46,0) ## zero_vector(37))

    CSA10.io.A := (zero_vector(9,0) ## P18 ## zero_vector(17,0) )
    CSA10.io.B := (zero_vector(7,0) ## P19 ## zero_vector(18,0) )
    CSA10.io.C :=(zero_vector(5,0) ## P20 ## zero_vector(19,0) )
    Rvec(10) := CSA10.io.R
    Svec(10) := (CSA10.io.S(46,0) ## zero_vector(36))

    CSA11.io.A := (zero_vector(3,0) ## P21 ## zero_vector(20,0) )
    CSA11.io.B := (zero_vector(1,0) ## P22 ## zero_vector(21,0) )
    CSA11.io.C := (P23 ## zero_vector(22,0) )
    Rvec(11) := CSA11.io.R
    Svec(11) := (CSA11.io.S(46,0) ## zero_vector(35))

    CSA12.io.A := Rvec(6)
    CSA12.io.B := Svec(6)
    CSA12.io.C := Rvec(7)
    Rvec(12) := CSA12.io.R
    Svec(12) := (CSA12.io.S(46,0) ## zero_vector(34))

    CSA13.io.A := Svec(7)
    CSA13.io.B := Rvec(8)
    CSA13.io.C := Svec(8)
    Rvec(13) := CSA13.io.R
    Svec(13) := (CSA13.io.S(46,0) ## zero_vector(33))

    CSA14.io.A := Rvec(9)
    CSA14.io.B := Svec(9)
    CSA14.io.C := Rvec(10)
    Rvec(14) := CSA14.io.R
    Svec(14) := (CSA14.io.S(46,0) ## zero_vector(32))

    CSA15.io.A := Svec(10)
    CSA15.io.B := Rvec(11)
    CSA15.io.C := Svec(11)
    Rvec(15) := CSA15.io.R
    Svec(15) := (CSA15.io.S(46,0) ## zero_vector(31))

    CSA16.io.A := Rvec(12)
    CSA16.io.B := Svec(12)
    CSA16.io.C := Rvec(13)
    Rvec(16) := CSA16.io.R
    Svec(16) := (CSA16.io.S(46,0) ## zero_vector(30))

    CSA17.io.A := Svec(13)
    CSA17.io.B := Rvec(14)
    CSA17.io.C := Svec(14)
    Rvec(17) := CSA17.io.R
    Svec(17) := (CSA17.io.S(46,0) ## zero_vector(29))

    CSA18.io.A := Rvec(16)
    CSA18.io.B := Svec(16)
    CSA18.io.C := Rvec(17)
    Rvec(18) := CSA18.io.R
    Svec(18) := (CSA18.io.S(46,0) ## zero_vector(28))

    CSA19.io.A := Svec(17)
    CSA19.io.B := Rvec(15)
    CSA19.io.C := Svec(15)
    Rvec(19) := CSA19.io.R
    Svec(19) := (CSA19.io.S(46,0) ## zero_vector(27))

    CSA20.io.A := Rvec(18)
    CSA20.io.B := Svec(18)
    CSA20.io.C := Rvec(19)
    Rvec(20) := CSA20.io.R
    Svec(20) := (CSA20.io.S(46,0) ## zero_vector(26))

    CSA21.io.A := Rvec(20)
    CSA21.io.B := Svec(20)
    CSA21.io.C := Svec(19)
    Rvec(21) := CSA21.io.R
    Svec(21) := (CSA21.io.S(46,0) ## zero_vector(25))

   */
  //val RR = Wire(UInt(48.W))
  //val SS = Wire(UInt(39.W))
  //printf("S = %b\n", S)
  //printf("RR = %b\n", RR)
  //printf("SS = %b\n", SS)
  //printf("CSAOUT = %b\n", io.OUT)

  //val CPP = Module(new CPPadd)
  val CPP = Module(new FourTwoCompressorxx)

  CPP.io.P0 := P0
  CPP.io.P1 := P1
  CPP.io.P2 := P2
  CPP.io.P3 := P3
  CPP.io.P4 := P4
  CPP.io.P5 := P5
  CPP.io.P6 := P6
  CPP.io.P7 := P7
  CPP.io.P8 := P8
  CPP.io.P9 := P9
  CPP.io.P10 := P10
  CPP.io.P11 := P11
  CPP.io.P12 := P12
  CPP.io.P13 := P13
  CPP.io.P14 := P14
  CPP.io.P15 := P15
  CPP.io.P16 := P16
  CPP.io.P17 := P17
  CPP.io.P18 := P18
  CPP.io.P19 := P19
  CPP.io.P20 := P20
  CPP.io.P21 := P21
  CPP.io.P22 := P22
  CPP.io.P23 := P23
  io.OUTR := CPP.io.R
  io.OUTS := CPP.io.S
  //RR := CPP.io.R
  //SS := (CPP.io.S(37, 0) ## zero_vector(9, 0))

  //io.OUT := RR + SS

}
  /*
class P1to9 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(10.W))
    val INB = Input(UInt(10.W))
    val INS = Input(UInt(10.W))
    val P1 = Output(UInt(3.W))
    val P2 = Output(UInt(4.W))
    val P3 = Output(UInt(5.W))
    val P4 = Output(UInt(6.W))
    val P5 = Output(UInt(7.W))
    val P6 = Output(UInt(8.W))
    val P7 = Output(UInt(9.W))
    val P8 = Output(UInt(10.W))
    val P9 = Output(UInt(11.W))
  })
  io.P1 := (((io.INA(1) & io.INB(1)) & io.INS(1)) ##
    ((io.INA(1) & io.INB(1)) & !io.INS(1)) ##
    (((!io.INA(1) & io.INB(1)) & io.INA(0)) | ((io.INA(1) & !io.INB(1)) & io.INB(0)) | ((io.INA(1) & io.INB(1)) & io.INS(0))))

  io.P2 := (((io.INA(2) & io.INB(2)) & io.INS(2)) ##
    ((io.INA(2) & io.INB(2)) & !io.INS(2)) ##
    (((!io.INA(2) & io.INB(2)) & io.INA(1)) | ((io.INA(2) & !io.INB(2)) & io.INB(1)) | ((io.INA(2) & io.INB(2)) & io.INS(1))) ##
    (((!io.INA(2) & io.INB(2)) & io.INA(0)) | ((io.INA(2) & !io.INB(2)) & io.INB(0)) | ((io.INA(2) & io.INB(2)) & io.INS(0))))
  io.P3 := (((io.INA(3) & io.INB(3)) & io.INS(3)) ##
    ((io.INA(3) & io.INB(3)) & !io.INS(3)) ##
    (((!io.INA(3) & io.INB(3)) & io.INA(2)) | ((io.INA(3) & !io.INB(3)) & io.INB(2)) | ((io.INA(3) & io.INB(3)) & io.INS(2))) ##
    (((!io.INA(3) & io.INB(3)) & io.INA(1)) | ((io.INA(3) & !io.INB(3)) & io.INB(1)) | ((io.INA(3) & io.INB(3)) & io.INS(1))) ##
    (((!io.INA(3) & io.INB(3)) & io.INA(0)) | ((io.INA(3) & !io.INB(3)) & io.INB(0)) | ((io.INA(3) & io.INB(3)) & io.INS(0))))
  io.P4 := (((io.INA(4) & io.INB(4)) & io.INS(4)) ##
    ((io.INA(4) & io.INB(4)) & !io.INS(4)) ##
    (((!io.INA(4) & io.INB(4)) & io.INA(3)) | ((io.INA(4) & !io.INB(4)) & io.INB(3)) | ((io.INA(4) & io.INB(4)) & io.INS(3))) ##
    (((!io.INA(4) & io.INB(4)) & io.INA(2)) | ((io.INA(4) & !io.INB(4)) & io.INB(2)) | ((io.INA(4) & io.INB(4)) & io.INS(2))) ##
    (((!io.INA(4) & io.INB(4)) & io.INA(1)) | ((io.INA(4) & !io.INB(4)) & io.INB(1)) | ((io.INA(4) & io.INB(4)) & io.INS(1))) ##
    (((!io.INA(4) & io.INB(4)) & io.INA(0)) | ((io.INA(4) & !io.INB(4)) & io.INB(0)) | ((io.INA(4) & io.INB(4)) & io.INS(0))))
  io.P5 := (((io.INA(5) & io.INB(5)) & io.INS(5)) ##
    ((io.INA(5) & io.INB(5)) & !io.INS(5)) ##
    (((!io.INA(5) & io.INB(5)) & io.INA(4)) | ((io.INA(5) & !io.INB(5)) & io.INB(4)) | ((io.INA(5) & io.INB(5)) & io.INS(4))) ##
    (((!io.INA(5) & io.INB(5)) & io.INA(3)) | ((io.INA(5) & !io.INB(5)) & io.INB(3)) | ((io.INA(5) & io.INB(5)) & io.INS(3))) ##
    (((!io.INA(5) & io.INB(5)) & io.INA(2)) | ((io.INA(5) & !io.INB(5)) & io.INB(2)) | ((io.INA(5) & io.INB(5)) & io.INS(2))) ##
    (((!io.INA(5) & io.INB(5)) & io.INA(1)) | ((io.INA(5) & !io.INB(5)) & io.INB(1)) | ((io.INA(5) & io.INB(5)) & io.INS(1))) ##
    (((!io.INA(5) & io.INB(5)) & io.INA(0)) | ((io.INA(5) & !io.INB(5)) & io.INB(0)) | ((io.INA(5) & io.INB(5)) & io.INS(0))))
  io.P6 := ( ((io.INA(6) & io.INB(6)) & io.INS(6)) ##
    ((io.INA(6) & io.INB(6)) & !io.INS(6)) ##
    (((!io.INA(6) & io.INB(6)) & io.INA(5)) | ((io.INA(6) & !io.INB(6)) & io.INB(5)) | ((io.INA(6) & io.INB(6)) & io.INS(5))) ##
    (((!io.INA(6) & io.INB(6)) & io.INA(4)) | ((io.INA(6) & !io.INB(6)) & io.INB(4)) | ((io.INA(6) & io.INB(6)) & io.INS(4))) ##
    (((!io.INA(6) & io.INB(6)) & io.INA(3)) | ((io.INA(6) & !io.INB(6)) & io.INB(3)) | ((io.INA(6) & io.INB(6)) & io.INS(3))) ##
    (((!io.INA(6) & io.INB(6)) & io.INA(2)) | ((io.INA(6) & !io.INB(6)) & io.INB(2)) | ((io.INA(6) & io.INB(6)) & io.INS(2))) ##
    (((!io.INA(6) & io.INB(6)) & io.INA(1)) | ((io.INA(6) & !io.INB(6)) & io.INB(1)) | ((io.INA(6) & io.INB(6)) & io.INS(1))) ##
    (((!io.INA(6) & io.INB(6)) & io.INA(0)) | ((io.INA(6) & !io.INB(6)) & io.INB(0)) | ((io.INA(6) & io.INB(6)) & io.INS(0))) )
  io.P7 := ( ((io.INA(7) & io.INB(7)) & io.INS(7)) ##
    ((io.INA(7) & io.INB(7)) & !io.INS(7)) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(6)) | ((io.INA(7) & !io.INB(7)) & io.INB(6)) | ((io.INA(7) & io.INB(7)) & io.INS(6))) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(5)) | ((io.INA(7) & !io.INB(7)) & io.INB(5)) | ((io.INA(7) & io.INB(7)) & io.INS(5))) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(4)) | ((io.INA(7) & !io.INB(7)) & io.INB(4)) | ((io.INA(7) & io.INB(7)) & io.INS(4))) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(3)) | ((io.INA(7) & !io.INB(7)) & io.INB(3)) | ((io.INA(7) & io.INB(7)) & io.INS(3))) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(2)) | ((io.INA(7) & !io.INB(7)) & io.INB(2)) | ((io.INA(7) & io.INB(7)) & io.INS(2))) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(1)) | ((io.INA(7) & !io.INB(7)) & io.INB(1)) | ((io.INA(7) & io.INB(7)) & io.INS(1))) ##
    (((!io.INA(7) & io.INB(7)) & io.INA(0)) | ((io.INA(7) & !io.INB(7)) & io.INB(0)) | ((io.INA(7) & io.INB(7)) & io.INS(0))) )

  io.P8 := (((io.INA(8) & io.INB(8)) & io.INS(8)) ##
    ((io.INA(8) & io.INB(8)) & !io.INS(8)) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(7)) | ((io.INA(8) & !io.INB(8)) & io.INB(7)) | ((io.INA(8) & io.INB(8)) & io.INS(7))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(6)) | ((io.INA(8) & !io.INB(8)) & io.INB(6)) | ((io.INA(8) & io.INB(8)) & io.INS(6))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(5)) | ((io.INA(8) & !io.INB(8)) & io.INB(5)) | ((io.INA(8) & io.INB(8)) & io.INS(5))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(4)) | ((io.INA(8) & !io.INB(8)) & io.INB(4)) | ((io.INA(8) & io.INB(8)) & io.INS(4))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(3)) | ((io.INA(8) & !io.INB(8)) & io.INB(3)) | ((io.INA(8) & io.INB(8)) & io.INS(3))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(2)) | ((io.INA(8) & !io.INB(8)) & io.INB(2)) | ((io.INA(8) & io.INB(8)) & io.INS(2))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(1)) | ((io.INA(8) & !io.INB(8)) & io.INB(1)) | ((io.INA(8) & io.INB(8)) & io.INS(1))) ##
    (((!io.INA(8) & io.INB(8)) & io.INA(0)) | ((io.INA(8) & !io.INB(8)) & io.INB(0)) | ((io.INA(8) & io.INB(8)) & io.INS(0))))

  io.P9 := (((io.INA(9) & io.INB(9)) & io.INS(9)) ##
    ((io.INA(9) & io.INB(9)) & !io.INS(9)) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(8)) | ((io.INA(9) & !io.INB(9)) & io.INB(8)) | ((io.INA(9) & io.INB(9)) & io.INS(8))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(7)) | ((io.INA(9) & !io.INB(9)) & io.INB(7)) | ((io.INA(9) & io.INB(9)) & io.INS(7))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(6)) | ((io.INA(9) & !io.INB(9)) & io.INB(6)) | ((io.INA(9) & io.INB(9)) & io.INS(6))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(5)) | ((io.INA(9) & !io.INB(9)) & io.INB(5)) | ((io.INA(9) & io.INB(9)) & io.INS(5))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(4)) | ((io.INA(9) & !io.INB(9)) & io.INB(4)) | ((io.INA(9) & io.INB(9)) & io.INS(4))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(3)) | ((io.INA(9) & !io.INB(9)) & io.INB(3)) | ((io.INA(9) & io.INB(9)) & io.INS(3))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(2)) | ((io.INA(9) & !io.INB(9)) & io.INB(2)) | ((io.INA(9) & io.INB(9)) & io.INS(2))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(1)) | ((io.INA(9) & !io.INB(9)) & io.INB(1)) | ((io.INA(9) & io.INB(9)) & io.INS(1))) ##
    (((!io.INA(9) & io.INB(9)) & io.INA(0)) | ((io.INA(9) & !io.INB(9)) & io.INB(0)) | ((io.INA(9) & io.INB(9)) & io.INS(0))))
}

class P10to13 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(14.W))
    val INB = Input(UInt(14.W))
    val INS = Input(UInt(14.W))
    val P10 = Output(UInt(12.W))
    val P11 = Output(UInt(13.W))
    val P12 = Output(UInt(14.W))
    val P13 = Output(UInt(15.W))
  })
  io.P10 := (((io.INA(10) & io.INB(10)) & io.INS(10)) ##
    ((io.INA(10) & io.INB(10)) & !io.INS(10)) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(9)) | ((io.INA(10) & !io.INB(10)) & io.INB(9)) | ((io.INA(10) & io.INB(10)) & io.INS(9))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(8)) | ((io.INA(10) & !io.INB(10)) & io.INB(8)) | ((io.INA(10) & io.INB(10)) & io.INS(8))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(7)) | ((io.INA(10) & !io.INB(10)) & io.INB(7)) | ((io.INA(10) & io.INB(10)) & io.INS(7))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(6)) | ((io.INA(10) & !io.INB(10)) & io.INB(6)) | ((io.INA(10) & io.INB(10)) & io.INS(6))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(5)) | ((io.INA(10) & !io.INB(10)) & io.INB(5)) | ((io.INA(10) & io.INB(10)) & io.INS(5))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(4)) | ((io.INA(10) & !io.INB(10)) & io.INB(4)) | ((io.INA(10) & io.INB(10)) & io.INS(4))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(3)) | ((io.INA(10) & !io.INB(10)) & io.INB(3)) | ((io.INA(10) & io.INB(10)) & io.INS(3))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(2)) | ((io.INA(10) & !io.INB(10)) & io.INB(2)) | ((io.INA(10) & io.INB(10)) & io.INS(2))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(1)) | ((io.INA(10) & !io.INB(10)) & io.INB(1)) | ((io.INA(10) & io.INB(10)) & io.INS(1))) ##
    (((!io.INA(10) & io.INB(10)) & io.INA(0)) | ((io.INA(10) & !io.INB(10)) & io.INB(0)) | ((io.INA(10) & io.INB(10)) & io.INS(0))))

  io.P11 := (((io.INA(11) & io.INB(11)) & io.INS(11)) ##
    ((io.INA(11) & io.INB(11)) & !io.INS(11)) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(10)) | ((io.INA(11) & !io.INB(11)) & io.INB(10)) | ((io.INA(11) & io.INB(11)) & io.INS(10))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(9)) | ((io.INA(11) & !io.INB(11)) & io.INB(9)) | ((io.INA(11) & io.INB(11)) & io.INS(9))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(8)) | ((io.INA(11) & !io.INB(11)) & io.INB(8)) | ((io.INA(11) & io.INB(11)) & io.INS(8))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(7)) | ((io.INA(11) & !io.INB(11)) & io.INB(7)) | ((io.INA(11) & io.INB(11)) & io.INS(7))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(6)) | ((io.INA(11) & !io.INB(11)) & io.INB(6)) | ((io.INA(11) & io.INB(11)) & io.INS(6))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(5)) | ((io.INA(11) & !io.INB(11)) & io.INB(5)) | ((io.INA(11) & io.INB(11)) & io.INS(5))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(4)) | ((io.INA(11) & !io.INB(11)) & io.INB(4)) | ((io.INA(11) & io.INB(11)) & io.INS(4))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(3)) | ((io.INA(11) & !io.INB(11)) & io.INB(3)) | ((io.INA(11) & io.INB(11)) & io.INS(3))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(2)) | ((io.INA(11) & !io.INB(11)) & io.INB(2)) | ((io.INA(11) & io.INB(11)) & io.INS(2))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(1)) | ((io.INA(11) & !io.INB(11)) & io.INB(1)) | ((io.INA(11) & io.INB(11)) & io.INS(1))) ##
    (((!io.INA(11) & io.INB(11)) & io.INA(0)) | ((io.INA(11) & !io.INB(11)) & io.INB(0)) | ((io.INA(11) & io.INB(11)) & io.INS(0))))

  io.P12 := (((io.INA(12) & io.INB(12)) & io.INS(12)) ##
    ((io.INA(12) & io.INB(12)) & !io.INS(12)) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(11)) | ((io.INA(12) & !io.INB(12)) & io.INB(11)) | ((io.INA(12) & io.INB(12)) & io.INS(11))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(10)) | ((io.INA(12) & !io.INB(12)) & io.INB(10)) | ((io.INA(12) & io.INB(12)) & io.INS(10))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(9)) | ((io.INA(12) & !io.INB(12)) & io.INB(9)) | ((io.INA(12) & io.INB(12)) & io.INS(9))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(8)) | ((io.INA(12) & !io.INB(12)) & io.INB(8)) | ((io.INA(12) & io.INB(12)) & io.INS(8))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(7)) | ((io.INA(12) & !io.INB(12)) & io.INB(7)) | ((io.INA(12) & io.INB(12)) & io.INS(7))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(6)) | ((io.INA(12) & !io.INB(12)) & io.INB(6)) | ((io.INA(12) & io.INB(12)) & io.INS(6))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(5)) | ((io.INA(12) & !io.INB(12)) & io.INB(5)) | ((io.INA(12) & io.INB(12)) & io.INS(5))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(4)) | ((io.INA(12) & !io.INB(12)) & io.INB(4)) | ((io.INA(12) & io.INB(12)) & io.INS(4))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(3)) | ((io.INA(12) & !io.INB(12)) & io.INB(3)) | ((io.INA(12) & io.INB(12)) & io.INS(3))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(2)) | ((io.INA(12) & !io.INB(12)) & io.INB(2)) | ((io.INA(12) & io.INB(12)) & io.INS(2))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(1)) | ((io.INA(12) & !io.INB(12)) & io.INB(1)) | ((io.INA(12) & io.INB(12)) & io.INS(1))) ##
    (((!io.INA(12) & io.INB(12)) & io.INA(0)) | ((io.INA(12) & !io.INB(12)) & io.INB(0)) | ((io.INA(12) & io.INB(12)) & io.INS(0))))

  io.P13 := ( ((io.INA(13) & io.INB(13)) & io.INS(13)) ##
    ((io.INA(13) & io.INB(13)) & !io.INS(13)) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(12)) | ((io.INA(13) & !io.INB(13)) & io.INB(12)) | ((io.INA(13) & io.INB(13)) & io.INS(12))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(11)) | ((io.INA(13) & !io.INB(13)) & io.INB(11)) | ((io.INA(13) & io.INB(13)) & io.INS(11))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(10)) | ((io.INA(13) & !io.INB(13)) & io.INB(10)) | ((io.INA(13) & io.INB(13)) & io.INS(10))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(9)) | ((io.INA(13) & !io.INB(13)) & io.INB(9)) | ((io.INA(13) & io.INB(13)) & io.INS(9))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(8)) | ((io.INA(13) & !io.INB(13)) & io.INB(8)) | ((io.INA(13) & io.INB(13)) & io.INS(8))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(7)) | ((io.INA(13) & !io.INB(13)) & io.INB(7)) | ((io.INA(13) & io.INB(13)) & io.INS(7))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(6)) | ((io.INA(13) & !io.INB(13)) & io.INB(6)) | ((io.INA(13) & io.INB(13)) & io.INS(6))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(5)) | ((io.INA(13) & !io.INB(13)) & io.INB(5)) | ((io.INA(13) & io.INB(13)) & io.INS(5))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(4)) | ((io.INA(13) & !io.INB(13)) & io.INB(4)) | ((io.INA(13) & io.INB(13)) & io.INS(4))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(3)) | ((io.INA(13) & !io.INB(13)) & io.INB(3)) | ((io.INA(13) & io.INB(13)) & io.INS(3))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(2)) | ((io.INA(13) & !io.INB(13)) & io.INB(2)) | ((io.INA(13) & io.INB(13)) & io.INS(2))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(1)) | ((io.INA(13) & !io.INB(13)) & io.INB(1)) | ((io.INA(13) & io.INB(13)) & io.INS(1))) ##
    (((!io.INA(13) & io.INB(13)) & io.INA(0)) | ((io.INA(13) & !io.INB(13)) & io.INB(0)) | ((io.INA(13) & io.INB(13)) & io.INS(0))) )

}

class P14to16 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(17.W))
    val INB = Input(UInt(17.W))
    val INS = Input(UInt(17.W))
    val P14 = Output(UInt(16.W))
    val P15 = Output(UInt(17.W))
    val P16 = Output(UInt(18.W))
  })
  io.P14 := ( ((io.INA(14) & io.INB(14)) & io.INS(14)) ##
    ((io.INA(14) & io.INB(14)) & !io.INS(14)) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(13)) | ((io.INA(14) & !io.INB(14)) & io.INB(13)) | ((io.INA(14) & io.INB(14)) & io.INS(13))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(12)) | ((io.INA(14) & !io.INB(14)) & io.INB(12)) | ((io.INA(14) & io.INB(14)) & io.INS(12))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(11)) | ((io.INA(14) & !io.INB(14)) & io.INB(11)) | ((io.INA(14) & io.INB(14)) & io.INS(11))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(10)) | ((io.INA(14) & !io.INB(14)) & io.INB(10)) | ((io.INA(14) & io.INB(14)) & io.INS(10))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(9)) | ((io.INA(14) & !io.INB(14)) & io.INB(9)) | ((io.INA(14) & io.INB(14)) & io.INS(9))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(8)) | ((io.INA(14) & !io.INB(14)) & io.INB(8)) | ((io.INA(14) & io.INB(14)) & io.INS(8))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(7)) | ((io.INA(14) & !io.INB(14)) & io.INB(7)) | ((io.INA(14) & io.INB(14)) & io.INS(7))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(6)) | ((io.INA(14) & !io.INB(14)) & io.INB(6)) | ((io.INA(14) & io.INB(14)) & io.INS(6))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(5)) | ((io.INA(14) & !io.INB(14)) & io.INB(5)) | ((io.INA(14) & io.INB(14)) & io.INS(5))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(4)) | ((io.INA(14) & !io.INB(14)) & io.INB(4)) | ((io.INA(14) & io.INB(14)) & io.INS(4))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(3)) | ((io.INA(14) & !io.INB(14)) & io.INB(3)) | ((io.INA(14) & io.INB(14)) & io.INS(3))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(2)) | ((io.INA(14) & !io.INB(14)) & io.INB(2)) | ((io.INA(14) & io.INB(14)) & io.INS(2))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(1)) | ((io.INA(14) & !io.INB(14)) & io.INB(1)) | ((io.INA(14) & io.INB(14)) & io.INS(1))) ##
    (((!io.INA(14) & io.INB(14)) & io.INA(0)) | ((io.INA(14) & !io.INB(14)) & io.INB(0)) | ((io.INA(14) & io.INB(14)) & io.INS(0))) )
  io.P15 := ( ((io.INA(15) & io.INB(15)) & io.INS(15)) ##
    ((io.INA(15) & io.INB(15)) & !io.INS(15)) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(14)) | ((io.INA(15) & !io.INB(15)) & io.INB(14)) | ((io.INA(15) & io.INB(15)) & io.INS(14))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(13)) | ((io.INA(15) & !io.INB(15)) & io.INB(13)) | ((io.INA(15) & io.INB(15)) & io.INS(13))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(12)) | ((io.INA(15) & !io.INB(15)) & io.INB(12)) | ((io.INA(15) & io.INB(15)) & io.INS(12))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(11)) | ((io.INA(15) & !io.INB(15)) & io.INB(11)) | ((io.INA(15) & io.INB(15)) & io.INS(11))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(10)) | ((io.INA(15) & !io.INB(15)) & io.INB(10)) | ((io.INA(15) & io.INB(15)) & io.INS(10))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(9)) | ((io.INA(15) & !io.INB(15)) & io.INB(9)) | ((io.INA(15) & io.INB(15)) & io.INS(9))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(8)) | ((io.INA(15) & !io.INB(15)) & io.INB(8)) | ((io.INA(15) & io.INB(15)) & io.INS(8))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(7)) | ((io.INA(15) & !io.INB(15)) & io.INB(7)) | ((io.INA(15) & io.INB(15)) & io.INS(7))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(6)) | ((io.INA(15) & !io.INB(15)) & io.INB(6)) | ((io.INA(15) & io.INB(15)) & io.INS(6))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(5)) | ((io.INA(15) & !io.INB(15)) & io.INB(5)) | ((io.INA(15) & io.INB(15)) & io.INS(5))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(4)) | ((io.INA(15) & !io.INB(15)) & io.INB(4)) | ((io.INA(15) & io.INB(15)) & io.INS(4))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(3)) | ((io.INA(15) & !io.INB(15)) & io.INB(3)) | ((io.INA(15) & io.INB(15)) & io.INS(3))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(2)) | ((io.INA(15) & !io.INB(15)) & io.INB(2)) | ((io.INA(15) & io.INB(15)) & io.INS(2))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(1)) | ((io.INA(15) & !io.INB(15)) & io.INB(1)) | ((io.INA(15) & io.INB(15)) & io.INS(1))) ##
    (((!io.INA(15) & io.INB(15)) & io.INA(0)) | ((io.INA(15) & !io.INB(15)) & io.INB(0)) | ((io.INA(15) & io.INB(15)) & io.INS(0))) )

  io.P16 := ( ((io.INA(16) & io.INB(16)) & io.INS(16)) ##
    ((io.INA(16) & io.INB(16)) & !io.INS(16)) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(15)) | ((io.INA(16) & !io.INB(16)) & io.INB(15)) | ((io.INA(16) & io.INB(16)) & io.INS(15))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(14)) | ((io.INA(16) & !io.INB(16)) & io.INB(14)) | ((io.INA(16) & io.INB(16)) & io.INS(14))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(13)) | ((io.INA(16) & !io.INB(16)) & io.INB(13)) | ((io.INA(16) & io.INB(16)) & io.INS(13))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(12)) | ((io.INA(16) & !io.INB(16)) & io.INB(12)) | ((io.INA(16) & io.INB(16)) & io.INS(12))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(11)) | ((io.INA(16) & !io.INB(16)) & io.INB(11)) | ((io.INA(16) & io.INB(16)) & io.INS(11))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(10)) | ((io.INA(16) & !io.INB(16)) & io.INB(10)) | ((io.INA(16) & io.INB(16)) & io.INS(10))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(9)) | ((io.INA(16) & !io.INB(16)) & io.INB(9)) | ((io.INA(16) & io.INB(16)) & io.INS(9))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(8)) | ((io.INA(16) & !io.INB(16)) & io.INB(8)) | ((io.INA(16) & io.INB(16)) & io.INS(8))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(7)) | ((io.INA(16) & !io.INB(16)) & io.INB(7)) | ((io.INA(16) & io.INB(16)) & io.INS(7))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(6)) | ((io.INA(16) & !io.INB(16)) & io.INB(6)) | ((io.INA(16) & io.INB(16)) & io.INS(6))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(5)) | ((io.INA(16) & !io.INB(16)) & io.INB(5)) | ((io.INA(16) & io.INB(16)) & io.INS(5))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(4)) | ((io.INA(16) & !io.INB(16)) & io.INB(4)) | ((io.INA(16) & io.INB(16)) & io.INS(4))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(3)) | ((io.INA(16) & !io.INB(16)) & io.INB(3)) | ((io.INA(16) & io.INB(16)) & io.INS(3))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(2)) | ((io.INA(16) & !io.INB(16)) & io.INB(2)) | ((io.INA(16) & io.INB(16)) & io.INS(2))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(1)) | ((io.INA(16) & !io.INB(16)) & io.INB(1)) | ((io.INA(16) & io.INB(16)) & io.INS(1))) ##
    (((!io.INA(16) & io.INB(16)) & io.INA(0)) | ((io.INA(16) & !io.INB(16)) & io.INB(0)) | ((io.INA(16) & io.INB(16)) & io.INS(0))) )
}

class P17to18 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(19.W))
    val INB = Input(UInt(19.W))
    val INS = Input(UInt(19.W))
    val P17 = Output(UInt(19.W))
    val P18 = Output(UInt(20.W))
  })
  io.P17 := ( ((io.INA(17) & io.INB(17)) & io.INS(17)) ##
    ((io.INA(17) & io.INB(17)) & !io.INS(17)) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(16)) | ((io.INA(17) & !io.INB(17)) & io.INB(16)) | ((io.INA(17) & io.INB(17)) & io.INS(16))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(15)) | ((io.INA(17) & !io.INB(17)) & io.INB(15)) | ((io.INA(17) & io.INB(17)) & io.INS(15))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(14)) | ((io.INA(17) & !io.INB(17)) & io.INB(14)) | ((io.INA(17) & io.INB(17)) & io.INS(14))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(13)) | ((io.INA(17) & !io.INB(17)) & io.INB(13)) | ((io.INA(17) & io.INB(17)) & io.INS(13))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(12)) | ((io.INA(17) & !io.INB(17)) & io.INB(12)) | ((io.INA(17) & io.INB(17)) & io.INS(12))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(11)) | ((io.INA(17) & !io.INB(17)) & io.INB(11)) | ((io.INA(17) & io.INB(17)) & io.INS(11))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(10)) | ((io.INA(17) & !io.INB(17)) & io.INB(10)) | ((io.INA(17) & io.INB(17)) & io.INS(10))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(9)) | ((io.INA(17) & !io.INB(17)) & io.INB(9)) | ((io.INA(17) & io.INB(17)) & io.INS(9))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(8)) | ((io.INA(17) & !io.INB(17)) & io.INB(8)) | ((io.INA(17) & io.INB(17)) & io.INS(8))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(7)) | ((io.INA(17) & !io.INB(17)) & io.INB(7)) | ((io.INA(17) & io.INB(17)) & io.INS(7))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(6)) | ((io.INA(17) & !io.INB(17)) & io.INB(6)) | ((io.INA(17) & io.INB(17)) & io.INS(6))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(5)) | ((io.INA(17) & !io.INB(17)) & io.INB(5)) | ((io.INA(17) & io.INB(17)) & io.INS(5))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(4)) | ((io.INA(17) & !io.INB(17)) & io.INB(4)) | ((io.INA(17) & io.INB(17)) & io.INS(4))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(3)) | ((io.INA(17) & !io.INB(17)) & io.INB(3)) | ((io.INA(17) & io.INB(17)) & io.INS(3))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(2)) | ((io.INA(17) & !io.INB(17)) & io.INB(2)) | ((io.INA(17) & io.INB(17)) & io.INS(2))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(1)) | ((io.INA(17) & !io.INB(17)) & io.INB(1)) | ((io.INA(17) & io.INB(17)) & io.INS(1))) ##
    (((!io.INA(17) & io.INB(17)) & io.INA(0)) | ((io.INA(17) & !io.INB(17)) & io.INB(0)) | ((io.INA(17) & io.INB(17)) & io.INS(0))) )
  io.P18 := ( ((io.INA(18) & io.INB(18)) & io.INS(18)) ##
    ((io.INA(18) & io.INB(18)) & !io.INS(18)) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(17)) | ((io.INA(18) & !io.INB(18)) & io.INB(17)) | ((io.INA(18) & io.INB(18)) & io.INS(17))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(16)) | ((io.INA(18) & !io.INB(18)) & io.INB(16)) | ((io.INA(18) & io.INB(18)) & io.INS(16))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(15)) | ((io.INA(18) & !io.INB(18)) & io.INB(15)) | ((io.INA(18) & io.INB(18)) & io.INS(15))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(14)) | ((io.INA(18) & !io.INB(18)) & io.INB(14)) | ((io.INA(18) & io.INB(18)) & io.INS(14))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(13)) | ((io.INA(18) & !io.INB(18)) & io.INB(13)) | ((io.INA(18) & io.INB(18)) & io.INS(13))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(12)) | ((io.INA(18) & !io.INB(18)) & io.INB(12)) | ((io.INA(18) & io.INB(18)) & io.INS(12))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(11)) | ((io.INA(18) & !io.INB(18)) & io.INB(11)) | ((io.INA(18) & io.INB(18)) & io.INS(11))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(10)) | ((io.INA(18) & !io.INB(18)) & io.INB(10)) | ((io.INA(18) & io.INB(18)) & io.INS(10))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(9)) | ((io.INA(18) & !io.INB(18)) & io.INB(9)) | ((io.INA(18) & io.INB(18)) & io.INS(9))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(8)) | ((io.INA(18) & !io.INB(18)) & io.INB(8)) | ((io.INA(18) & io.INB(18)) & io.INS(8))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(7)) | ((io.INA(18) & !io.INB(18)) & io.INB(7)) | ((io.INA(18) & io.INB(18)) & io.INS(7))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(6)) | ((io.INA(18) & !io.INB(18)) & io.INB(6)) | ((io.INA(18) & io.INB(18)) & io.INS(6))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(5)) | ((io.INA(18) & !io.INB(18)) & io.INB(5)) | ((io.INA(18) & io.INB(18)) & io.INS(5))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(4)) | ((io.INA(18) & !io.INB(18)) & io.INB(4)) | ((io.INA(18) & io.INB(18)) & io.INS(4))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(3)) | ((io.INA(18) & !io.INB(18)) & io.INB(3)) | ((io.INA(18) & io.INB(18)) & io.INS(3))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(2)) | ((io.INA(18) & !io.INB(18)) & io.INB(2)) | ((io.INA(18) & io.INB(18)) & io.INS(2))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(1)) | ((io.INA(18) & !io.INB(18)) & io.INB(1)) | ((io.INA(18) & io.INB(18)) & io.INS(1))) ##
    (((!io.INA(18) & io.INB(18)) & io.INA(0)) | ((io.INA(18) & !io.INB(18)) & io.INB(0)) | ((io.INA(18) & io.INB(18)) & io.INS(0))) )
}

class P19to20 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(21.W))
    val INB = Input(UInt(21.W))
    val INS = Input(UInt(21.W))
    val P19 = Output(UInt(21.W))
    val P20 = Output(UInt(22.W))
  })
  io.P19 := ( ((io.INA(19) & io.INB(19)) & io.INS(19)) ##
    ((io.INA(19) & io.INB(19)) & !io.INS(19)) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(18)) | ((io.INA(19) & !io.INB(19)) & io.INB(18)) | ((io.INA(19) & io.INB(19)) & io.INS(18))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(17)) | ((io.INA(19) & !io.INB(19)) & io.INB(17)) | ((io.INA(19) & io.INB(19)) & io.INS(17))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(16)) | ((io.INA(19) & !io.INB(19)) & io.INB(16)) | ((io.INA(19) & io.INB(19)) & io.INS(16))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(15)) | ((io.INA(19) & !io.INB(19)) & io.INB(15)) | ((io.INA(19) & io.INB(19)) & io.INS(15))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(14)) | ((io.INA(19) & !io.INB(19)) & io.INB(14)) | ((io.INA(19) & io.INB(19)) & io.INS(14))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(13)) | ((io.INA(19) & !io.INB(19)) & io.INB(13)) | ((io.INA(19) & io.INB(19)) & io.INS(13))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(12)) | ((io.INA(19) & !io.INB(19)) & io.INB(12)) | ((io.INA(19) & io.INB(19)) & io.INS(12))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(11)) | ((io.INA(19) & !io.INB(19)) & io.INB(11)) | ((io.INA(19) & io.INB(19)) & io.INS(11))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(10)) | ((io.INA(19) & !io.INB(19)) & io.INB(10)) | ((io.INA(19) & io.INB(19)) & io.INS(10))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(9)) | ((io.INA(19) & !io.INB(19)) & io.INB(9)) | ((io.INA(19) & io.INB(19)) & io.INS(9))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(8)) | ((io.INA(19) & !io.INB(19)) & io.INB(8)) | ((io.INA(19) & io.INB(19)) & io.INS(8))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(7)) | ((io.INA(19) & !io.INB(19)) & io.INB(7)) | ((io.INA(19) & io.INB(19)) & io.INS(7))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(6)) | ((io.INA(19) & !io.INB(19)) & io.INB(6)) | ((io.INA(19) & io.INB(19)) & io.INS(6))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(5)) | ((io.INA(19) & !io.INB(19)) & io.INB(5)) | ((io.INA(19) & io.INB(19)) & io.INS(5))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(4)) | ((io.INA(19) & !io.INB(19)) & io.INB(4)) | ((io.INA(19) & io.INB(19)) & io.INS(4))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(3)) | ((io.INA(19) & !io.INB(19)) & io.INB(3)) | ((io.INA(19) & io.INB(19)) & io.INS(3))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(2)) | ((io.INA(19) & !io.INB(19)) & io.INB(2)) | ((io.INA(19) & io.INB(19)) & io.INS(2))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(1)) | ((io.INA(19) & !io.INB(19)) & io.INB(1)) | ((io.INA(19) & io.INB(19)) & io.INS(1))) ##
    (((!io.INA(19) & io.INB(19)) & io.INA(0)) | ((io.INA(19) & !io.INB(19)) & io.INB(0)) | ((io.INA(19) & io.INB(19)) & io.INS(0))) )
  io.P20 := ( ((io.INA(20) & io.INB(20)) & io.INS(20)) ##
    ((io.INA(20) & io.INB(20)) & !io.INS(20)) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(19)) | ((io.INA(20) & !io.INB(20)) & io.INB(19)) | ((io.INA(20) & io.INB(20)) & io.INS(19))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(18)) | ((io.INA(20) & !io.INB(20)) & io.INB(18)) | ((io.INA(20) & io.INB(20)) & io.INS(18))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(17)) | ((io.INA(20) & !io.INB(20)) & io.INB(17)) | ((io.INA(20) & io.INB(20)) & io.INS(17))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(16)) | ((io.INA(20) & !io.INB(20)) & io.INB(16)) | ((io.INA(20) & io.INB(20)) & io.INS(16))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(15)) | ((io.INA(20) & !io.INB(20)) & io.INB(15)) | ((io.INA(20) & io.INB(20)) & io.INS(15))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(14)) | ((io.INA(20) & !io.INB(20)) & io.INB(14)) | ((io.INA(20) & io.INB(20)) & io.INS(14))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(13)) | ((io.INA(20) & !io.INB(20)) & io.INB(13)) | ((io.INA(20) & io.INB(20)) & io.INS(13))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(12)) | ((io.INA(20) & !io.INB(20)) & io.INB(12)) | ((io.INA(20) & io.INB(20)) & io.INS(12))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(11)) | ((io.INA(20) & !io.INB(20)) & io.INB(11)) | ((io.INA(20) & io.INB(20)) & io.INS(11))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(10)) | ((io.INA(20) & !io.INB(20)) & io.INB(10)) | ((io.INA(20) & io.INB(20)) & io.INS(10))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(9)) | ((io.INA(20) & !io.INB(20)) & io.INB(9)) | ((io.INA(20) & io.INB(20)) & io.INS(9))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(8)) | ((io.INA(20) & !io.INB(20)) & io.INB(8)) | ((io.INA(20) & io.INB(20)) & io.INS(8))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(7)) | ((io.INA(20) & !io.INB(20)) & io.INB(7)) | ((io.INA(20) & io.INB(20)) & io.INS(7))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(6)) | ((io.INA(20) & !io.INB(20)) & io.INB(6)) | ((io.INA(20) & io.INB(20)) & io.INS(6))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(5)) | ((io.INA(20) & !io.INB(20)) & io.INB(5)) | ((io.INA(20) & io.INB(20)) & io.INS(5))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(4)) | ((io.INA(20) & !io.INB(20)) & io.INB(4)) | ((io.INA(20) & io.INB(20)) & io.INS(4))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(3)) | ((io.INA(20) & !io.INB(20)) & io.INB(3)) | ((io.INA(20) & io.INB(20)) & io.INS(3))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(2)) | ((io.INA(20) & !io.INB(20)) & io.INB(2)) | ((io.INA(20) & io.INB(20)) & io.INS(2))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(1)) | ((io.INA(20) & !io.INB(20)) & io.INB(1)) | ((io.INA(20) & io.INB(20)) & io.INS(1))) ##
    (((!io.INA(20) & io.INB(20)) & io.INA(0)) | ((io.INA(20) & !io.INB(20)) & io.INB(0)) | ((io.INA(20) & io.INB(20)) & io.INS(0))) )
}

class P21to22 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(23.W))
    val INB = Input(UInt(23.W))
    val INS = Input(UInt(23.W))
    val P21 = Output(UInt(23.W))
    val P22 = Output(UInt(24.W))
  })
  io.P21 := ( ((io.INA(21) & io.INB(21)) & io.INS(21)) ##
    ((io.INA(21) & io.INB(21)) & !io.INS(21)) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(20)) | ((io.INA(21) & !io.INB(21)) & io.INB(20)) | ((io.INA(21) & io.INB(21)) & io.INS(20))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(19)) | ((io.INA(21) & !io.INB(21)) & io.INB(19)) | ((io.INA(21) & io.INB(21)) & io.INS(19))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(18)) | ((io.INA(21) & !io.INB(21)) & io.INB(18)) | ((io.INA(21) & io.INB(21)) & io.INS(18))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(17)) | ((io.INA(21) & !io.INB(21)) & io.INB(17)) | ((io.INA(21) & io.INB(21)) & io.INS(17))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(16)) | ((io.INA(21) & !io.INB(21)) & io.INB(16)) | ((io.INA(21) & io.INB(21)) & io.INS(16))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(15)) | ((io.INA(21) & !io.INB(21)) & io.INB(15)) | ((io.INA(21) & io.INB(21)) & io.INS(15))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(14)) | ((io.INA(21) & !io.INB(21)) & io.INB(14)) | ((io.INA(21) & io.INB(21)) & io.INS(14))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(13)) | ((io.INA(21) & !io.INB(21)) & io.INB(13)) | ((io.INA(21) & io.INB(21)) & io.INS(13))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(12)) | ((io.INA(21) & !io.INB(21)) & io.INB(12)) | ((io.INA(21) & io.INB(21)) & io.INS(12))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(11)) | ((io.INA(21) & !io.INB(21)) & io.INB(11)) | ((io.INA(21) & io.INB(21)) & io.INS(11))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(10)) | ((io.INA(21) & !io.INB(21)) & io.INB(10)) | ((io.INA(21) & io.INB(21)) & io.INS(10))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(9)) | ((io.INA(21) & !io.INB(21)) & io.INB(9)) | ((io.INA(21) & io.INB(21)) & io.INS(9))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(8)) | ((io.INA(21) & !io.INB(21)) & io.INB(8)) | ((io.INA(21) & io.INB(21)) & io.INS(8))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(7)) | ((io.INA(21) & !io.INB(21)) & io.INB(7)) | ((io.INA(21) & io.INB(21)) & io.INS(7))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(6)) | ((io.INA(21) & !io.INB(21)) & io.INB(6)) | ((io.INA(21) & io.INB(21)) & io.INS(6))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(5)) | ((io.INA(21) & !io.INB(21)) & io.INB(5)) | ((io.INA(21) & io.INB(21)) & io.INS(5))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(4)) | ((io.INA(21) & !io.INB(21)) & io.INB(4)) | ((io.INA(21) & io.INB(21)) & io.INS(4))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(3)) | ((io.INA(21) & !io.INB(21)) & io.INB(3)) | ((io.INA(21) & io.INB(21)) & io.INS(3))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(2)) | ((io.INA(21) & !io.INB(21)) & io.INB(2)) | ((io.INA(21) & io.INB(21)) & io.INS(2))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(1)) | ((io.INA(21) & !io.INB(21)) & io.INB(1)) | ((io.INA(21) & io.INB(21)) & io.INS(1))) ##
    (((!io.INA(21) & io.INB(21)) & io.INA(0)) | ((io.INA(21) & !io.INB(21)) & io.INB(0)) | ((io.INA(21) & io.INB(21)) & io.INS(0))) )
  io.P22 := ( ((io.INA(22) & io.INB(22)) & io.INS(22)) ##
    ((io.INA(22) & io.INB(22)) & !io.INS(22)) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(21)) | ((io.INA(22) & !io.INB(22)) & io.INB(21)) | ((io.INA(22) & io.INB(22)) & io.INS(21))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(20)) | ((io.INA(22) & !io.INB(22)) & io.INB(20)) | ((io.INA(22) & io.INB(22)) & io.INS(20))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(19)) | ((io.INA(22) & !io.INB(22)) & io.INB(19)) | ((io.INA(22) & io.INB(22)) & io.INS(19))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(18)) | ((io.INA(22) & !io.INB(22)) & io.INB(18)) | ((io.INA(22) & io.INB(22)) & io.INS(18))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(17)) | ((io.INA(22) & !io.INB(22)) & io.INB(17)) | ((io.INA(22) & io.INB(22)) & io.INS(17))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(16)) | ((io.INA(22) & !io.INB(22)) & io.INB(16)) | ((io.INA(22) & io.INB(22)) & io.INS(16))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(15)) | ((io.INA(22) & !io.INB(22)) & io.INB(15)) | ((io.INA(22) & io.INB(22)) & io.INS(15))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(14)) | ((io.INA(22) & !io.INB(22)) & io.INB(14)) | ((io.INA(22) & io.INB(22)) & io.INS(14))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(13)) | ((io.INA(22) & !io.INB(22)) & io.INB(13)) | ((io.INA(22) & io.INB(22)) & io.INS(13))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(12)) | ((io.INA(22) & !io.INB(22)) & io.INB(12)) | ((io.INA(22) & io.INB(22)) & io.INS(12))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(11)) | ((io.INA(22) & !io.INB(22)) & io.INB(11)) | ((io.INA(22) & io.INB(22)) & io.INS(11))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(10)) | ((io.INA(22) & !io.INB(22)) & io.INB(10)) | ((io.INA(22) & io.INB(22)) & io.INS(10))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(9)) | ((io.INA(22) & !io.INB(22)) & io.INB(9)) | ((io.INA(22) & io.INB(22)) & io.INS(9))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(8)) | ((io.INA(22) & !io.INB(22)) & io.INB(8)) | ((io.INA(22) & io.INB(22)) & io.INS(8))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(7)) | ((io.INA(22) & !io.INB(22)) & io.INB(7)) | ((io.INA(22) & io.INB(22)) & io.INS(7))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(6)) | ((io.INA(22) & !io.INB(22)) & io.INB(6)) | ((io.INA(22) & io.INB(22)) & io.INS(6))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(5)) | ((io.INA(22) & !io.INB(22)) & io.INB(5)) | ((io.INA(22) & io.INB(22)) & io.INS(5))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(4)) | ((io.INA(22) & !io.INB(22)) & io.INB(4)) | ((io.INA(22) & io.INB(22)) & io.INS(4))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(3)) | ((io.INA(22) & !io.INB(22)) & io.INB(3)) | ((io.INA(22) & io.INB(22)) & io.INS(3))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(2)) | ((io.INA(22) & !io.INB(22)) & io.INB(2)) | ((io.INA(22) & io.INB(22)) & io.INS(2))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(1)) | ((io.INA(22) & !io.INB(22)) & io.INB(1)) | ((io.INA(22) & io.INB(22)) & io.INS(1))) ##
    (((!io.INA(22) & io.INB(22)) & io.INA(0)) | ((io.INA(22) & !io.INB(22)) & io.INB(0)) | ((io.INA(22) & io.INB(22)) & io.INS(0))) )
}

class P23to23 extends Module {
  val io = IO(new Bundle {
    val INA = Input(UInt(24.W))
    val INB = Input(UInt(24.W))
    val INS = Input(UInt(25.W))
    val P23 = Output(UInt(25.W))
  })
  io.P23 := ( ((io.INA(23) & io.INB(23)) & (io.INS(24) | io.INS(23)) ) ##
    ((io.INA(23) & io.INB(23)) & !(io.INS(24) | io.INS(23)) ) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(22)) | ((io.INA(23) & !io.INB(23)) & io.INB(22)) | ((io.INA(23) & io.INB(23)) & io.INS(22))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(21)) | ((io.INA(23) & !io.INB(23)) & io.INB(21)) | ((io.INA(23) & io.INB(23)) & io.INS(21))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(20)) | ((io.INA(23) & !io.INB(23)) & io.INB(20)) | ((io.INA(23) & io.INB(23)) & io.INS(20))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(19)) | ((io.INA(23) & !io.INB(23)) & io.INB(19)) | ((io.INA(23) & io.INB(23)) & io.INS(19))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(18)) | ((io.INA(23) & !io.INB(23)) & io.INB(18)) | ((io.INA(23) & io.INB(23)) & io.INS(18))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(17)) | ((io.INA(23) & !io.INB(23)) & io.INB(17)) | ((io.INA(23) & io.INB(23)) & io.INS(17))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(16)) | ((io.INA(23) & !io.INB(23)) & io.INB(16)) | ((io.INA(23) & io.INB(23)) & io.INS(16))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(15)) | ((io.INA(23) & !io.INB(23)) & io.INB(15)) | ((io.INA(23) & io.INB(23)) & io.INS(15))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(14)) | ((io.INA(23) & !io.INB(23)) & io.INB(14)) | ((io.INA(23) & io.INB(23)) & io.INS(14))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(13)) | ((io.INA(23) & !io.INB(23)) & io.INB(13)) | ((io.INA(23) & io.INB(23)) & io.INS(13))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(12)) | ((io.INA(23) & !io.INB(23)) & io.INB(12)) | ((io.INA(23) & io.INB(23)) & io.INS(12))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(11)) | ((io.INA(23) & !io.INB(23)) & io.INB(11)) | ((io.INA(22) & io.INB(23)) & io.INS(11))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(10)) | ((io.INA(23) & !io.INB(23)) & io.INB(10)) | ((io.INA(23) & io.INB(23)) & io.INS(10))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(9)) | ((io.INA(23) & !io.INB(23)) & io.INB(9)) | ((io.INA(23) & io.INB(23)) & io.INS(9))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(8)) | ((io.INA(23) & !io.INB(23)) & io.INB(8)) | ((io.INA(23) & io.INB(23)) & io.INS(8))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(7)) | ((io.INA(23) & !io.INB(23)) & io.INB(7)) | ((io.INA(23) & io.INB(23)) & io.INS(7))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(6)) | ((io.INA(23) & !io.INB(23)) & io.INB(6)) | ((io.INA(23) & io.INB(23)) & io.INS(6))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(5)) | ((io.INA(23) & !io.INB(23)) & io.INB(5)) | ((io.INA(23) & io.INB(23)) & io.INS(5))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(4)) | ((io.INA(23) & !io.INB(23)) & io.INB(4)) | ((io.INA(23) & io.INB(23)) & io.INS(4))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(3)) | ((io.INA(23) & !io.INB(23)) & io.INB(3)) | ((io.INA(23) & io.INB(23)) & io.INS(3))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(2)) | ((io.INA(23) & !io.INB(23)) & io.INB(2)) | ((io.INA(23) & io.INB(23)) & io.INS(2))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(1)) | ((io.INA(23) & !io.INB(23)) & io.INB(1)) | ((io.INA(23) & io.INB(23)) & io.INS(1))) ##
    (((!io.INA(23) & io.INB(23)) & io.INA(0)) | ((io.INA(23) & !io.INB(23)) & io.INB(0)) | ((io.INA(23) & io.INB(23)) & io.INS(0))) )
}

class halfadder extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val S = Output(UInt(1.W))
    val C = Output(UInt(1.W))
  })

  io.S := io.A ^ io.B
  io.C := io.A & io.B
}

class fulladder extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val C = Input(UInt(1.W))
    val R = Output(UInt(1.W))
    val S = Output(UInt(1.W))
  })

  val ta = Wire(UInt(1.W))
  val tb = Wire(UInt(1.W))
  val tc = Wire(UInt(1.W))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)

  HA0.io.A := io.A
  HA0.io.B := io.B
  ta := HA0.io.S
  tb := HA0.io.C

  HA1.io.A := ta
  HA1.io.B := io.C
  io.R := HA1.io.S
  tc := HA1.io.C

  io.S := tb | tc
}

class CSA extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(48.W))
    val B = Input(UInt(48.W))
    val C = Input(UInt(48.W))
    val R = Output(UInt(48.W))
    val S = Output(UInt(48.W))
  })

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)
  val FA3 = Module(new fulladder)
  val FA4 = Module(new fulladder)
  val FA5 = Module(new fulladder)
  val FA6 = Module(new fulladder)
  val FA7 = Module(new fulladder)
  val FA8 = Module(new fulladder)
  val FA9 = Module(new fulladder)
  val FA10 = Module(new fulladder)
  val FA11 = Module(new fulladder)
  val FA12 = Module(new fulladder)
  val FA13 = Module(new fulladder)
  val FA14 = Module(new fulladder)
  val FA15 = Module(new fulladder)
  val FA16 = Module(new fulladder)
  val FA17 = Module(new fulladder)
  val FA18 = Module(new fulladder)
  val FA19 = Module(new fulladder)
  val FA20 = Module(new fulladder)
  val FA21 = Module(new fulladder)
  val FA22 = Module(new fulladder)
  val FA23 = Module(new fulladder)
  val FA24 = Module(new fulladder)
  val FA25 = Module(new fulladder)
  val FA26 = Module(new fulladder)
  val FA27 = Module(new fulladder)
  val FA28 = Module(new fulladder)
  val FA29 = Module(new fulladder)
  val FA30 = Module(new fulladder)
  val FA31 = Module(new fulladder)
  val FA32 = Module(new fulladder)
  val FA33 = Module(new fulladder)
  val FA34 = Module(new fulladder)
  val FA35 = Module(new fulladder)
  val FA36 = Module(new fulladder)
  val FA37 = Module(new fulladder)
  val FA38 = Module(new fulladder)
  val FA39 = Module(new fulladder)
  val FA40 = Module(new fulladder)
  val FA41 = Module(new fulladder)
  val FA42 = Module(new fulladder)
  val FA43 = Module(new fulladder)
  val FA44 = Module(new fulladder)
  val FA45 = Module(new fulladder)
  val FA46 = Module(new fulladder)
  val FA47 = Module(new fulladder)

  io.R := FA47.io.R ## FA46.io.R ## FA45.io.R ## FA44.io.R ## FA43.io.R ## FA42.io.R ## FA41.io.R ## FA40.io.R ##
    FA39.io.R ## FA38.io.R ## FA37.io.R ## FA36.io.R ## FA35.io.R ## FA34.io.R ## FA33.io.R ## FA32.io.R ##
    FA31.io.R ## FA30.io.R ## FA29.io.R ## FA28.io.R ## FA27.io.R ## FA26.io.R ## FA25.io.R ## FA24.io.R ##
    FA23.io.R ## FA22.io.R ## FA21.io.R ## FA20.io.R ## FA19.io.R ## FA18.io.R ## FA17.io.R ## FA16.io.R ##
    FA15.io.R ## FA14.io.R ## FA13.io.R ## FA12.io.R ## FA11.io.R ## FA10.io.R ## FA9.io.R ## FA8.io.R ##
    FA7.io.R ## FA6.io.R ## FA5.io.R ## FA4.io.R ## FA3.io.R ## FA2.io.R ## FA1.io.R ## FA0.io.R

  io.S := FA47.io.S ## FA46.io.S ## FA45.io.S ## FA44.io.S ## FA43.io.S ## FA42.io.S ## FA41.io.S ## FA40.io.S ##
    FA39.io.S ## FA38.io.S ## FA37.io.S ## FA36.io.S ## FA35.io.S ## FA34.io.S ## FA33.io.S ## FA32.io.S ##
    FA31.io.S ## FA30.io.S ## FA29.io.S ## FA28.io.S ## FA27.io.S ## FA26.io.S ## FA25.io.S ## FA24.io.S ##
    FA23.io.S ## FA22.io.S ## FA21.io.S ## FA20.io.S ## FA19.io.S ## FA18.io.S ## FA17.io.S ## FA16.io.S ##
    FA15.io.S ## FA14.io.S ## FA13.io.S ## FA12.io.S ## FA11.io.S ## FA10.io.S ## FA9.io.S ## FA8.io.S ##
    FA7.io.S ## FA6.io.S ## FA5.io.S ## FA4.io.S ## FA3.io.S ## FA2.io.S ## FA1.io.S ## FA0.io.S
  FA0.io.A := io.A(0)
  FA0.io.B := io.B(0)
  FA0.io.C := io.C(0)

  FA1.io.A := io.A(1)
  FA1.io.B := io.B(1)
  FA1.io.C := io.C(1)

  FA2.io.A := io.A(2)
  FA2.io.B := io.B(2)
  FA2.io.C := io.C(2)

  FA3.io.A := io.A(3)
  FA3.io.B := io.B(3)
  FA3.io.C := io.C(3)

  FA4.io.A := io.A(4)
  FA4.io.B := io.B(4)
  FA4.io.C := io.C(4)

  FA5.io.A := io.A(5)
  FA5.io.B := io.B(5)
  FA5.io.C := io.C(5)

  FA6.io.A := io.A(6)
  FA6.io.B := io.B(6)
  FA6.io.C := io.C(6)

  FA7.io.A := io.A(7)
  FA7.io.B := io.B(7)
  FA7.io.C := io.C(7)

  FA8.io.A := io.A(8)
  FA8.io.B := io.B(8)
  FA8.io.C := io.C(8)

  FA9.io.A := io.A(9)
  FA9.io.B := io.B(9)
  FA9.io.C := io.C(9)

  FA10.io.A := io.A(10)
  FA10.io.B := io.B(10)
  FA10.io.C := io.C(10)

  FA11.io.A := io.A(11)
  FA11.io.B := io.B(11)
  FA11.io.C := io.C(11)
  FA12.io.A := io.A(12)
  FA12.io.B := io.B(12)
  FA12.io.C := io.C(12)

  FA13.io.A := io.A(13)
  FA13.io.B := io.B(13)
  FA13.io.C := io.C(13)

  FA14.io.A := io.A(14)
  FA14.io.B := io.B(14)
  FA14.io.C := io.C(14)

  FA15.io.A := io.A(15)
  FA15.io.B := io.B(15)
  FA15.io.C := io.C(15)

  FA16.io.A := io.A(16)
  FA16.io.B := io.B(16)
  FA16.io.C := io.C(16)

  FA17.io.A := io.A(17)
  FA17.io.B := io.B(17)
  FA17.io.C := io.C(17)

  FA18.io.A := io.A(18)
  FA18.io.B := io.B(18)
  FA18.io.C := io.C(18)

  FA19.io.A := io.A(19)
  FA19.io.B := io.B(19)
  FA19.io.C := io.C(19)

  FA20.io.A := io.A(20)
  FA20.io.B := io.B(20)
  FA20.io.C := io.C(20)

  FA21.io.A := io.A(21)
  FA21.io.B := io.B(21)
  FA21.io.C := io.C(21)

  FA22.io.A := io.A(22)
  FA22.io.B := io.B(22)
  FA22.io.C := io.C(22)

  FA23.io.A := io.A(23)
  FA23.io.B := io.B(23)
  FA23.io.C := io.C(23)

  FA24.io.A := io.A(24)
  FA24.io.B := io.B(24)
  FA24.io.C := io.C(24)

  FA25.io.A := io.A(25)
  FA25.io.B := io.B(25)
  FA25.io.C := io.C(25)

  FA26.io.A := io.A(26)
  FA26.io.B := io.B(26)
  FA26.io.C := io.C(26)

  FA27.io.A := io.A(27)
  FA27.io.B := io.B(27)
  FA27.io.C := io.C(27)

  FA28.io.A := io.A(28)
  FA28.io.B := io.B(28)
  FA28.io.C := io.C(28)

  FA29.io.A := io.A(29)
  FA29.io.B := io.B(29)
  FA29.io.C := io.C(29)

  FA30.io.A := io.A(30)
  FA30.io.B := io.B(30)
  FA30.io.C := io.C(30)

  FA31.io.A := io.A(31)
  FA31.io.B := io.B(31)
  FA31.io.C := io.C(31)

  FA32.io.A := io.A(32)
  FA32.io.B := io.B(32)
  FA32.io.C := io.C(32)

  FA33.io.A := io.A(33)
  FA33.io.B := io.B(33)
  FA33.io.C := io.C(33)

  FA34.io.A := io.A(34)
  FA34.io.B := io.B(34)
  FA34.io.C := io.C(34)

  FA35.io.A := io.A(35)
  FA35.io.B := io.B(35)
  FA35.io.C := io.C(35)

  FA36.io.A := io.A(36)
  FA36.io.B := io.B(36)
  FA36.io.C := io.C(36)

  FA37.io.A := io.A(37)
  FA37.io.B := io.B(37)
  FA37.io.C := io.C(37)

  FA38.io.A := io.A(38)
  FA38.io.B := io.B(38)
  FA38.io.C := io.C(38)

  FA39.io.A := io.A(39)
  FA39.io.B := io.B(39)
  FA39.io.C := io.C(39)

  FA40.io.A := io.A(40)
  FA40.io.B := io.B(40)
  FA40.io.C := io.C(40)

  FA41.io.A := io.A(41)
  FA41.io.B := io.B(41)
  FA41.io.C := io.C(41)

  FA42.io.A := io.A(42)
  FA42.io.B := io.B(42)
  FA42.io.C := io.C(42)

  FA43.io.A := io.A(43)
  FA43.io.B := io.B(43)
  FA43.io.C := io.C(43)

  FA44.io.A := io.A(44)
  FA44.io.B := io.B(44)
  FA44.io.C := io.C(44)

  FA45.io.A := io.A(45)
  FA45.io.B := io.B(45)
  FA45.io.C := io.C(45)

  FA46.io.A := io.A(46)
  FA46.io.B := io.B(46)
  FA46.io.C := io.C(46)

  FA47.io.A := io.A(47)
  FA47.io.B := io.B(47)
  FA47.io.C := io.C(47)
}*/