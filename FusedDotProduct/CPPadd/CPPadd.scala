package CPPadd

import chisel3._
import CSA._

class CPPadd extends Module {
  val io = IO(new Bundle {
    val P0 = Input(UInt(1.W))
    val P1 = Input(UInt(3.W))
    val P2 = Input(UInt(4.W))
    val P3 = Input(UInt(5.W))
    val P4 = Input(UInt(6.W))
    val P5 = Input(UInt(7.W))
    val P6 = Input(UInt(8.W))
    val P7 = Input(UInt(9.W))
    val P8 = Input(UInt(10.W))
    val P9 = Input(UInt(11.W))
    val P10 = Input(UInt(12.W))
    val P11 = Input(UInt(13.W))
    val P12 = Input(UInt(14.W))
    val P13 = Input(UInt(15.W))
    val P14 = Input(UInt(16.W))
    val P15 = Input(UInt(17.W))
    val P16 = Input(UInt(18.W))
    val P17 = Input(UInt(19.W))
    val P18 = Input(UInt(20.W))
    val P19 = Input(UInt(21.W))
    val P20 = Input(UInt(22.W))
    val P21 = Input(UInt(23.W))
    val P22 = Input(UInt(24.W))
    val P23 = Input(UInt(25.W))
    val R = Output(UInt(48.W))
    val S = Output(UInt(48.W))
  })

  val zero_vector = 0.U(47.W)
  val Rvec = Wire(Vec(22, UInt(48.W)))
  val Svec = Wire(Vec(22, UInt(48.W)))

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
  /////// CSA19までは通る
  val CSA20 = Module(new CSA)
  val CSA21 = Module(new CSA)

  CSA0.io.A := (zero_vector(46,0) ## io.P0)
  CSA0.io.B := (zero_vector(43,0)  ## io.P1 ## zero_vector(0) )
  CSA0.io.C := (zero_vector(41,0) ## io.P2 ## zero_vector(1,0) )
  Rvec(0) := CSA0.io.R
  Svec(0) := (CSA0.io.S(46,0) ## zero_vector(46))

  CSA1.io.A := (zero_vector(39,0) ## io.P3 ## zero_vector(2,0) )
  CSA1.io.B := (zero_vector(37,0)  ## io.P4 ## zero_vector(3,0) )
  CSA1.io.C := (zero_vector(35,0) ## io.P5 ## zero_vector(4,0) )
  Rvec(1) := CSA1.io.R
  Svec(1) := (CSA1.io.S(46,0) ## zero_vector(45))

  CSA2.io.A := (zero_vector(33,0) ## io.P6 ## zero_vector(5,0) )
  CSA2.io.B := (zero_vector(31,0) ## io.P7 ## zero_vector(6,0) )
  CSA2.io.C := (zero_vector(29,0) ## io.P8 ## zero_vector(7,0) )
  Rvec(2) := CSA2.io.R
  Svec(2) := (CSA2.io.S(46,0) ## zero_vector(44))

  CSA3.io.A := (zero_vector(27,0) ## io.P9 ## zero_vector(8,0) )
  CSA3.io.B := (zero_vector(25,0)  ## io.P10 ## zero_vector(9,0) )
  CSA3.io.C := (zero_vector(23,0) ## io.P11 ## zero_vector(10,0) )
  Rvec(3) := CSA3.io.R
  Svec(3) := (CSA3.io.S(46,0) ## zero_vector(43))

  CSA4.io.A := (zero_vector(21,0) ## io.P12 ## zero_vector(11,0) )
  CSA4.io.B := (zero_vector(19,0) ## io.P13 ## zero_vector(12,0) )
  CSA4.io.C := (zero_vector(17,0) ## io.P14 ## zero_vector(13,0) )
  Rvec(4) := CSA4.io.R
  Svec(4) := (CSA4.io.S(46,0) ## zero_vector(42))

  CSA5.io.A := (zero_vector(15,0) ## io.P15 ## zero_vector(14,0) )
  CSA5.io.B := (zero_vector(13,0) ## io.P16 ## zero_vector(15,0) )
  CSA5.io.C :=(zero_vector(11,0) ## io.P17 ## zero_vector(16,0) )
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

  CSA10.io.A := (zero_vector(9,0) ## io.P18 ## zero_vector(17,0) )
  CSA10.io.B := (zero_vector(7,0) ## io.P19 ## zero_vector(18,0) )
  CSA10.io.C :=(zero_vector(5,0) ## io.P20 ## zero_vector(19,0) )
  Rvec(10) := CSA10.io.R
  Svec(10) := (CSA10.io.S(46,0) ## zero_vector(36))

  CSA11.io.A := (zero_vector(3,0) ## io.P21 ## zero_vector(20,0) )
  CSA11.io.B := (zero_vector(1,0) ## io.P22 ## zero_vector(21,0) )
  CSA11.io.C := (io.P23 ## zero_vector(22,0) )
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

  io.R := Rvec(21)
  io.S := Svec(21)


 // io.R := Rvec(19)
 // io.S := Svec(19)
}
