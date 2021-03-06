package FourTwoCompressor

import chisel3._

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

class compressor extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val C = Input(UInt(1.W))
    val D = Input(UInt(1.W))
    val E = Input(UInt(1.W))
    val R = Output(UInt(1.W))
    val S = Output(UInt(1.W))
    val P = Output(UInt(1.W))
  })

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)

  val carry = Wire(UInt(1.W))

  FA0.io.A := io.A
  FA0.io.B := io.B
  FA0.io.C := io.C
  carry := FA0.io.R
  io.P := FA0.io.S

  FA1.io.A := io.E
  FA1.io.B := carry
  FA1.io.C := io.D
  io.R := FA1.io.R
  io.S := FA1.io.S
}

class HAcompressor extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val C = Input(UInt(1.W))
    val R = Output(UInt(1.W))
    val S = Output(UInt(1.W))
    val P = Output(UInt(1.W))
  })

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)

  val carry = Wire(UInt(1.W))

  HA0.io.A := io.A
  HA0.io.B := io.B
  carry := HA0.io.S
  io.P := HA0.io.C

  HA1.io.A := io.C
  HA1.io.B := carry
  io.R := HA1.io.S
  io.S := HA1.io.C
}

class FourTwoCompressor0 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(3.W))
    val C = Input(UInt(4.W))
    val D = Input(UInt(5.W))
    //val E = Input(UInt(48.W))
    val R = Output(UInt(8.W))
    val S = Output(UInt(4.W))
    //val P = Output(UInt(8.W))
  })

  val vecR = Wire(Vec(8, UInt(1.W)))
  val vecS = Wire(Vec(4, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val FA0 = Module(new fulladder)

  // printf("R = %b\n", io.R)
  // printf("S = %b\n", io.S)

  vecR(0) := io.A
  vecR(1) := io.B(0)

  HA0.io.A := io.B(1)
  HA0.io.B := io.C(0)
  vecR(2) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.B(2)
  FA0.io.B := io.C(1)
  FA0.io.C := io.D(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(3) := HA1.io.S
  vecS(0) := HA1.io.C

  HA2.io.A := io.C(2)
  HA2.io.B := io.D(1)
  vec1(0) := HA2.io.S
  vec1(1) := HA2.io.C

  HA3.io.A := vec0(1)
  HA3.io.B := vec1(0)
  vecR(4) := HA3.io.S
  vecS(1) := HA3.io.C

  HA4.io.A := io.C(3)
  HA4.io.B := io.D(2)
  vec2(0) := HA4.io.S
  vec2(1) := HA4.io.C

  HA5.io.A := vec1(1)
  HA5.io.B := vec2(0)
  vecR(5) := HA5.io.S
  vecS(2) := HA5.io.C

  HA6.io.A := io.D(3)
  HA6.io.B := vec2(1)
  vecR(6) := HA6.io.S
  vecS(3) := HA6.io.C

  vecR(7) := io.D(4)

  io.R := vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  //io.R := io.D(4) ## HA6.io.S ## HA5.io.S ## HA1.io.S ##  ## io.B(0) ## io.A
  io.S := vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor1 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(6.W))
    val B = Input(UInt(7.W))
    val C = Input(UInt(8.W))
    val D = Input(UInt(9.W))
    //val E = Input(UInt(48.W))
    val R = Output(UInt(12.W))
    val S = Output(UInt(9.W))
    //val P = Output(UInt(8.W))
  })

  val vecR = Wire(Vec(12, UInt(1.W)))
  val vecS = Wire(Vec(9, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val vec0 = Wire(Vec(2, UInt(1.W)))
  /*
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
   */
  val vec4 = Wire(Vec(2, UInt(1.W)))
  val vec5 = Wire(Vec(2, UInt(1.W)))
  val vec6 = Wire(Vec(2, UInt(1.W)))
  val vec7 = Wire(Vec(2, UInt(1.W)))
/*
  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)
  val HA8 = Module(new halfadder)
  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)
  val FA3 = Module(new fulladder)
  val FA4 = Module(new fulladder)
  val FA5 = Module(new fulladder)
  val FA6 = Module(new fulladder)
  val FA7 = Module(new fulladder)
  val FA8 = Module(new fulladder)
  printf("R = %b\n", io.R)
  printf("S = %b\n", io.S)

  vecR(0) := io.A(0)

  HA0.io.A := io.A(1)
  HA0.io.B := io.B(0)
  vecR(1) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(2)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(2) := HA1.io.S
  vecS(0) := HA1.io.C

  FA1.io.A := io.B(2)
  FA1.io.B := io.C(1)
  FA1.io.C := io.D(0)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  FA2.io.A := vec0(1)
  FA2.io.B := io.A(3)
  FA2.io.C := vec1(0)
  vecR(3) := FA2.io.R
  vecS(1) := FA2.io.S

  FA3.io.A := io.B(3)
  FA3.io.B := io.C(2)
  FA3.io.C := io.D(1)
  vec2(0) := FA3.io.R
  vec2(1) := FA3.io.S

  FA4.io.A := vec1(1)
  FA4.io.B := io.A(4)
  FA4.io.C := vec2(0)
  vecR(4) := FA4.io.R
  vecS(2) := FA4.io.S

  FA5.io.A := io.B(4)
  FA5.io.B := io.C(3)
  FA5.io.C := io.D(2)
  vec3(0) := FA5.io.R
  vec3(1) := FA5.io.S

  FA6.io.A := vec2(1)
  FA6.io.B := io.A(5)
  FA6.io.C := vec3(0)
  vecR(5) := FA6.io.R
  vecS(3) := FA6.io.S

  FA7.io.A := io.B(5)
  FA7.io.B := io.C(4)
  FA7.io.C := io.D(3)
  vec4(0) := FA7.io.R
  vec4(1) := FA7.io.S

  HA2.io.A := vec3(1)
  HA2.io.B := vec4(0)
  vecR(6) := HA2.io.S
  vecS(4) := HA2.io.C

  FA8.io.A := io.B(6)
  FA8.io.B := io.C(5)
  FA8.io.C := io.D(4)
  vec5(0) := FA8.io.R
  vec5(1) := FA8.io.S

  HA3.io.A := vec4(1)
  HA3.io.B := vec5(0)
  vecR(7) := HA3.io.S
  vecS(5) := HA3.io.C

  HA4.io.A := io.C(6)
  HA4.io.B := io.D(5)
  vec6(0) := HA4.io.S
  vec6(1) := HA4.io.C

  HA5.io.A := vec5(1)
  HA5.io.B := vec6(0)
  vecR(8) := HA5.io.S
  vecS(6) := HA5.io.C

  HA6.io.A := io.C(7)
  HA6.io.B := io.D(6)
  vec7(0) := HA6.io.S
  vec7(1) := HA6.io.C

  HA7.io.A := vec6(1)
  HA7.io.B := vec7(0)
  vecR(9) := HA7.io.S
  vecS(7) := HA7.io.C

  HA8.io.A := vec7(1)
  HA8.io.B := io.D(7)
  vecR(10) := HA8.io.S
  vecS(8) := HA8.io.C

  vecR(11) := io.D(8)

 */

  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)
  val HA8 = Module(new halfadder)
  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)

  HA0.io.A := io.A(1)
  HA0.io.B := io.B(0)
  vecR(1) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(2)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(2) := HA1.io.S
  vecS(0) := HA1.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(1)
  COM0.io.C := io.B(2)
  COM0.io.D := io.A(3)
  COM0.io.E := vec0(1)
  vecR(3) := COM0.io.R
  vecS(1) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(2)
  COM1.io.C := io.B(3)
  COM1.io.D := io.A(4)
  COM1.io.E := C1
  vecR(4) := COM1.io.R
  vecS(2) := COM1.io.S
  C2 := COM0.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(3)
  COM2.io.C := io.B(4)
  COM2.io.D := io.A(5)
  COM2.io.E := C2
  vecR(5) := COM2.io.R
  vecS(3) := COM2.io.S
  C3 := COM2.io.P

  FA1.io.A := io.B(5)
  FA1.io.B := io.C(4)
  FA1.io.C := io.D(3)
  vec4(0) := FA1.io.R
  vec4(1) := FA1.io.S

  HA2.io.A := C3
  HA2.io.B := vec4(0)
  vecR(6) := HA2.io.S
  vecS(4) := HA2.io.C

  FA2.io.A := io.B(6)
  FA2.io.B := io.C(5)
  FA2.io.C := io.D(4)
  vec5(0) := FA2.io.R
  vec5(1) := FA2.io.S

  HA3.io.A := vec4(1)
  HA3.io.B := vec5(0)
  vecR(7) := HA3.io.S
  vecS(5) := HA3.io.C

  HA4.io.A := io.C(6)
  HA4.io.B := io.D(5)
  vec6(0) := HA4.io.S
  vec6(1) := HA4.io.C

  HA5.io.A := vec5(1)
  HA5.io.B := vec6(0)
  vecR(8) := HA5.io.S
  vecS(6) := HA5.io.C

  HA6.io.A := io.C(7)
  HA6.io.B := io.D(6)
  vec7(0) := HA6.io.S
  vec7(1) := HA6.io.C

  HA7.io.A := vec6(1)
  HA7.io.B := vec7(0)
  vecR(9) := HA7.io.S
  vecS(7) := HA7.io.C

  HA8.io.A := vec7(1)
  HA8.io.B := io.D(7)
  vecR(10) := HA8.io.S
  vecS(8) := HA8.io.C

  vecR(11) := io.D(8)

  io.R := vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  //io.R := io.D(4) ## HA6.io.S ## HA5.io.S ## HA1.io.S ##  ## io.B(0) ## io.A
  io.S := vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor2 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(10.W))
    val B = Input(UInt(11.W))
    val C = Input(UInt(12.W))
    val D = Input(UInt(13.W))
    val R = Output(UInt(16.W))
    val S = Output(UInt(13.W))
  })

  val vecR = Wire(Vec(16, UInt(1.W)))
  val vecS = Wire(Vec(13, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
 // val C8 = Wire(UInt(1.W))
  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
  val vec4 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)
  val HA8 = Module(new halfadder)
  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)

  HA0.io.A := io.A(1)
  HA0.io.B := io.B(0)
  vecR(1) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(2)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(2) := HA1.io.S
  vecS(0) := HA1.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(1)
  COM0.io.C := io.B(2)
  COM0.io.D := io.A(3)
  COM0.io.E := vec0(1)
  vecR(3) := COM0.io.R
  vecS(1) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(2)
  COM1.io.C := io.B(3)
  COM1.io.D := io.A(4)
  COM1.io.E := C1
  vecR(4) := COM1.io.R
  vecS(2) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(3)
  COM2.io.C := io.B(4)
  COM2.io.D := io.A(5)
  COM2.io.E := C2
  vecR(5) := COM2.io.R
  vecS(3) := COM2.io.S
  C3 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(4)
  COM3.io.C := io.B(5)
  COM3.io.D := io.A(6)
  COM3.io.E := C3
  vecR(6) := COM3.io.R
  vecS(4) := COM3.io.S
  C4 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(5)
  COM4.io.C := io.B(6)
  COM4.io.D := io.A(7)
  COM4.io.E := C4
  vecR(7) := COM4.io.R
  vecS(5) := COM4.io.S
  C5 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(6)
  COM5.io.C := io.B(7)
  COM5.io.D := io.A(8)
  COM5.io.E := C5
  vecR(8) := COM5.io.R
  vecS(6) := COM5.io.S
  C6 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(7)
  COM6.io.C := io.B(8)
  COM6.io.D := io.A(9)
  COM6.io.E := C6
  vecR(9) := COM6.io.R
  vecS(7) := COM6.io.S
  C7 := COM6.io.P

  FA1.io.A := io.B(9)
  FA1.io.B := io.C(8)
  FA1.io.C := io.D(7)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec1(0)
  HA2.io.B := C7
  vecR(10) := HA2.io.S
  vecS(8) := HA2.io.C

  FA2.io.A := io.B(10)
  FA2.io.B := io.C(9)
  FA2.io.C := io.D(8)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec2(0)
  HA3.io.B := vec1(1)
  vecR(11) := HA3.io.S
  vecS(9) := HA3.io.C

  HA4.io.A := io.C(10)
  HA4.io.B := io.D(9)
  vec3(0) := HA4.io.S
  vec3(1) := HA4.io.C

  HA5.io.A := vec2(1)
  HA5.io.B := vec3(0)
  vecR(12) := HA5.io.S
  vecS(10) := HA5.io.C

  HA6.io.A := io.C(11)
  HA6.io.B := io.D(10)
  vec4(0) := HA6.io.S
  vec4(1) := HA6.io.C

  HA7.io.A := vec3(1)
  HA7.io.B := vec4(0)
  vecR(13) := HA7.io.S
  vecS(11) := HA7.io.C

  HA8.io.A := vec4(1)
  HA8.io.B := io.D(11)
  vecR(14) := HA8.io.S
  vecS(12) := HA8.io.C

  vecR(15) := io.D(12)

  io.R := vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(12) ## vecS(11) ## vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor3 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(14.W))
    val B = Input(UInt(15.W))
    val C = Input(UInt(16.W))
    val D = Input(UInt(17.W))
    val R = Output(UInt(20.W))
    val S = Output(UInt(17.W))
  })

  val vecR = Wire(Vec(20, UInt(1.W)))
  val vecS = Wire(Vec(17, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  // val C8 = Wire(UInt(1.W))
  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
  val vec4 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)
  val HA8 = Module(new halfadder)
  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)
  val COM9 = Module(new compressor)
  val COM10 = Module(new compressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)

  HA0.io.A := io.A(1)
  HA0.io.B := io.B(0)
  vecR(1) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(2)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(2) := HA1.io.S
  vecS(0) := HA1.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(1)
  COM0.io.C := io.B(2)
  COM0.io.D := io.A(3)
  COM0.io.E := vec0(1)
  vecR(3) := COM0.io.R
  vecS(1) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(2)
  COM1.io.C := io.B(3)
  COM1.io.D := io.A(4)
  COM1.io.E := C1
  vecR(4) := COM1.io.R
  vecS(2) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(3)
  COM2.io.C := io.B(4)
  COM2.io.D := io.A(5)
  COM2.io.E := C2
  vecR(5) := COM2.io.R
  vecS(3) := COM2.io.S
  C3 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(4)
  COM3.io.C := io.B(5)
  COM3.io.D := io.A(6)
  COM3.io.E := C3
  vecR(6) := COM3.io.R
  vecS(4) := COM3.io.S
  C4 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(5)
  COM4.io.C := io.B(6)
  COM4.io.D := io.A(7)
  COM4.io.E := C4
  vecR(7) := COM4.io.R
  vecS(5) := COM4.io.S
  C5 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(6)
  COM5.io.C := io.B(7)
  COM5.io.D := io.A(8)
  COM5.io.E := C5
  vecR(8) := COM5.io.R
  vecS(6) := COM5.io.S
  C6 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(7)
  COM6.io.C := io.B(8)
  COM6.io.D := io.A(9)
  COM6.io.E := C6
  vecR(9) := COM6.io.R
  vecS(7) := COM6.io.S
  C7 := COM6.io.P

  COM7.io.A := io.D(7)
  COM7.io.B := io.C(8)
  COM7.io.C := io.B(9)
  COM7.io.D := io.A(10)
  COM7.io.E := C7
  vecR(10) := COM7.io.R
  vecS(8) := COM7.io.S
  C8 := COM7.io.P

  COM8.io.A := io.D(8)
  COM8.io.B := io.C(9)
  COM8.io.C := io.B(10)
  COM8.io.D := io.A(11)
  COM8.io.E := C8
  vecR(11) := COM7.io.R
  vecS(9) := COM7.io.S
  C9 := COM7.io.P

  COM9.io.A := io.D(9)
  COM9.io.B := io.C(10)
  COM9.io.C := io.B(11)
  COM9.io.D := io.A(12)
  COM9.io.E := C9
  vecR(12) := COM9.io.R
  vecS(10) := COM9.io.S
  C10 := COM9.io.P

  COM10.io.A := io.D(10)
  COM10.io.B := io.C(11)
  COM10.io.C := io.B(12)
  COM10.io.D := io.A(13)
  COM10.io.E := C10
  vecR(13) := COM10.io.R
  vecS(11) := COM10.io.S
  C11 := COM10.io.P

  FA1.io.A := io.B(13)
  FA1.io.B := io.C(12)
  FA1.io.C := io.D(11)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec1(0)
  HA2.io.B := C11
  vecR(14) := HA2.io.S
  vecS(12) := HA2.io.C

  FA2.io.A := io.B(14)
  FA2.io.B := io.C(13)
  FA2.io.C := io.D(12)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec2(0)
  HA3.io.B := vec1(1)
  vecR(15) := HA3.io.S
  vecS(13) := HA3.io.C

  HA4.io.A := io.C(14)
  HA4.io.B := io.D(13)
  vec3(0) := HA4.io.S
  vec3(1) := HA4.io.C

  HA5.io.A := vec2(1)
  HA5.io.B := vec3(0)
  vecR(16) := HA5.io.S
  vecS(14) := HA5.io.C

  HA6.io.A := io.C(15)
  HA6.io.B := io.D(14)
  vec4(0) := HA6.io.S
  vec4(1) := HA6.io.C

  HA7.io.A := vec3(1)
  HA7.io.B := vec4(0)
  vecR(17) := HA7.io.S
  vecS(15) := HA7.io.C

  HA8.io.A := vec4(1)
  HA8.io.B := io.D(15)
  vecR(18) := HA8.io.S
  vecS(16) := HA8.io.C

  vecR(19) := io.D(16)

  io.R := vecR(19) ## vecR(18) ## vecR(17) ## vecR(16) ## vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ## vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor4 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(18.W))
    val B = Input(UInt(19.W))
    val C = Input(UInt(20.W))
    val D = Input(UInt(21.W))
    val R = Output(UInt(24.W))
    val S = Output(UInt(21.W))
  })

  val vecR = Wire(Vec(24, UInt(1.W)))
  val vecS = Wire(Vec(21, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  val C12 = Wire(UInt(1.W))
  val C13 = Wire(UInt(1.W))
  val C14 = Wire(UInt(1.W))
  val C15 = Wire(UInt(1.W))
  // val C8 = Wire(UInt(1.W))
  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
  val vec4 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)
  val HA8 = Module(new halfadder)
  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)
  val COM9 = Module(new compressor)
  val COM10 = Module(new compressor)
  val COM11 = Module(new compressor)
  val COM12 = Module(new compressor)
  val COM13 = Module(new compressor)
  val COM14 = Module(new compressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)

  HA0.io.A := io.A(1)
  HA0.io.B := io.B(0)
  vecR(1) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(2)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(2) := HA1.io.S
  vecS(0) := HA1.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(1)
  COM0.io.C := io.B(2)
  COM0.io.D := io.A(3)
  COM0.io.E := vec0(1)
  vecR(3) := COM0.io.R
  vecS(1) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(2)
  COM1.io.C := io.B(3)
  COM1.io.D := io.A(4)
  COM1.io.E := C1
  vecR(4) := COM1.io.R
  vecS(2) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(3)
  COM2.io.C := io.B(4)
  COM2.io.D := io.A(5)
  COM2.io.E := C2
  vecR(5) := COM2.io.R
  vecS(3) := COM2.io.S
  C3 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(4)
  COM3.io.C := io.B(5)
  COM3.io.D := io.A(6)
  COM3.io.E := C3
  vecR(6) := COM3.io.R
  vecS(4) := COM3.io.S
  C4 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(5)
  COM4.io.C := io.B(6)
  COM4.io.D := io.A(7)
  COM4.io.E := C4
  vecR(7) := COM4.io.R
  vecS(5) := COM4.io.S
  C5 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(6)
  COM5.io.C := io.B(7)
  COM5.io.D := io.A(8)
  COM5.io.E := C5
  vecR(8) := COM5.io.R
  vecS(6) := COM5.io.S
  C6 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(7)
  COM6.io.C := io.B(8)
  COM6.io.D := io.A(9)
  COM6.io.E := C6
  vecR(9) := COM6.io.R
  vecS(7) := COM6.io.S
  C7 := COM6.io.P

  COM7.io.A := io.D(7)
  COM7.io.B := io.C(8)
  COM7.io.C := io.B(9)
  COM7.io.D := io.A(10)
  COM7.io.E := C7
  vecR(10) := COM7.io.R
  vecS(8) := COM7.io.S
  C8 := COM7.io.P

  COM8.io.A := io.D(8)
  COM8.io.B := io.C(9)
  COM8.io.C := io.B(10)
  COM8.io.D := io.A(11)
  COM8.io.E := C8
  vecR(11) := COM8.io.R
  vecS(9) := COM8.io.S
  C9 := COM8.io.P

  COM9.io.A := io.D(9)
  COM9.io.B := io.C(10)
  COM9.io.C := io.B(11)
  COM9.io.D := io.A(12)
  COM9.io.E := C9
  vecR(12) := COM9.io.R
  vecS(10) := COM9.io.S
  C10 := COM9.io.P

  COM10.io.A := io.D(10)
  COM10.io.B := io.C(11)
  COM10.io.C := io.B(12)
  COM10.io.D := io.A(13)
  COM10.io.E := C10
  vecR(13) := COM10.io.R
  vecS(11) := COM10.io.S
  C11 := COM10.io.P

  COM11.io.A := io.D(11)
  COM11.io.B := io.C(12)
  COM11.io.C := io.B(13)
  COM11.io.D := io.A(14)
  COM11.io.E := C11
  vecR(14) := COM11.io.R
  vecS(12) := COM11.io.S
  C12 := COM11.io.P

  COM12.io.A := io.D(12)
  COM12.io.B := io.C(13)
  COM12.io.C := io.B(14)
  COM12.io.D := io.A(15)
  COM12.io.E := C12
  vecR(15) := COM12.io.R
  vecS(13) := COM12.io.S
  C13 := COM12.io.P

  COM13.io.A := io.D(13)
  COM13.io.B := io.C(14)
  COM13.io.C := io.B(15)
  COM13.io.D := io.A(16)
  COM13.io.E := C13
  vecR(16) := COM13.io.R
  vecS(14) := COM13.io.S
  C14 := COM13.io.P

  COM14.io.A := io.D(14)
  COM14.io.B := io.C(15)
  COM14.io.C := io.B(16)
  COM14.io.D := io.A(17)
  COM14.io.E := C14
  vecR(17) := COM14.io.R
  vecS(15) := COM14.io.S
  C15 := COM14.io.P

  FA1.io.A := io.B(17)
  FA1.io.B := io.C(16)
  FA1.io.C := io.D(15)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec1(0)
  HA2.io.B := C15
  vecR(18) := HA2.io.S
  vecS(16) := HA2.io.C

  FA2.io.A := io.B(18)
  FA2.io.B := io.C(17)
  FA2.io.C := io.D(16)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec2(0)
  HA3.io.B := vec1(1)
  vecR(19) := HA3.io.S
  vecS(17) := HA3.io.C

  HA4.io.A := io.C(18)
  HA4.io.B := io.D(17)
  vec3(0) := HA4.io.S
  vec3(1) := HA4.io.C

  HA5.io.A := vec2(1)
  HA5.io.B := vec3(0)
  vecR(20) := HA5.io.S
  vecS(18) := HA5.io.C

  HA6.io.A := io.C(19)
  HA6.io.B := io.D(18)
  vec4(0) := HA6.io.S
  vec4(1) := HA6.io.C

  HA7.io.A := vec3(1)
  HA7.io.B := vec4(0)
  vecR(21) := HA7.io.S
  vecS(19) := HA7.io.C

  HA8.io.A := vec4(1)
  HA8.io.B := io.D(19)
  vecR(22) := HA8.io.S
  vecS(20) := HA8.io.C

  vecR(23) := io.D(20)

  io.R := vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ##
    vecR(16) ## vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ##
    vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(20) ## vecS(19) ## vecS(18) ## vecS(17) ## vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ##
    vecS(12) ## vecS(11) ## vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ##
    vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor5 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(22.W))
    val B = Input(UInt(23.W))
    val C = Input(UInt(24.W))
    val D = Input(UInt(25.W))
    val R = Output(UInt(28.W))
    val S = Output(UInt(25.W))
  })

  val vecR = Wire(Vec(28, UInt(1.W)))
  val vecS = Wire(Vec(25, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  val C12 = Wire(UInt(1.W))
  val C13 = Wire(UInt(1.W))
  val C14 = Wire(UInt(1.W))
  val C15 = Wire(UInt(1.W))
  val C16 = Wire(UInt(1.W))
  val C17 = Wire(UInt(1.W))
  val C18 = Wire(UInt(1.W))
  val C19 = Wire(UInt(1.W))
  // val C8 = Wire(UInt(1.W))
  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
  val vec4 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)
  val HA8 = Module(new halfadder)
  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)
  val COM9 = Module(new compressor)
  val COM10 = Module(new compressor)
  val COM11 = Module(new compressor)
  val COM12 = Module(new compressor)
  val COM13 = Module(new compressor)
  val COM14 = Module(new compressor)
  val COM15 = Module(new compressor)
  val COM16 = Module(new compressor)
  val COM17 = Module(new compressor)
  val COM18 = Module(new compressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)

  HA0.io.A := io.A(1)
  HA0.io.B := io.B(0)
  vecR(1) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(2)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(2) := HA1.io.S
  vecS(0) := HA1.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(1)
  COM0.io.C := io.B(2)
  COM0.io.D := io.A(3)
  COM0.io.E := vec0(1)
  vecR(3) := COM0.io.R
  vecS(1) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(2)
  COM1.io.C := io.B(3)
  COM1.io.D := io.A(4)
  COM1.io.E := C1
  vecR(4) := COM1.io.R
  vecS(2) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(3)
  COM2.io.C := io.B(4)
  COM2.io.D := io.A(5)
  COM2.io.E := C2
  vecR(5) := COM2.io.R
  vecS(3) := COM2.io.S
  C3 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(4)
  COM3.io.C := io.B(5)
  COM3.io.D := io.A(6)
  COM3.io.E := C3
  vecR(6) := COM3.io.R
  vecS(4) := COM3.io.S
  C4 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(5)
  COM4.io.C := io.B(6)
  COM4.io.D := io.A(7)
  COM4.io.E := C4
  vecR(7) := COM4.io.R
  vecS(5) := COM4.io.S
  C5 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(6)
  COM5.io.C := io.B(7)
  COM5.io.D := io.A(8)
  COM5.io.E := C5
  vecR(8) := COM5.io.R
  vecS(6) := COM5.io.S
  C6 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(7)
  COM6.io.C := io.B(8)
  COM6.io.D := io.A(9)
  COM6.io.E := C6
  vecR(9) := COM6.io.R
  vecS(7) := COM6.io.S
  C7 := COM6.io.P

  COM7.io.A := io.D(7)
  COM7.io.B := io.C(8)
  COM7.io.C := io.B(9)
  COM7.io.D := io.A(10)
  COM7.io.E := C7
  vecR(10) := COM7.io.R
  vecS(8) := COM7.io.S
  C8 := COM7.io.P

  COM8.io.A := io.D(8)
  COM8.io.B := io.C(9)
  COM8.io.C := io.B(10)
  COM8.io.D := io.A(11)
  COM8.io.E := C8
  vecR(11) := COM8.io.R
  vecS(9) := COM8.io.S
  C9 := COM8.io.P

  COM9.io.A := io.D(9)
  COM9.io.B := io.C(10)
  COM9.io.C := io.B(11)
  COM9.io.D := io.A(12)
  COM9.io.E := C9
  vecR(12) := COM9.io.R
  vecS(10) := COM9.io.S
  C10 := COM9.io.P

  COM10.io.A := io.D(10)
  COM10.io.B := io.C(11)
  COM10.io.C := io.B(12)
  COM10.io.D := io.A(13)
  COM10.io.E := C10
  vecR(13) := COM10.io.R
  vecS(11) := COM10.io.S
  C11 := COM10.io.P

  COM11.io.A := io.D(11)
  COM11.io.B := io.C(12)
  COM11.io.C := io.B(13)
  COM11.io.D := io.A(14)
  COM11.io.E := C11
  vecR(14) := COM11.io.R
  vecS(12) := COM11.io.S
  C12 := COM11.io.P

  COM12.io.A := io.D(12)
  COM12.io.B := io.C(13)
  COM12.io.C := io.B(14)
  COM12.io.D := io.A(15)
  COM12.io.E := C12
  vecR(15) := COM12.io.R
  vecS(13) := COM12.io.S
  C13 := COM12.io.P

  COM13.io.A := io.D(13)
  COM13.io.B := io.C(14)
  COM13.io.C := io.B(15)
  COM13.io.D := io.A(16)
  COM13.io.E := C13
  vecR(16) := COM13.io.R
  vecS(14) := COM13.io.S
  C14 := COM13.io.P

  COM14.io.A := io.D(14)
  COM14.io.B := io.C(15)
  COM14.io.C := io.B(16)
  COM14.io.D := io.A(17)
  COM14.io.E := C14
  vecR(17) := COM14.io.R
  vecS(15) := COM14.io.S
  C15 := COM14.io.P

  COM15.io.A := io.D(15)
  COM15.io.B := io.C(16)
  COM15.io.C := io.B(17)
  COM15.io.D := io.A(18)
  COM15.io.E := C15
  vecR(18) := COM15.io.R
  vecS(16) := COM15.io.S
  C16 := COM15.io.P

  COM16.io.A := io.D(16)
  COM16.io.B := io.C(17)
  COM16.io.C := io.B(18)
  COM16.io.D := io.A(19)
  COM16.io.E := C16
  vecR(19) := COM16.io.R
  vecS(17) := COM16.io.S
  C17 := COM16.io.P

  COM17.io.A := io.D(17)
  COM17.io.B := io.C(18)
  COM17.io.C := io.B(19)
  COM17.io.D := io.A(20)
  COM17.io.E := C17
  vecR(20) := COM17.io.R
  vecS(18) := COM17.io.S
  C18 := COM17.io.P

  COM18.io.A := io.D(18)
  COM18.io.B := io.C(19)
  COM18.io.C := io.B(20)
  COM18.io.D := io.A(21)
  COM18.io.E := C18
  vecR(21) := COM18.io.R
  vecS(19) := COM18.io.S
  C19 := COM18.io.P

  FA1.io.A := io.B(21)
  FA1.io.B := io.C(20)
  FA1.io.C := io.D(19)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec1(0)
  HA2.io.B := C19
  vecR(22) := HA2.io.S
  vecS(20) := HA2.io.C

  FA2.io.A := io.B(22)
  FA2.io.B := io.C(21)
  FA2.io.C := io.D(20)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec2(0)
  HA3.io.B := vec1(1)
  vecR(23) := HA3.io.S
  vecS(21) := HA3.io.C

  HA4.io.A := io.C(22)
  HA4.io.B := io.D(21)
  vec3(0) := HA4.io.S
  vec3(1) := HA4.io.C

  HA5.io.A := vec2(1)
  HA5.io.B := vec3(0)
  vecR(24) := HA5.io.S
  vecS(22) := HA5.io.C

  HA6.io.A := io.C(23)
  HA6.io.B := io.D(22)
  vec4(0) := HA6.io.S
  vec4(1) := HA6.io.C

  HA7.io.A := vec3(1)
  HA7.io.B := vec4(0)
  vecR(25) := HA7.io.S
  vecS(23) := HA7.io.C

  HA8.io.A := vec4(1)
  HA8.io.B := io.D(23)
  vecR(26) := HA8.io.S
  vecS(24) := HA8.io.C

  vecR(27) := io.D(24)

  io.R := vecR(27) ## vecR(26) ## vecR(25) ## vecR(24) ##
    vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ##
    vecR(16) ## vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ##
    vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(24) ## vecS(23) ## vecS(22) ## vecS(21) ## vecS(20) ## vecS(19) ## vecS(18) ## vecS(17) ##
    vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ## vecS(10) ## vecS(9) ## vecS(8) ##
    vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor6 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(8.W))
    val B = Input(UInt(4.W))
    val C = Input(UInt(12.W))
    val D = Input(UInt(9.W))
    val R = Output(UInt(16.W))
    val S = Output(UInt(11.W))
  })

  val vecR = Wire(Vec(16, UInt(1.W)))
  val vecS = Wire(Vec(11, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))

  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)

  val HACOM0 = Module(new HAcompressor)
  val HACOM1 = Module(new HAcompressor)
  val HACOM2 = Module(new HAcompressor)
  val HACOM3 = Module(new HAcompressor)
  val HACOM4 = Module(new HAcompressor)
  val HACOM5 = Module(new HAcompressor)
  val HACOM6 = Module(new HAcompressor)
  val HACOM7 = Module(new HAcompressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)
  vecR(1) := io.A(1)
  vecR(2) := io.A(2)
  vecR(3) := io.A(3)

  FA0.io.A := io.A(4)
  FA0.io.B := io.B(0)
  FA0.io.C := io.C(0)
  vecR(4) := FA0.io.R
  C0 := FA0.io.S

  FA1.io.A := io.A(5)
  FA1.io.B := io.B(1)
  FA1.io.C := io.C(1)
  vec0(0) := FA1.io.R
  vec0(1) := FA1.io.S

  HA0.io.A := C0
  HA0.io.B := vec0(0)
  vecR(5) := HA0.io.S
  vecS(0) := HA0.io.C

  FA2.io.A := io.A(6)
  FA2.io.B := io.B(2)
  FA2.io.C := io.C(2)
  vec1(0) := FA2.io.R
  vec1(1) := FA2.io.S

  HA1.io.A := vec0(1)
  HA1.io.B := vec1(0)
  vecR(6) := HA1.io.S
  vecS(1) := HA1.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(3)
  COM0.io.C := io.B(3)
  COM0.io.D := io.A(7)
  COM0.io.E := vec1(1)
  vecR(7) := COM0.io.R
  vecS(2) := COM0.io.S
  C1 := COM0.io.P

  HACOM0.io.A := io.D(1)
  HACOM0.io.B := io.C(4)
  HACOM0.io.C := C1
  vecR(8) := HACOM0.io.R
  vecS(3) := HACOM0.io.S
  C2 := HACOM0.io.P

  HACOM1.io.A := io.D(2)
  HACOM1.io.B := io.C(5)
  HACOM1.io.C := C2
  vecR(9) := HACOM1.io.R
  vecS(4) := HACOM1.io.S
  C3 := HACOM1.io.P

  HACOM2.io.A := io.D(3)
  HACOM2.io.B := io.C(6)
  HACOM2.io.C := C3
  vecR(10) := HACOM2.io.R
  vecS(5) := HACOM2.io.S
  C4 := HACOM2.io.P

  HACOM3.io.A := io.D(4)
  HACOM3.io.B := io.C(7)
  HACOM3.io.C := C4
  vecR(11) := HACOM3.io.R
  vecS(6) := HACOM3.io.S
  C5 := HACOM3.io.P

  HACOM4.io.A := io.D(5)
  HACOM4.io.B := io.C(8)
  HACOM4.io.C := C5
  vecR(12) := HACOM4.io.R
  vecS(7) := HACOM4.io.S
  C6 := HACOM4.io.P

  HACOM5.io.A := io.D(6)
  HACOM5.io.B := io.C(9)
  HACOM5.io.C := C6
  vecR(13) := HACOM5.io.R
  vecS(8) := HACOM5.io.S
  C7 := HACOM5.io.P

  HACOM6.io.A := io.D(7)
  HACOM6.io.B := io.C(10)
  HACOM6.io.C := C7
  vecR(14) := HACOM6.io.R
  vecS(9) := HACOM6.io.S
  C8 := HACOM6.io.P

  HACOM7.io.A := io.D(8)
  HACOM7.io.B := io.C(11)
  HACOM7.io.C := C8
  vecR(15) := HACOM7.io.R
  vecS(10) := HACOM7.io.S
  C9 := HACOM7.io.P

  io.R := vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ##
    vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor7 extends Module {
  val io = IO(new Bundle {
  val A = Input(UInt(16.W))
  val B = Input(UInt(13.W))
  val C = Input(UInt(20.W))
  val D = Input(UInt(17.W))
  val R = Output(UInt(24.W))
  val S = Output(UInt(20.W))
})

  val vecR = Wire(Vec(24, UInt(1.W)))
  val vecS = Wire(Vec(20, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  val C12 = Wire(UInt(1.W))
  val C13 = Wire(UInt(1.W))
  val C14 = Wire(UInt(1.W))
  val C15 = Wire(UInt(1.W))
  val C16 = Wire(UInt(1.W))
  val C17 = Wire(UInt(1.W))

  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)


  val HACOM0 = Module(new HAcompressor)
  val HACOM1 = Module(new HAcompressor)
  val HACOM2 = Module(new HAcompressor)
  val HACOM3 = Module(new HAcompressor)
  val HACOM4 = Module(new HAcompressor)
  val HACOM5 = Module(new HAcompressor)
  val HACOM6 = Module(new HAcompressor)
  val HACOM7 = Module(new HAcompressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)
  vecR(1) := io.A(1)
  vecR(2) := io.A(2)

  HA0.io.A := io.A(3)
  HA0.io.B := io.B(0)
  vecR(3) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(4)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(4) := HA1.io.S
  vecS(0) := HA1.io.C

  FA1.io.A := io.A(5)
  FA1.io.B := io.B(2)
  FA1.io.C := io.C(1)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec0(1)
  HA2.io.B := vec1(0)
  vecR(5) := HA2.io.S
  vecS(1) := HA2.io.C

  FA2.io.A := io.A(6)
  FA2.io.B := io.B(3)
  FA2.io.C := io.C(2)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec1(1)
  HA3.io.B := vec2(0)
  vecR(6) := HA3.io.S
  vecS(2) := HA3.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(3)
  COM0.io.C := io.B(4)
  COM0.io.D := io.A(7)
  COM0.io.E := vec2(1)
  vecR(7) := COM0.io.R
  vecS(3) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(4)
  COM1.io.C := io.B(5)
  COM1.io.D := io.A(8)
  COM1.io.E := C1
  vecR(8) := COM1.io.R
  vecS(4) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(5)
  COM2.io.C := io.B(6)
  COM2.io.D := io.A(9)
  COM2.io.E := C2
  vecR(9) := COM2.io.R
  vecS(5) := COM2.io.S
  C3 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(6)
  COM3.io.C := io.B(7)
  COM3.io.D := io.A(10)
  COM3.io.E := C3
  vecR(10) := COM3.io.R
  vecS(6) := COM3.io.S
  C4 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(7)
  COM4.io.C := io.B(8)
  COM4.io.D := io.A(11)
  COM4.io.E := C4
  vecR(11) := COM4.io.R
  vecS(7) := COM4.io.S
  C5 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(8)
  COM5.io.C := io.B(9)
  COM5.io.D := io.A(12)
  COM5.io.E := C5
  vecR(12) := COM5.io.R
  vecS(8) := COM5.io.S
  C6 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(9)
  COM6.io.C := io.B(10)
  COM6.io.D := io.A(13)
  COM6.io.E := C6
  vecR(13) := COM6.io.R
  vecS(9) := COM6.io.S
  C7 := COM6.io.P

  COM7.io.A := io.D(7)
  COM7.io.B := io.C(10)
  COM7.io.C := io.B(11)
  COM7.io.D := io.A(14)
  COM7.io.E := C7
  vecR(14) := COM7.io.R
  vecS(10) := COM7.io.S
  C8 := COM7.io.P

  COM8.io.A := io.D(8)
  COM8.io.B := io.C(11)
  COM8.io.C := io.B(12)
  COM8.io.D := io.A(15)
  COM8.io.E := C8
  vecR(15) := COM8.io.R
  vecS(11) := COM8.io.S
  C9 := COM8.io.P

  HACOM0.io.A := io.D(9)
  HACOM0.io.B := io.C(12)
  HACOM0.io.C := C9
  vecR(16) := HACOM0.io.R
  vecS(12) := HACOM0.io.S
  C10 := HACOM0.io.P

  HACOM1.io.A := io.D(10)
  HACOM1.io.B := io.C(13)
  HACOM1.io.C := C10
  vecR(17) := HACOM1.io.R
  vecS(13) := HACOM1.io.S
  C11 := HACOM1.io.P

  HACOM2.io.A := io.D(11)
  HACOM2.io.B := io.C(14)
  HACOM2.io.C := C11
  vecR(18) := HACOM2.io.R
  vecS(14) := HACOM2.io.S
  C12 := HACOM2.io.P

  HACOM3.io.A := io.D(12)
  HACOM3.io.B := io.C(15)
  HACOM3.io.C := C12
  vecR(19) := HACOM3.io.R
  vecS(15) := HACOM3.io.S
  C13 := HACOM3.io.P

  HACOM4.io.A := io.D(13)
  HACOM4.io.B := io.C(16)
  HACOM4.io.C := C13
  vecR(20) := HACOM4.io.R
  vecS(16) := HACOM4.io.S
  C14 := HACOM4.io.P

  HACOM5.io.A := io.D(14)
  HACOM5.io.B := io.C(17)
  HACOM5.io.C := C14
  vecR(21) := HACOM5.io.R
  vecS(17) := HACOM5.io.S
  C15 := HACOM5.io.P

  HACOM6.io.A := io.D(15)
  HACOM6.io.B := io.C(18)
  HACOM6.io.C := C15
  vecR(22) := HACOM6.io.R
  vecS(18) := HACOM6.io.S
  C16 := HACOM6.io.P

  HACOM7.io.A := io.D(16)
  HACOM7.io.B := io.C(19)
  HACOM7.io.C := C16
  vecR(23) := HACOM7.io.R
  vecS(19) := HACOM7.io.S
  C17 := HACOM7.io.P

  io.R := vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ##
    vecR(16) ## vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ##
    vecR(8) ## vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(19) ## vecS(18) ## vecS(17) ## vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ##
    vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor8 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(24.W))
    val B = Input(UInt(21.W))
    val C = Input(UInt(28.W))
    val D = Input(UInt(25.W))
    val R = Output(UInt(32.W))
    val S = Output(UInt(28.W))
  })

  val vecR = Wire(Vec(32, UInt(1.W)))
  val vecS = Wire(Vec(28, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  val C12 = Wire(UInt(1.W))
  val C13 = Wire(UInt(1.W))
  val C14 = Wire(UInt(1.W))
  val C15 = Wire(UInt(1.W))
  val C16 = Wire(UInt(1.W))
  val C17 = Wire(UInt(1.W))
  val C18 = Wire(UInt(1.W))
  val C19 = Wire(UInt(1.W))
  val C20 = Wire(UInt(1.W))
  val C21 = Wire(UInt(1.W))
  val C22 = Wire(UInt(1.W))
  val C23 = Wire(UInt(1.W))
  val C24 = Wire(UInt(1.W))
  val C25 = Wire(UInt(1.W))

  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))

  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)
  val COM9 = Module(new compressor)
  val COM10 = Module(new compressor)
  val COM11 = Module(new compressor)
  val COM12 = Module(new compressor)
  val COM13 = Module(new compressor)
  val COM14 = Module(new compressor)
  val COM15 = Module(new compressor)
  val COM16 = Module(new compressor)



  val HACOM0 = Module(new HAcompressor)
  val HACOM1 = Module(new HAcompressor)
  val HACOM2 = Module(new HAcompressor)
  val HACOM3 = Module(new HAcompressor)
  val HACOM4 = Module(new HAcompressor)
  val HACOM5 = Module(new HAcompressor)
  val HACOM6 = Module(new HAcompressor)
  val HACOM7 = Module(new HAcompressor)
  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)
  vecR(1) := io.A(1)
  vecR(2) := io.A(2)

  HA0.io.A := io.A(3)
  HA0.io.B := io.B(0)
  vecR(3) := HA0.io.S
  C0 := HA0.io.C

  FA0.io.A := io.A(4)
  FA0.io.B := io.B(1)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C0
  HA1.io.B := vec0(0)
  vecR(4) := HA1.io.S
  vecS(0) := HA1.io.C

  FA1.io.A := io.A(5)
  FA1.io.B := io.B(2)
  FA1.io.C := io.C(1)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec0(1)
  HA2.io.B := vec1(0)
  vecR(5) := HA2.io.S
  vecS(1) := HA2.io.C

  FA2.io.A := io.A(6)
  FA2.io.B := io.B(3)
  FA2.io.C := io.C(2)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec1(1)
  HA3.io.B := vec2(0)
  vecR(6) := HA3.io.S
  vecS(2) := HA3.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(3)
  COM0.io.C := io.B(4)
  COM0.io.D := io.A(7)
  COM0.io.E := vec2(1)
  vecR(7) := COM0.io.R
  vecS(3) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(4)
  COM1.io.C := io.B(5)
  COM1.io.D := io.A(8)
  COM1.io.E := C1
  vecR(8) := COM1.io.R
  vecS(4) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(5)
  COM2.io.C := io.B(6)
  COM2.io.D := io.A(9)
  COM2.io.E := C2
  vecR(9) := COM2.io.R
  vecS(5) := COM2.io.S
  C3 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(6)
  COM3.io.C := io.B(7)
  COM3.io.D := io.A(10)
  COM3.io.E := C3
  vecR(10) := COM3.io.R
  vecS(6) := COM3.io.S
  C4 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(7)
  COM4.io.C := io.B(8)
  COM4.io.D := io.A(11)
  COM4.io.E := C4
  vecR(11) := COM4.io.R
  vecS(7) := COM4.io.S
  C5 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(8)
  COM5.io.C := io.B(9)
  COM5.io.D := io.A(12)
  COM5.io.E := C5
  vecR(12) := COM5.io.R
  vecS(8) := COM5.io.S
  C6 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(9)
  COM6.io.C := io.B(10)
  COM6.io.D := io.A(13)
  COM6.io.E := C6
  vecR(13) := COM6.io.R
  vecS(9) := COM6.io.S
  C7 := COM6.io.P

  COM7.io.A := io.D(7)
  COM7.io.B := io.C(10)
  COM7.io.C := io.B(11)
  COM7.io.D := io.A(14)
  COM7.io.E := C7
  vecR(14) := COM7.io.R
  vecS(10) := COM7.io.S
  C8 := COM7.io.P

  COM8.io.A := io.D(8)
  COM8.io.B := io.C(11)
  COM8.io.C := io.B(12)
  COM8.io.D := io.A(15)
  COM8.io.E := C8
  vecR(15) := COM8.io.R
  vecS(11) := COM8.io.S
  C9 := COM8.io.P

  COM9.io.A := io.D(9)
  COM9.io.B := io.C(12)
  COM9.io.C := io.B(13)
  COM9.io.D := io.A(16)
  COM9.io.E := C9
  vecR(16) := COM9.io.R
  vecS(12) := COM9.io.S
  C10 := COM9.io.P

  COM10.io.A := io.D(10)
  COM10.io.B := io.C(13)
  COM10.io.C := io.B(14)
  COM10.io.D := io.A(17)
  COM10.io.E := C10
  vecR(17) := COM10.io.R
  vecS(13) := COM10.io.S
  C11 := COM10.io.P

  COM11.io.A := io.D(11)
  COM11.io.B := io.C(14)
  COM11.io.C := io.B(15)
  COM11.io.D := io.A(18)
  COM11.io.E := C11
  vecR(18) := COM11.io.R
  vecS(14) := COM11.io.S
  C12 := COM11.io.P

  COM12.io.A := io.D(12)
  COM12.io.B := io.C(15)
  COM12.io.C := io.B(16)
  COM12.io.D := io.A(19)
  COM12.io.E := C12
  vecR(19) := COM12.io.R
  vecS(15) := COM12.io.S
  C13 := COM12.io.P

  COM13.io.A := io.D(13)
  COM13.io.B := io.C(16)
  COM13.io.C := io.B(17)
  COM13.io.D := io.A(20)
  COM13.io.E := C13
  vecR(20) := COM13.io.R
  vecS(16) := COM13.io.S
  C14 := COM13.io.P

  COM14.io.A := io.D(14)
  COM14.io.B := io.C(17)
  COM14.io.C := io.B(18)
  COM14.io.D := io.A(21)
  COM14.io.E := C14
  vecR(21) := COM14.io.R
  vecS(17) := COM14.io.S
  C15 := COM14.io.P

  COM15.io.A := io.D(15)
  COM15.io.B := io.C(18)
  COM15.io.C := io.B(19)
  COM15.io.D := io.A(22)
  COM15.io.E := C15
  vecR(22) := COM15.io.R
  vecS(18) := COM15.io.S
  C16 := COM15.io.P

  COM16.io.A := io.D(16)
  COM16.io.B := io.C(19)
  COM16.io.C := io.B(20)
  COM16.io.D := io.A(23)
  COM16.io.E := C16
  vecR(23) := COM16.io.R
  vecS(19) := COM16.io.S
  C17 := COM16.io.P

  HACOM0.io.A := io.D(17)
  HACOM0.io.B := io.C(20)
  HACOM0.io.C := C17
  vecR(24) := HACOM0.io.R
  vecS(20) := HACOM0.io.S
  C18 := HACOM0.io.P

  HACOM1.io.A := io.D(18)
  HACOM1.io.B := io.C(21)
  HACOM1.io.C := C18
  vecR(25) := HACOM1.io.R
  vecS(21) := HACOM1.io.S
  C19 := HACOM1.io.P

  HACOM2.io.A := io.D(19)
  HACOM2.io.B := io.C(22)
  HACOM2.io.C := C19
  vecR(26) := HACOM2.io.R
  vecS(22) := HACOM2.io.S
  C20 := HACOM2.io.P

  HACOM3.io.A := io.D(20)
  HACOM3.io.B := io.C(23)
  HACOM3.io.C := C20
  vecR(27) := HACOM3.io.R
  vecS(23) := HACOM3.io.S
  C21 := HACOM3.io.P

  HACOM4.io.A := io.D(21)
  HACOM4.io.B := io.C(24)
  HACOM4.io.C := C21
  vecR(28) := HACOM4.io.R
  vecS(24) := HACOM4.io.S
  C22 := HACOM4.io.P

  HACOM5.io.A := io.D(22)
  HACOM5.io.B := io.C(25)
  HACOM5.io.C := C22
  vecR(29) := HACOM5.io.R
  vecS(25) := HACOM5.io.S
  C23 := HACOM5.io.P

  HACOM6.io.A := io.D(23)
  HACOM6.io.B := io.C(26)
  HACOM6.io.C := C23
  vecR(30) := HACOM6.io.R
  vecS(26) := HACOM6.io.S
  C24 := HACOM6.io.P

  HACOM7.io.A := io.D(24)
  HACOM7.io.B := io.C(27)
  HACOM7.io.C := C24
  vecR(31) := HACOM7.io.R
  vecS(27) := HACOM7.io.S
  C25 := HACOM7.io.P

  io.R := vecR(31) ## vecR(30) ## vecR(29) ## vecR(28) ## vecR(27) ## vecR(26) ## vecR(25) ## vecR(24) ##
    vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ## vecR(16) ##
    vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ##
    vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S :=  vecS(27) ## vecS(26) ## vecS(25) ## vecS(24) ## vecS(23) ## vecS(22) ## vecS(21) ## vecS(20) ##
    vecS(19) ## vecS(18) ## vecS(17) ## vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ##
    vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor9 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(16.W))
    val B = Input(UInt(11.W))
    val C = Input(UInt(24.W))
    val D = Input(UInt(20.W))
    val R = Output(UInt(32.W))
    val S = Output(UInt(25.W))
  })

  val vecR = Wire(Vec(32, UInt(1.W)))
  val vecS = Wire(Vec(25, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  val C12 = Wire(UInt(1.W))
  val C13 = Wire(UInt(1.W))
  val C14 = Wire(UInt(1.W))
  val C15 = Wire(UInt(1.W))
  val C16 = Wire(UInt(1.W))
  val C17 = Wire(UInt(1.W))
  val C18 = Wire(UInt(1.W))
  val C19 = Wire(UInt(1.W))

  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
  val vec4 = Wire(Vec(2, UInt(1.W)))
  val vec5 = Wire(Vec(2, UInt(1.W)))


  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)
  val HA6 = Module(new halfadder)
  val HA7 = Module(new halfadder)

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)
  val FA3 = Module(new fulladder)
  val FA4 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)

  val HACOM0 = Module(new HAcompressor)
  val HACOM1 = Module(new HAcompressor)
  val HACOM2 = Module(new HAcompressor)
  val HACOM3 = Module(new HAcompressor)
  val HACOM4 = Module(new HAcompressor)
  val HACOM5 = Module(new HAcompressor)
  val HACOM6 = Module(new HAcompressor)
  val HACOM7 = Module(new HAcompressor)
  val HACOM8 = Module(new HAcompressor)
  val HACOM9 = Module(new HAcompressor)
  val HACOM10 = Module(new HAcompressor)
  val HACOM11 = Module(new HAcompressor)
  val HACOM12 = Module(new HAcompressor)
  val HACOM13 = Module(new HAcompressor)
  val HACOM14 = Module(new HAcompressor)
  val HACOM15 = Module(new HAcompressor)

  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)
  vecR(1) := io.A(1)
  vecR(2) := io.A(2)
  vecR(3) := io.A(3)
  vecR(4) := io.A(4)
  vecR(5) := io.A(5)

  HA0.io.A := io.A(6)
  HA0.io.B := io.B(0)
  vecR(6) := HA0.io.S
  C0 := HA0.io.C

  HA1.io.A := io.A(7)
  HA1.io.B := io.B(1)
  vec0(0) := HA1.io.S
  vec0(1) := HA1.io.C

  HA2.io.A := C0
  HA2.io.B := vec0(0)
  vecR(7) := HA2.io.S
  vecS(0) := HA2.io.C

  FA0.io.A := io.A(8)
  FA0.io.B := io.B(2)
  FA0.io.C := io.C(0)
  vec1(0) := FA0.io.R
  vec1(1) := FA0.io.S

  HA3.io.A := vec0(1)
  HA3.io.B := vec1(0)
  vecR(8) := HA3.io.S
  vecS(1) := HA3.io.C

  FA1.io.A := io.A(9)
  FA1.io.B := io.B(3)
  FA1.io.C := io.C(1)
  vec2(0) := FA1.io.R
  vec2(1) := FA1.io.S

  HA4.io.A := vec1(1)
  HA4.io.B := vec2(0)
  vecR(9) := HA4.io.S
  vecS(2) := HA4.io.C

  FA2.io.A := io.A(10)
  FA2.io.B := io.B(4)
  FA2.io.C := io.C(2)
  vec3(0) := FA2.io.R
  vec3(1) := FA2.io.S

  HA5.io.A := vec2(1)
  HA5.io.B := vec3(0)
  vecR(10) := HA5.io.S
  vecS(3) := HA5.io.C

  FA3.io.A := io.A(11)
  FA3.io.B := io.B(5)
  FA3.io.C := io.C(3)
  vec4(0) := FA3.io.R
  vec4(1) := FA3.io.S

  HA6.io.A := vec3(1)
  HA6.io.B := vec4(0)
  vecR(11) := HA6.io.S
  vecS(4) := HA6.io.C

  FA4.io.A := io.A(12)
  FA4.io.B := io.B(6)
  FA4.io.C := io.C(4)
  vec5(0) := FA4.io.R
  vec5(1) := FA4.io.S

  HA7.io.A := vec4(1)
  HA7.io.B := vec5(0)
  vecR(12) := HA7.io.S
  vecS(5) := HA7.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(5)
  COM0.io.C := io.B(7)
  COM0.io.D := io.A(13)
  COM0.io.E := vec5(1)
  vecR(13) := COM0.io.R
  vecS(6) := COM0.io.S
  C1 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(6)
  COM1.io.C := io.B(8)
  COM1.io.D := io.A(14)
  COM1.io.E := C1
  vecR(14) := COM1.io.R
  vecS(7) := COM1.io.S
  C2 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(7)
  COM2.io.C := io.B(9)
  COM2.io.D := io.A(15)
  COM2.io.E := C2
  vecR(15) := COM2.io.R
  vecS(8) := COM2.io.S
  C3 := COM2.io.P

  HACOM0.io.A := io.D(3)
  HACOM0.io.B := io.C(8)
  HACOM0.io.C := C3
  vecR(16) := HACOM0.io.R
  vecS(9) := HACOM0.io.S
  C4 := HACOM0.io.P

  HACOM1.io.A := io.D(4)
  HACOM1.io.B := io.C(9)
  HACOM1.io.C := C4
  vecR(17) := HACOM1.io.R
  vecS(10) := HACOM1.io.S
  C5 := HACOM1.io.P

  HACOM2.io.A := io.D(5)
  HACOM2.io.B := io.C(10)
  HACOM2.io.C := C5
  vecR(18) := HACOM2.io.R
  vecS(11) := HACOM2.io.S
  C6 := HACOM2.io.P

  HACOM3.io.A := io.D(6)
  HACOM3.io.B := io.C(11)
  HACOM3.io.C := C6
  vecR(19) := HACOM3.io.R
  vecS(12) := HACOM3.io.S
  C7 := HACOM3.io.P

  HACOM4.io.A := io.D(7)
  HACOM4.io.B := io.C(12)
  HACOM4.io.C := C7
  vecR(20) := HACOM4.io.R
  vecS(13) := HACOM4.io.S
  C8 := HACOM4.io.P

  HACOM5.io.A := io.D(8)
  HACOM5.io.B := io.C(13)
  HACOM5.io.C := C8
  vecR(21) := HACOM5.io.R
  vecS(14) := HACOM5.io.S
  C9 := HACOM5.io.P

  HACOM6.io.A := io.D(9)
  HACOM6.io.B := io.C(14)
  HACOM6.io.C := C9
  vecR(22) := HACOM6.io.R
  vecS(15) := HACOM6.io.S
  C10 := HACOM6.io.P

  HACOM7.io.A := io.D(10)
  HACOM7.io.B := io.C(15)
  HACOM7.io.C := C10
  vecR(23) := HACOM7.io.R
  vecS(16) := HACOM7.io.S
  C11 := HACOM7.io.P

  HACOM8.io.A := io.D(11)
  HACOM8.io.B := io.C(16)
  HACOM8.io.C := C11
  vecR(24) := HACOM8.io.R
  vecS(17) := HACOM8.io.S
  C12 := HACOM8.io.P

  HACOM9.io.A := io.D(12)
  HACOM9.io.B := io.C(17)
  HACOM9.io.C := C12
  vecR(25) := HACOM9.io.R
  vecS(18) := HACOM9.io.S
  C13 := HACOM9.io.P

  HACOM10.io.A := io.D(13)
  HACOM10.io.B := io.C(18)
  HACOM10.io.C := C13
  vecR(26) := HACOM10.io.R
  vecS(19) := HACOM10.io.S
  C14 := HACOM10.io.P

  HACOM11.io.A := io.D(14)
  HACOM11.io.B := io.C(19)
  HACOM11.io.C := C14
  vecR(27) := HACOM11.io.R
  vecS(20) := HACOM11.io.S
  C15 := HACOM11.io.P

  HACOM12.io.A := io.D(15)
  HACOM12.io.B := io.C(20)
  HACOM12.io.C := C15
  vecR(28) := HACOM12.io.R
  vecS(21) := HACOM12.io.S
  C16 := HACOM12.io.P

  HACOM13.io.A := io.D(16)
  HACOM13.io.B := io.C(21)
  HACOM13.io.C := C16
  vecR(29) := HACOM13.io.R
  vecS(22) := HACOM13.io.S
  C17 := HACOM13.io.P

  HACOM14.io.A := io.D(17)
  HACOM14.io.B := io.C(22)
  HACOM14.io.C := C17
  vecR(30) := HACOM14.io.R
  vecS(23) := HACOM14.io.S
  C18 := HACOM14.io.P

  HACOM15.io.A := io.D(18)
  HACOM15.io.B := io.C(23)
  HACOM15.io.C := C18
  vecR(31) := HACOM15.io.R
  vecS(24) := HACOM15.io.S
  C19 := HACOM15.io.P

  io.R := vecR(31) ## vecR(30) ## vecR(29) ## vecR(28) ## vecR(27) ## vecR(26) ## vecR(25) ## vecR(24) ##
    vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ## vecR(16) ##
    vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ##
    vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(24) ## vecS(23) ## vecS(22) ## vecS(21) ## vecS(20) ##
    vecS(19) ## vecS(18) ## vecS(17) ## vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ##
    vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressor10 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(32.W))
    val B = Input(UInt(25.W))
    val C = Input(UInt(32.W))
    val D = Input(UInt(28.W))
    val R = Output(UInt(48.W))
    val S = Output(UInt(39.W))
  })

  val vecR = Wire(Vec(48, UInt(1.W)))
  val vecS = Wire(Vec(39, UInt(1.W)))
  val C0 = Wire(UInt(1.W))
  val C1 = Wire(UInt(1.W))
  val C2 = Wire(UInt(1.W))
  val C3 = Wire(UInt(1.W))
  val C4 = Wire(UInt(1.W))
  val C5 = Wire(UInt(1.W))
  val C6 = Wire(UInt(1.W))
  val C7 = Wire(UInt(1.W))
  val C8 = Wire(UInt(1.W))
  val C9 = Wire(UInt(1.W))
  val C10 = Wire(UInt(1.W))
  val C11 = Wire(UInt(1.W))
  val C12 = Wire(UInt(1.W))
  val C13 = Wire(UInt(1.W))
  val C14 = Wire(UInt(1.W))
  val C15 = Wire(UInt(1.W))
  val C16 = Wire(UInt(1.W))
  val C17 = Wire(UInt(1.W))
  val C18 = Wire(UInt(1.W))
  val C19 = Wire(UInt(1.W))
  val C20 = Wire(UInt(1.W))
  val C21 = Wire(UInt(1.W))
  val C22 = Wire(UInt(1.W))
  val C23 = Wire(UInt(1.W))
  val C24 = Wire(UInt(1.W))
  val C25 = Wire(UInt(1.W))
  val C26 = Wire(UInt(1.W))
  val C27 = Wire(UInt(1.W))
  val C28 = Wire(UInt(1.W))
  val C29 = Wire(UInt(1.W))
  val C30 = Wire(UInt(1.W))
  val C31 = Wire(UInt(1.W))
  val C32 = Wire(UInt(1.W))
  val C33 = Wire(UInt(1.W))
  val C34 = Wire(UInt(1.W))

  val vec0 = Wire(Vec(2, UInt(1.W)))
  val vec1 = Wire(Vec(2, UInt(1.W)))
  val vec2 = Wire(Vec(2, UInt(1.W)))
  val vec3 = Wire(Vec(2, UInt(1.W)))
  val vec4 = Wire(Vec(2, UInt(1.W)))


  val HA0 = Module(new halfadder)
  val HA1 = Module(new halfadder)
  val HA2 = Module(new halfadder)
  val HA3 = Module(new halfadder)
  val HA4 = Module(new halfadder)
  val HA5 = Module(new halfadder)

  val FA0 = Module(new fulladder)
  val FA1 = Module(new fulladder)
  val FA2 = Module(new fulladder)
  val FA3 = Module(new fulladder)
  val FA4 = Module(new fulladder)

  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)
  val COM9 = Module(new compressor)
  val COM10 = Module(new compressor)

  val HACOM0 = Module(new HAcompressor)
  val HACOM1 = Module(new HAcompressor)
  val HACOM2 = Module(new HAcompressor)
  val HACOM3 = Module(new HAcompressor)
  val HACOM4 = Module(new HAcompressor)
  val HACOM5 = Module(new HAcompressor)
  val HACOM6 = Module(new HAcompressor)
  val HACOM7 = Module(new HAcompressor)
  val HACOM8 = Module(new HAcompressor)
  val HACOM9 = Module(new HAcompressor)
  val HACOM10 = Module(new HAcompressor)
  val HACOM11 = Module(new HAcompressor)
  val HACOM12 = Module(new HAcompressor)
  val HACOM13 = Module(new HAcompressor)
  val HACOM14 = Module(new HAcompressor)
  val HACOM15 = Module(new HAcompressor)
  val HACOM16 = Module(new HAcompressor)
  val HACOM17 = Module(new HAcompressor)
  val HACOM18 = Module(new HAcompressor)
  val HACOM19 = Module(new HAcompressor)
  val HACOM20 = Module(new HAcompressor)
  val HACOM21 = Module(new HAcompressor)
  val HACOM22 = Module(new HAcompressor)

  //val COM7 = Module(new compressor)

  //printf("R = %b\n", io.R)
  //printf("S = %b\n", io.S)

  vecR(0) := io.A(0)
  vecR(1) := io.A(1)
  vecR(2) := io.A(2)
  vecR(3) := io.A(3)
  vecR(4) := io.A(4)
  vecR(5) := io.A(5)
  vecR(6) := io.A(6)
  vecR(7) := io.A(7)

  HA0.io.A := io.A(8)
  HA0.io.B := io.B(0)
  vecR(8) := HA0.io.S
  C0 := HA0.io.C

  HACOM0.io.A := io.A(9)
  HACOM0.io.B := io.B(1)
  HACOM0.io.C := C0
  vecR(9) := HACOM0.io.R
  vecS(0) := HACOM0.io.S
  C1 := HACOM0.io.P

  HACOM1.io.A := io.A(10)
  HACOM1.io.B := io.B(2)
  HACOM1.io.C := C1
  vecR(10) := HACOM1.io.R
  vecS(1) := HACOM1.io.S
  C2 := HACOM1.io.P

  HACOM2.io.A := io.A(11)
  HACOM2.io.B := io.B(3)
  HACOM2.io.C := C2
  vecR(11) := HACOM2.io.R
  vecS(2) := HACOM2.io.S
  C3 := HACOM2.io.P

  HACOM3.io.A := io.A(12)
  HACOM3.io.B := io.B(4)
  HACOM3.io.C := C3
  vecR(12) := HACOM3.io.R
  vecS(3) := HACOM3.io.S
  C4 := HACOM3.io.P

  HACOM4.io.A := io.A(13)
  HACOM4.io.B := io.B(5)
  HACOM4.io.C := C4
  vecR(13) := HACOM4.io.R
  vecS(4) := HACOM4.io.S
  C5 := HACOM4.io.P

  HACOM5.io.A := io.A(14)
  HACOM5.io.B := io.B(6)
  HACOM5.io.C := C5
  vecR(14) := HACOM5.io.R
  vecS(5) := HACOM5.io.S
  C6 := HACOM5.io.P

  HACOM6.io.A := io.A(15)
  HACOM6.io.B := io.B(7)
  HACOM6.io.C := C6
  vecR(15) := HACOM6.io.R
  vecS(6) := HACOM6.io.S
  C7 := HACOM6.io.P

  FA0.io.A := io.A(16)
  FA0.io.B := io.B(8)
  FA0.io.C := io.C(0)
  vec0(0) := FA0.io.R
  vec0(1) := FA0.io.S

  HA1.io.A := C7
  HA1.io.B := vec0(0)
  vecR(16) := HA1.io.S
  vecS(7) := HA1.io.C

  FA1.io.A := io.A(17)
  FA1.io.B := io.B(9)
  FA1.io.C := io.C(1)
  vec1(0) := FA1.io.R
  vec1(1) := FA1.io.S

  HA2.io.A := vec0(1)
  HA2.io.B := vec1(0)
  vecR(17) := HA2.io.S
  vecS(8) := HA2.io.C

  FA2.io.A := io.A(18)
  FA2.io.B := io.B(10)
  FA2.io.C := io.C(2)
  vec2(0) := FA2.io.R
  vec2(1) := FA2.io.S

  HA3.io.A := vec1(1)
  HA3.io.B := vec2(0)
  vecR(18) := HA3.io.S
  vecS(9) := HA3.io.C

  FA3.io.A := io.A(19)
  FA3.io.B := io.B(11)
  FA3.io.C := io.C(3)
  vec3(0) := FA3.io.R
  vec3(1) := FA3.io.S

  HA4.io.A := vec2(1)
  HA4.io.B := vec3(0)
  vecR(19) := HA4.io.S
  vecS(10) := HA4.io.C

  FA4.io.A := io.A(20)
  FA4.io.B := io.B(12)
  FA4.io.C := io.C(4)
  vec4(0) := FA4.io.R
  vec4(1) := FA4.io.S

  HA5.io.A := vec3(1)
  HA5.io.B := vec4(0)
  vecR(20) := HA5.io.S
  vecS(11) := HA5.io.C

  COM0.io.A := io.D(0)
  COM0.io.B := io.C(5)
  COM0.io.C := io.B(13)
  COM0.io.D := io.A(21)
  COM0.io.E := vec4(1)
  vecR(21) := COM0.io.R
  vecS(12) := COM0.io.S
  C8 := COM0.io.P

  COM1.io.A := io.D(1)
  COM1.io.B := io.C(6)
  COM1.io.C := io.B(14)
  COM1.io.D := io.A(22)
  COM1.io.E := C8
  vecR(22) := COM1.io.R
  vecS(13) := COM1.io.S
  C9 := COM1.io.P

  COM2.io.A := io.D(2)
  COM2.io.B := io.C(7)
  COM2.io.C := io.B(15)
  COM2.io.D := io.A(23)
  COM2.io.E := C9
  vecR(23) := COM2.io.R
  vecS(14) := COM2.io.S
  C10 := COM2.io.P

  COM3.io.A := io.D(3)
  COM3.io.B := io.C(8)
  COM3.io.C := io.B(16)
  COM3.io.D := io.A(24)
  COM3.io.E := C10
  vecR(24) := COM3.io.R
  vecS(15) := COM3.io.S
  C11 := COM3.io.P

  COM4.io.A := io.D(4)
  COM4.io.B := io.C(9)
  COM4.io.C := io.B(17)
  COM4.io.D := io.A(25)
  COM4.io.E := C11
  vecR(25) := COM4.io.R
  vecS(16) := COM4.io.S
  C12 := COM4.io.P

  COM5.io.A := io.D(5)
  COM5.io.B := io.C(10)
  COM5.io.C := io.B(18)
  COM5.io.D := io.A(26)
  COM5.io.E := C12
  vecR(26) := COM5.io.R
  vecS(17) := COM5.io.S
  C13 := COM5.io.P

  COM6.io.A := io.D(6)
  COM6.io.B := io.C(11)
  COM6.io.C := io.B(19)
  COM6.io.D := io.A(27)
  COM6.io.E := C13
  vecR(27) := COM6.io.R
  vecS(18) := COM6.io.S
  C14 := COM6.io.P

  COM7.io.A := io.D(7)
  COM7.io.B := io.C(12)
  COM7.io.C := io.B(20)
  COM7.io.D := io.A(28)
  COM7.io.E := C14
  vecR(28) := COM7.io.R
  vecS(19) := COM7.io.S
  C15 := COM7.io.P

  COM8.io.A := io.D(8)
  COM8.io.B := io.C(13)
  COM8.io.C := io.B(21)
  COM8.io.D := io.A(29)
  COM8.io.E := C15
  vecR(29) := COM8.io.R
  vecS(20) := COM8.io.S
  C16 := COM8.io.P

  COM9.io.A := io.D(9)
  COM9.io.B := io.C(14)
  COM9.io.C := io.B(22)
  COM9.io.D := io.A(30)
  COM9.io.E := C16
  vecR(30) := COM9.io.R
  vecS(21) := COM9.io.S
  C17 := COM9.io.P

  COM10.io.A := io.D(10)
  COM10.io.B := io.C(15)
  COM10.io.C := io.B(23)
  COM10.io.D := io.A(31)
  COM10.io.E := C17
  vecR(31) := COM10.io.R
  vecS(22) := COM10.io.S
  C18 := COM10.io.P

  HACOM7.io.A := io.D(11)
  HACOM7.io.B := io.C(16)
  HACOM7.io.C := C18
  vecR(32) := HACOM7.io.R
  vecS(23) := HACOM7.io.S
  C19 := HACOM7.io.P

  HACOM8.io.A := io.D(12)
  HACOM8.io.B := io.C(17)
  HACOM8.io.C := C19
  vecR(33) := HACOM8.io.R
  vecS(24) := HACOM8.io.S
  C20 := HACOM8.io.P

  HACOM9.io.A := io.D(13)
  HACOM9.io.B := io.C(18)
  HACOM9.io.C := C20
  vecR(34) := HACOM9.io.R
  vecS(25) := HACOM9.io.S
  C21 := HACOM9.io.P

  HACOM10.io.A := io.D(14)
  HACOM10.io.B := io.C(19)
  HACOM10.io.C := C21
  vecR(35) := HACOM10.io.R
  vecS(26) := HACOM10.io.S
  C22 := HACOM10.io.P

  HACOM11.io.A := io.D(15)
  HACOM11.io.B := io.C(20)
  HACOM11.io.C := C22
  vecR(36) := HACOM11.io.R
  vecS(27) := HACOM11.io.S
  C23 := HACOM11.io.P

  HACOM12.io.A := io.D(16)
  HACOM12.io.B := io.C(21)
  HACOM12.io.C := C23
  vecR(37) := HACOM12.io.R
  vecS(28) := HACOM12.io.S
  C24 := HACOM12.io.P

  HACOM13.io.A := io.D(17)
  HACOM13.io.B := io.C(22)
  HACOM13.io.C := C24
  vecR(38) := HACOM13.io.R
  vecS(29) := HACOM13.io.S
  C25 := HACOM13.io.P

  HACOM14.io.A := io.D(18)
  HACOM14.io.B := io.C(23)
  HACOM14.io.C := C25
  vecR(39) := HACOM14.io.R
  vecS(30) := HACOM14.io.S
  C26 := HACOM14.io.P

  HACOM15.io.A := io.D(19)
  HACOM15.io.B := io.C(24)
  HACOM15.io.C := C26
  vecR(40) := HACOM15.io.R
  vecS(31) := HACOM15.io.S
  C27 := HACOM15.io.P

  HACOM16.io.A := io.D(20)
  HACOM16.io.B := io.C(25)
  HACOM16.io.C := C27
  vecR(41) := HACOM16.io.R
  vecS(32) := HACOM16.io.S
  C28 := HACOM16.io.P

  HACOM17.io.A := io.D(21)
  HACOM17.io.B := io.C(26)
  HACOM17.io.C := C28
  vecR(42) := HACOM17.io.R
  vecS(33) := HACOM17.io.S
  C29 := HACOM17.io.P

  HACOM18.io.A := io.D(22)
  HACOM18.io.B := io.C(27)
  HACOM18.io.C := C29
  vecR(43) := HACOM18.io.R
  vecS(34) := HACOM18.io.S
  C30 := HACOM18.io.P

  HACOM19.io.A := io.D(23)
  HACOM19.io.B := io.C(28)
  HACOM19.io.C := C30
  vecR(44) := HACOM19.io.R
  vecS(35) := HACOM19.io.S
  C31 := HACOM19.io.P

  HACOM20.io.A := io.D(24)
  HACOM20.io.B := io.C(29)
  HACOM20.io.C := C31
  vecR(45) := HACOM20.io.R
  vecS(36) := HACOM20.io.S
  C32 := HACOM20.io.P

  HACOM21.io.A := io.D(25)
  HACOM21.io.B := io.C(30)
  HACOM21.io.C := C32
  vecR(46) := HACOM21.io.R
  vecS(37) := HACOM21.io.S
  C33 := HACOM21.io.P

  HACOM22.io.A := io.D(26)
  HACOM22.io.B := io.C(31)
  HACOM22.io.C := C33
  vecR(47) := HACOM22.io.R
  vecS(38) := HACOM22.io.S
  C34 := HACOM22.io.P
  //printf("C34 = %b\n", C34)

  io.R := vecR(47) ## vecR(46) ## vecR(45) ## vecR(44) ## vecR(43) ## vecR(42) ## vecR(41) ## vecR(40) ##
    vecR(39) ## vecR(38) ## vecR(37) ## vecR(36) ## vecR(35) ## vecR(34) ## vecR(33) ## vecR(32) ##
    vecR(31) ## vecR(30) ## vecR(29) ## vecR(28) ## vecR(27) ## vecR(26) ## vecR(25) ## vecR(24) ##
    vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ## vecR(16) ##
    vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ##
    vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(38) ## vecS(37) ## vecS(36) ## vecS(35) ## vecS(34) ##
    vecS(33) ## vecS(32) ## vecS(31) ## vecS(30) ## vecS(29) ## vecS(28) ## vecS(27) ## vecS(26) ## vecS(25) ##
    vecS(24) ## vecS(23) ## vecS(22) ## vecS(21) ## vecS(20) ##
    vecS(19) ## vecS(18) ## vecS(17) ## vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ##
    vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}

class FourTwoCompressorxx extends Module {
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
    val S = Output(UInt(39.W))
  })
 // printf("R = %b\n", io.R)
 // printf("S = %b\n", io.S)

  val FTC0 = Module(new FourTwoCompressor0)
  val FTC1 = Module(new FourTwoCompressor1)
  val FTC2 = Module(new FourTwoCompressor2)
  val FTC3 = Module(new FourTwoCompressor3)
  val FTC4 = Module(new FourTwoCompressor4)
  val FTC5 = Module(new FourTwoCompressor5)
  val FTC6 = Module(new FourTwoCompressor6)
  val FTC7 = Module(new FourTwoCompressor7)
  val FTC8 = Module(new FourTwoCompressor8)
  val FTC9 = Module(new FourTwoCompressor9)
  val FTC10 = Module(new FourTwoCompressor10)

  val R0 = Wire(UInt(8.W))
  val S0 = Wire(UInt(4.W))
  val R1 = Wire(UInt(12.W))
  val S1 = Wire(UInt(9.W))
  val R2 = Wire(UInt(16.W))
  val S2 = Wire(UInt(13.W))
  val R3 = Wire(UInt(20.W))
  val S3 = Wire(UInt(17.W))
  val R4 = Wire(UInt(24.W))
  val S4 = Wire(UInt(21.W))
  val R5 = Wire(UInt(28.W))
  val S5 = Wire(UInt(25.W))
  val R6 = Wire(UInt(16.W))
  val S6 = Wire(UInt(11.W))
  val R7 = Wire(UInt(24.W))
  val S7 = Wire(UInt(20.W))
  val R8 = Wire(UInt(32.W))
  val S8 = Wire(UInt(28.W))
  val R9 = Wire(UInt(32.W))
  val S9 = Wire(UInt(25.W))

  FTC0.io.A := io.P0
  FTC0.io.B := io.P1
  FTC0.io.C := io.P2
  FTC0.io.D := io.P3
  R0 := FTC0.io.R
  S0 := FTC0.io.S

  FTC1.io.A := io.P4
  FTC1.io.B := io.P5
  FTC1.io.C := io.P6
  FTC1.io.D := io.P7
  R1 := FTC1.io.R
  S1 := FTC1.io.S

  FTC2.io.A := io.P8
  FTC2.io.B := io.P9
  FTC2.io.C := io.P10
  FTC2.io.D := io.P11
  R2 := FTC2.io.R
  S2 := FTC2.io.S

  FTC3.io.A := io.P12
  FTC3.io.B := io.P13
  FTC3.io.C := io.P14
  FTC3.io.D := io.P15
  R3 := FTC3.io.R
  S3 := FTC3.io.S

  FTC4.io.A := io.P16
  FTC4.io.B := io.P17
  FTC4.io.C := io.P18
  FTC4.io.D := io.P19
  R4 := FTC4.io.R
  S4 := FTC4.io.S

  FTC5.io.A := io.P20
  FTC5.io.B := io.P21
  FTC5.io.C := io.P22
  FTC5.io.D := io.P23
  R5 := FTC5.io.R
  S5 := FTC5.io.S

  FTC6.io.A := R0
  FTC6.io.B := S0
  FTC6.io.C := R1
  FTC6.io.D := S1
  R6 := FTC6.io.R
  S6 := FTC6.io.S

  FTC7.io.A := R2
  FTC7.io.B := S2
  FTC7.io.C := R3
  FTC7.io.D := S3
  R7 := FTC7.io.R
  S7 := FTC7.io.S

  FTC8.io.A := R4
  FTC8.io.B := S4
  FTC8.io.C := R5
  FTC8.io.D := S5
  R8 := FTC8.io.R
  S8 := FTC8.io.S

  FTC9.io.A := R6
  FTC9.io.B := S6
  FTC9.io.C := R7
  FTC9.io.D := S7
  R9 := FTC9.io.R
  S9 := FTC9.io.S

  FTC10.io.A := R9
  FTC10.io.B := S9
  FTC10.io.C := R8
  FTC10.io.D := S8
  io.R := FTC10.io.R
  io.S := FTC10.io.S
}
/*
class FourTwoCompressor extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(48.W))
    val B = Input(UInt(48.W))
    val C = Input(UInt(48.W))
    val D = Input(UInt(48.W))
    val E = Input(UInt(48.W))
    val R = Output(UInt(48.W))
    val S = Output(UInt(48.W))
    val P = Output(UInt(48.W))
  })

  val FA0 = Module(new compressor)
  val FA1 = Module(new compressor)
  val FA2 = Module(new compressor)
  val FA3 = Module(new compressor)
  val FA4 = Module(new compressor)
  val FA5 = Module(new compressor)
  val FA6 = Module(new compressor)
  val FA7 = Module(new compressor)
  val FA8 = Module(new compressor)
  val FA9 = Module(new compressor)
  val FA10 = Module(new compressor)
  val FA11 = Module(new compressor)
  val FA12 = Module(new compressor)
  val FA13 = Module(new compressor)
  val FA14 = Module(new compressor)
  val FA15 = Module(new compressor)
  val FA16 = Module(new compressor)
  val FA17 = Module(new compressor)
  val FA18 = Module(new compressor)
  val FA19 = Module(new compressor)
  val FA20 = Module(new compressor)
  val FA21 = Module(new compressor)
  val FA22 = Module(new compressor)
  val FA23 = Module(new compressor)
  val FA24 = Module(new compressor)
  val FA25 = Module(new compressor)
  val FA26 = Module(new compressor)
  val FA27 = Module(new compressor)
  val FA28 = Module(new compressor)
  val FA29 = Module(new compressor)
  val FA30 = Module(new compressor)
  val FA31 = Module(new compressor)
  val FA32 = Module(new compressor)
  val FA33 = Module(new compressor)
  val FA34 = Module(new compressor)
  val FA35 = Module(new compressor)
  val FA36 = Module(new compressor)
  val FA37 = Module(new compressor)
  val FA38 = Module(new compressor)
  val FA39 = Module(new compressor)
  val FA40 = Module(new compressor)
  val FA41 = Module(new compressor)
  val FA42 = Module(new compressor)
  val FA43 = Module(new compressor)
  val FA44 = Module(new compressor)
  val FA45 = Module(new compressor)
  val FA46 = Module(new compressor)
  val FA47 = Module(new compressor)

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

  io.P := FA47.io.P ## FA46.io.P ## FA45.io.P ## FA44.io.P ## FA43.io.P ## FA42.io.P ## FA41.io.P ## FA40.io.P ##
    FA39.io.P ## FA38.io.P ## FA37.io.P ## FA36.io.P ## FA35.io.P ## FA34.io.P ## FA33.io.P ## FA32.io.P ##
    FA31.io.P ## FA30.io.P ## FA29.io.P ## FA28.io.P ## FA27.io.P ## FA26.io.P ## FA25.io.P ## FA24.io.P ##
    FA23.io.P ## FA22.io.P ## FA21.io.P ## FA20.io.P ## FA19.io.P ## FA18.io.P ## FA17.io.P ## FA16.io.P ##
    FA15.io.P ## FA14.io.P ## FA13.io.P ## FA12.io.P ## FA11.io.P ## FA10.io.P ## FA9.io.P ## FA8.io.P ##
    FA7.io.P ## FA6.io.P ## FA5.io.P ## FA4.io.P ## FA3.io.P ## FA2.io.P ## FA1.io.P ## FA0.io.P

  FA0.io.A := io.A(0)
  FA0.io.B := io.B(0)
  FA0.io.C := io.C(0)
  FA0.io.D := io.D(0)
  FA0.io.E := io.E(0)

  FA1.io.A := io.A(1)
  FA1.io.B := io.B(1)
  FA1.io.C := io.C(1)
  FA1.io.D := io.D(1)
  FA1.io.E := io.E(1)

  FA2.io.A := io.A(2)
  FA2.io.B := io.B(2)
  FA2.io.C := io.C(2)
  FA2.io.D := io.D(2)
  FA2.io.E := io.E(2)

  FA3.io.A := io.A(3)
  FA3.io.B := io.B(3)
  FA3.io.C := io.C(3)
  FA3.io.D := io.D(3)
  FA3.io.E := io.E(3)

  FA4.io.A := io.A(4)
  FA4.io.B := io.B(4)
  FA4.io.C := io.C(4)
  FA4.io.D := io.D(4)
  FA4.io.E := io.E(4)

  FA5.io.A := io.A(5)
  FA5.io.B := io.B(5)
  FA5.io.C := io.C(5)
  FA5.io.D := io.D(5)
  FA5.io.E := io.E(5)

  FA6.io.A := io.A(6)
  FA6.io.B := io.B(6)
  FA6.io.C := io.C(6)
  FA6.io.D := io.D(6)
  FA6.io.E := io.E(6)

  FA7.io.A := io.A(7)
  FA7.io.B := io.B(7)
  FA7.io.C := io.C(7)
  FA7.io.D := io.D(7)
  FA7.io.E := io.E(7)

  FA8.io.A := io.A(8)
  FA8.io.B := io.B(8)
  FA8.io.C := io.C(8)
  FA8.io.D := io.D(8)
  FA8.io.E := io.E(8)

  FA9.io.A := io.A(9)
  FA9.io.B := io.B(9)
  FA9.io.C := io.C(9)
  FA9.io.D := io.D(9)
  FA9.io.E := io.E(9)

  FA10.io.A := io.A(10)
  FA10.io.B := io.B(10)
  FA10.io.C := io.C(10)
  FA10.io.D := io.D(10)
  FA10.io.E := io.E(10)

  FA11.io.A := io.A(11)
  FA11.io.B := io.B(11)
  FA11.io.C := io.C(11)
  FA11.io.D := io.D(11)
  FA11.io.E := io.E(11)

  FA12.io.A := io.A(12)
  FA12.io.B := io.B(12)
  FA12.io.C := io.C(12)
  FA12.io.D := io.D(12)
  FA12.io.E := io.E(12)

  FA13.io.A := io.A(13)
  FA13.io.B := io.B(13)
  FA13.io.C := io.C(13)
  FA13.io.D := io.D(13)
  FA13.io.E := io.E(13)

  FA14.io.A := io.A(14)
  FA14.io.B := io.B(14)
  FA14.io.C := io.C(14)
  FA14.io.D := io.D(14)
  FA14.io.E := io.E(14)

  FA15.io.A := io.A(15)
  FA15.io.B := io.B(15)
  FA15.io.C := io.C(15)
  FA15.io.D := io.D(15)
  FA15.io.E := io.E(15)

  FA16.io.A := io.A(16)
  FA16.io.B := io.B(16)
  FA16.io.C := io.C(16)
  FA16.io.D := io.D(16)
  FA16.io.E := io.E(16)

  FA17.io.A := io.A(17)
  FA17.io.B := io.B(17)
  FA17.io.C := io.C(17)
  FA17.io.D := io.D(17)
  FA17.io.E := io.E(17)

  FA18.io.A := io.A(18)
  FA18.io.B := io.B(18)
  FA18.io.C := io.C(18)
  FA18.io.D := io.D(18)
  FA18.io.E := io.E(18)

  FA19.io.A := io.A(19)
  FA19.io.B := io.B(19)
  FA19.io.C := io.C(19)
  FA19.io.D := io.D(19)
  FA19.io.E := io.E(19)

  FA20.io.A := io.A(20)
  FA20.io.B := io.B(20)
  FA20.io.C := io.C(20)
  FA20.io.D := io.D(20)
  FA20.io.E := io.E(20)

  FA21.io.A := io.A(21)
  FA21.io.B := io.B(21)
  FA21.io.C := io.C(21)
  FA21.io.D := io.D(21)
  FA21.io.E := io.E(21)

  FA22.io.A := io.A(22)
  FA22.io.B := io.B(22)
  FA22.io.C := io.C(22)
  FA22.io.D := io.D(22)
  FA22.io.E := io.E(22)

  FA23.io.A := io.A(23)
  FA23.io.B := io.B(23)
  FA23.io.C := io.C(23)
  FA23.io.D := io.D(23)
  FA23.io.E := io.E(23)

  FA24.io.A := io.A(24)
  FA24.io.B := io.B(24)
  FA24.io.C := io.C(24)
  FA24.io.D := io.D(24)
  FA24.io.E := io.E(24)

  FA25.io.A := io.A(25)
  FA25.io.B := io.B(25)
  FA25.io.C := io.C(25)
  FA25.io.D := io.D(25)
  FA25.io.E := io.E(25)

  FA26.io.A := io.A(26)
  FA26.io.B := io.B(26)
  FA26.io.C := io.C(26)
  FA26.io.D := io.D(26)
  FA26.io.E := io.E(26)

  FA27.io.A := io.A(27)
  FA27.io.B := io.B(27)
  FA27.io.C := io.C(27)
  FA27.io.D := io.D(27)
  FA27.io.E := io.E(27)

  FA28.io.A := io.A(28)
  FA28.io.B := io.B(28)
  FA28.io.C := io.C(28)
  FA28.io.D := io.D(28)
  FA28.io.E := io.E(28)

  FA29.io.A := io.A(29)
  FA29.io.B := io.B(29)
  FA29.io.C := io.C(29)
  FA29.io.D := io.D(29)
  FA29.io.E := io.E(29)

  FA30.io.A := io.A(30)
  FA30.io.B := io.B(30)
  FA30.io.C := io.C(30)
  FA30.io.D := io.D(30)
  FA30.io.E := io.E(30)

  FA31.io.A := io.A(31)
  FA31.io.B := io.B(31)
  FA31.io.C := io.C(31)
  FA31.io.D := io.D(31)
  FA31.io.E := io.E(31)

  FA32.io.A := io.A(32)
  FA32.io.B := io.B(32)
  FA32.io.C := io.C(32)
  FA32.io.D := io.D(32)
  FA32.io.E := io.E(32)

  FA33.io.A := io.A(33)
  FA33.io.B := io.B(33)
  FA33.io.C := io.C(33)
  FA33.io.D := io.D(33)
  FA33.io.E := io.E(33)

  FA34.io.A := io.A(34)
  FA34.io.B := io.B(34)
  FA34.io.C := io.C(34)
  FA34.io.D := io.D(34)
  FA34.io.E := io.E(34)

  FA35.io.A := io.A(35)
  FA35.io.B := io.B(35)
  FA35.io.C := io.C(35)
  FA35.io.D := io.D(35)
  FA35.io.E := io.E(35)

  FA36.io.A := io.A(36)
  FA36.io.B := io.B(36)
  FA36.io.C := io.C(36)
  FA36.io.D := io.D(36)
  FA36.io.E := io.E(36)

  FA37.io.A := io.A(37)
  FA37.io.B := io.B(37)
  FA37.io.C := io.C(37)
  FA37.io.D := io.D(37)
  FA37.io.E := io.E(37)

  FA38.io.A := io.A(38)
  FA38.io.B := io.B(38)
  FA38.io.C := io.C(38)
  FA38.io.D := io.D(38)
  FA38.io.E := io.E(38)

  FA39.io.A := io.A(39)
  FA39.io.B := io.B(39)
  FA39.io.C := io.C(39)
  FA39.io.D := io.D(39)
  FA39.io.E := io.E(39)

  FA40.io.A := io.A(40)
  FA40.io.B := io.B(40)
  FA40.io.C := io.C(40)
  FA40.io.D := io.D(40)
  FA40.io.E := io.E(40)

  FA41.io.A := io.A(41)
  FA41.io.B := io.B(41)
  FA41.io.C := io.C(41)
  FA41.io.D := io.D(41)
  FA41.io.E := io.E(41)

  FA42.io.A := io.A(42)
  FA42.io.B := io.B(42)
  FA42.io.C := io.C(42)
  FA42.io.D := io.D(42)
  FA42.io.E := io.E(42)

  FA43.io.A := io.A(43)
  FA43.io.B := io.B(43)
  FA43.io.C := io.C(43)
  FA43.io.D := io.D(43)
  FA43.io.E := io.E(43)

  FA44.io.A := io.A(44)
  FA44.io.B := io.B(44)
  FA44.io.C := io.C(44)
  FA44.io.D := io.D(44)
  FA44.io.E := io.E(44)

  FA45.io.A := io.A(45)
  FA45.io.B := io.B(45)
  FA45.io.C := io.C(45)
  FA45.io.D := io.D(45)
  FA45.io.E := io.E(45)

  FA46.io.A := io.A(46)
  FA46.io.B := io.B(46)
  FA46.io.C := io.C(46)
  FA46.io.D := io.D(46)
  FA46.io.E := io.E(46)

  FA47.io.A := io.A(47)
  FA47.io.B := io.B(47)
  FA47.io.C := io.C(47)
  FA47.io.D := io.D(47)
  FA47.io.E := io.E(47)
}

 */

class FourTwoCompressor51 extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(51.W))
    val B = Input(UInt(51.W))
    val C = Input(UInt(51.W))
    val D = Input(UInt(51.W))
    val R = Output(UInt(51.W))
    val S = Output(UInt(51.W))
  })

  val vecR = Wire(Vec(51, UInt(1.W)))
  val vecS = Wire(Vec(51, UInt(1.W)))
  val HA0 = Module(new halfadder)

  val FA0 = Module(new fulladder)


  val COM0 = Module(new compressor)
  val COM1 = Module(new compressor)
  val COM2 = Module(new compressor)
  val COM3 = Module(new compressor)
  val COM4 = Module(new compressor)
  val COM5 = Module(new compressor)
  val COM6 = Module(new compressor)
  val COM7 = Module(new compressor)
  val COM8 = Module(new compressor)
  val COM9 = Module(new compressor)
  val COM10 = Module(new compressor)
  val COM11 = Module(new compressor)
  val COM12 = Module(new compressor)
  val COM13 = Module(new compressor)
  val COM14 = Module(new compressor)
  val COM15 = Module(new compressor)
  val COM16 = Module(new compressor)
  val COM17 = Module(new compressor)
  val COM18 = Module(new compressor)
  val COM19 = Module(new compressor)
  val COM20 = Module(new compressor)
  val COM21 = Module(new compressor)
  val COM22 = Module(new compressor)
  val COM23 = Module(new compressor)
  val COM24 = Module(new compressor)
  val COM25 = Module(new compressor)
  val COM26 = Module(new compressor)
  val COM27 = Module(new compressor)
  val COM28 = Module(new compressor)
  val COM29 = Module(new compressor)
  val COM30 = Module(new compressor)
  val COM31 = Module(new compressor)
  val COM32 = Module(new compressor)
  val COM33 = Module(new compressor)
  val COM34 = Module(new compressor)
  val COM35 = Module(new compressor)
  val COM36 = Module(new compressor)
  val COM37 = Module(new compressor)
  val COM38 = Module(new compressor)
  val COM39 = Module(new compressor)
  val COM40 = Module(new compressor)
  val COM41 = Module(new compressor)
  val COM42 = Module(new compressor)
  val COM43 = Module(new compressor)
  val COM44 = Module(new compressor)
  val COM45 = Module(new compressor)
  val COM46 = Module(new compressor)
  val COM47 = Module(new compressor)
  val COM48 = Module(new compressor)
  val COM49 = Module(new compressor)

  val Carry = Wire(UInt(1.W))
  val C = Wire(Vec(51, UInt(1.W)))

  FA0.io.A := io.B(0)
  FA0.io.B := io.C(0)
  FA0.io.C := io.D(0)
  Carry := FA0.io.R
  C(0) := FA0.io.S

  HA0.io.A := io.A(0)
  HA0.io.B := Carry
  vecR(0) := HA0.io.S
  vecS(0) := HA0.io.C

  COM0.io.A := io.A(1)
  COM0.io.B := io.B(1)
  COM0.io.C := io.C(1)
  COM0.io.D := io.D(1)
  COM0.io.E := C(0)
  vecR(1) := COM0.io.R
  vecS(1) := COM0.io.S
  C(1) := COM0.io.P

  COM1.io.A := io.A(2)
  COM1.io.B := io.B(2)
  COM1.io.C := io.C(2)
  COM1.io.D := io.D(2)
  COM1.io.E := C(1)
  vecR(2) := COM1.io.R
  vecS(2) := COM1.io.S
  C(2) := COM1.io.P

  COM2.io.A := io.A(3)
  COM2.io.B := io.B(3)
  COM2.io.C := io.C(3)
  COM2.io.D := io.D(3)
  COM2.io.E := C(2)
  vecR(3) := COM2.io.R
  vecS(3) := COM2.io.S
  C(3) := COM2.io.P

  COM3.io.A := io.A(4)
  COM3.io.B := io.B(4)
  COM3.io.C := io.C(4)
  COM3.io.D := io.D(4)
  COM3.io.E := C(3)
  vecR(4) := COM3.io.R
  vecS(4) := COM3.io.S
  C(4) := COM3.io.P

  COM4.io.A := io.A(5)
  COM4.io.B := io.B(5)
  COM4.io.C := io.C(5)
  COM4.io.D := io.D(5)
  COM4.io.E := C(4)
  vecR(5) := COM4.io.R
  vecS(5) := COM4.io.S
  C(5) := COM4.io.P

  COM5.io.A := io.A(6)
  COM5.io.B := io.B(6)
  COM5.io.C := io.C(6)
  COM5.io.D := io.D(6)
  COM5.io.E := C(5)
  vecR(6) := COM5.io.R
  vecS(6) := COM5.io.S
  C(6) := COM5.io.P

  COM6.io.A := io.A(7)
  COM6.io.B := io.B(7)
  COM6.io.C := io.C(7)
  COM6.io.D := io.D(7)
  COM6.io.E := C(6)
  vecR(7) := COM6.io.R
  vecS(7) := COM6.io.S
  C(7) := COM6.io.P

  COM7.io.A := io.A(8)
  COM7.io.B := io.B(8)
  COM7.io.C := io.C(8)
  COM7.io.D := io.D(8)
  COM7.io.E := C(7)
  vecR(8) := COM7.io.R
  vecS(8) := COM7.io.S
  C(8) := COM7.io.P

  COM8.io.A := io.A(9)
  COM8.io.B := io.B(9)
  COM8.io.C := io.C(9)
  COM8.io.D := io.D(9)
  COM8.io.E := C(8)
  vecR(9) := COM8.io.R
  vecS(9) := COM8.io.S
  C(9) := COM8.io.P

  COM9.io.A := io.A(10)
  COM9.io.B := io.B(10)
  COM9.io.C := io.C(10)
  COM9.io.D := io.D(10)
  COM9.io.E := C(9)
  vecR(10) := COM9.io.R
  vecS(10) := COM9.io.S
  C(10) := COM9.io.P

  COM10.io.A := io.A(11)
  COM10.io.B := io.B(11)
  COM10.io.C := io.C(11)
  COM10.io.D := io.D(11)
  COM10.io.E := C(10)
  vecR(11) := COM10.io.R
  vecS(11) := COM10.io.S
  C(11) := COM10.io.P

  COM11.io.A := io.A(12)
  COM11.io.B := io.B(12)
  COM11.io.C := io.C(12)
  COM11.io.D := io.D(12)
  COM11.io.E := C(11)
  vecR(12) := COM11.io.R
  vecS(12) := COM11.io.S
  C(12) := COM11.io.P

  COM12.io.A := io.A(13)
  COM12.io.B := io.B(13)
  COM12.io.C := io.C(13)
  COM12.io.D := io.D(13)
  COM12.io.E := C(12)
  vecR(13) := COM12.io.R
  vecS(13) := COM12.io.S
  C(13) := COM12.io.P

  COM13.io.A := io.A(14)
  COM13.io.B := io.B(14)
  COM13.io.C := io.C(14)
  COM13.io.D := io.D(14)
  COM13.io.E := C(13)
  vecR(14) := COM13.io.R
  vecS(14) := COM13.io.S
  C(14) := COM13.io.P

  COM14.io.A := io.A(15)
  COM14.io.B := io.B(15)
  COM14.io.C := io.C(15)
  COM14.io.D := io.D(15)
  COM14.io.E := C(14)
  vecR(15) := COM14.io.R
  vecS(15) := COM14.io.S
  C(15) := COM14.io.P

  COM15.io.A := io.A(16)
  COM15.io.B := io.B(16)
  COM15.io.C := io.C(16)
  COM15.io.D := io.D(16)
  COM15.io.E := C(15)
  vecR(16) := COM15.io.R
  vecS(16) := COM15.io.S
  C(16) := COM15.io.P

  COM16.io.A := io.A(17)
  COM16.io.B := io.B(17)
  COM16.io.C := io.C(17)
  COM16.io.D := io.D(17)
  COM16.io.E := C(16)
  vecR(17) := COM16.io.R
  vecS(17) := COM16.io.S
  C(17) := COM16.io.P

  COM17.io.A := io.A(18)
  COM17.io.B := io.B(18)
  COM17.io.C := io.C(18)
  COM17.io.D := io.D(18)
  COM17.io.E := C(17)
  vecR(18) := COM17.io.R
  vecS(18) := COM17.io.S
  C(18) := COM17.io.P

  COM18.io.A := io.A(19)
  COM18.io.B := io.B(19)
  COM18.io.C := io.C(19)
  COM18.io.D := io.D(19)
  COM18.io.E := C(18)
  vecR(19) := COM18.io.R
  vecS(19) := COM18.io.S
  C(19) := COM18.io.P

  COM19.io.A := io.A(20)
  COM19.io.B := io.B(20)
  COM19.io.C := io.C(20)
  COM19.io.D := io.D(20)
  COM19.io.E := C(19)
  vecR(20) := COM19.io.R
  vecS(20) := COM19.io.S
  C(20) := COM19.io.P
  COM20.io.A := io.A(21)
  COM20.io.B := io.B(21)
  COM20.io.C := io.C(21)
  COM20.io.D := io.D(21)
  COM20.io.E := C(20)
  vecR(21) := COM20.io.R
  vecS(21) := COM20.io.S
  C(21) := COM20.io.P

  COM21.io.A := io.A(22)
  COM21.io.B := io.B(22)
  COM21.io.C := io.C(22)
  COM21.io.D := io.D(22)
  COM21.io.E := C(21)
  vecR(22) := COM22.io.R
  vecS(22) := COM22.io.S
  C(22) := COM22.io.P

  COM22.io.A := io.A(23)
  COM22.io.B := io.B(23)
  COM22.io.C := io.C(23)
  COM22.io.D := io.D(23)
  COM22.io.E := C(22)
  vecR(23) := COM22.io.R
  vecS(23) := COM22.io.S
  C(23) := COM22.io.P

  COM23.io.A := io.A(24)
  COM23.io.B := io.B(24)
  COM23.io.C := io.C(24)
  COM23.io.D := io.D(24)
  COM23.io.E := C(23)
  vecR(24) := COM23.io.R
  vecS(24) := COM23.io.S
  C(24) := COM23.io.P

  COM24.io.A := io.A(25)
  COM24.io.B := io.B(25)
  COM24.io.C := io.C(25)
  COM24.io.D := io.D(25)
  COM24.io.E := C(24)
  vecR(25) := COM24.io.R
  vecS(25) := COM24.io.S
  C(25) := COM24.io.P

  COM25.io.A := io.A(26)
  COM25.io.B := io.B(26)
  COM25.io.C := io.C(26)
  COM25.io.D := io.D(26)
  COM25.io.E := C(25)
  vecR(26) := COM25.io.R
  vecS(26) := COM25.io.S
  C(26) := COM25.io.P

  COM26.io.A := io.A(27)
  COM26.io.B := io.B(27)
  COM26.io.C := io.C(27)
  COM26.io.D := io.D(27)
  COM26.io.E := C(26)
  vecR(27) := COM26.io.R
  vecS(27) := COM26.io.S
  C(27) := COM26.io.P

  COM27.io.A := io.A(28)
  COM27.io.B := io.B(28)
  COM27.io.C := io.C(28)
  COM27.io.D := io.D(28)
  COM27.io.E := C(27)
  vecR(28) := COM27.io.R
  vecS(28) := COM27.io.S
  C(28) := COM27.io.P

  COM28.io.A := io.A(29)
  COM28.io.B := io.B(29)
  COM28.io.C := io.C(29)
  COM28.io.D := io.D(29)
  COM28.io.E := C(28)
  vecR(29) := COM28.io.R
  vecS(29) := COM28.io.S
  C(29) := COM28.io.P

  COM29.io.A := io.A(30)
  COM29.io.B := io.B(30)
  COM29.io.C := io.C(30)
  COM29.io.D := io.D(30)
  COM29.io.E := C(29)
  vecR(30) := COM29.io.R
  vecS(30) := COM29.io.S
  C(30) := COM29.io.P

  COM30.io.A := io.A(31)
  COM30.io.B := io.B(31)
  COM30.io.C := io.C(31)
  COM30.io.D := io.D(31)
  COM30.io.E := C(30)
  vecR(31) := COM30.io.R
  vecS(31) := COM30.io.S
  C(31) := COM30.io.P

  COM31.io.A := io.A(32)
  COM31.io.B := io.B(32)
  COM31.io.C := io.C(32)
  COM31.io.D := io.D(32)
  COM31.io.E := C(31)
  vecR(32) := COM31.io.R
  vecS(32) := COM31.io.S
  C(32) := COM31.io.P

  COM32.io.A := io.A(33)
  COM32.io.B := io.B(33)
  COM32.io.C := io.C(33)
  COM32.io.D := io.D(33)
  COM32.io.E := C(32)
  vecR(33) := COM32.io.R
  vecS(33) := COM32.io.S
  C(33) := COM32.io.P

  COM33.io.A := io.A(34)
  COM33.io.B := io.B(34)
  COM33.io.C := io.C(34)
  COM33.io.D := io.D(34)
  COM33.io.E := C(33)
  vecR(34) := COM33.io.R
  vecS(34) := COM33.io.S
  C(34) := COM33.io.P

  COM34.io.A := io.A(35)
  COM34.io.B := io.B(35)
  COM34.io.C := io.C(35)
  COM34.io.D := io.D(35)
  COM34.io.E := C(34)
  vecR(35) := COM34.io.R
  vecS(35) := COM34.io.S
  C(35) := COM34.io.P

  COM35.io.A := io.A(36)
  COM35.io.B := io.B(36)
  COM35.io.C := io.C(36)
  COM35.io.D := io.D(36)
  COM35.io.E := C(35)
  vecR(36) := COM35.io.R
  vecS(36) := COM35.io.S
  C(36) := COM35.io.P

  COM36.io.A := io.A(37)
  COM36.io.B := io.B(37)
  COM36.io.C := io.C(37)
  COM36.io.D := io.D(37)
  COM36.io.E := C(36)
  vecR(37) := COM36.io.R
  vecS(37) := COM36.io.S
  C(37) := COM36.io.P

  COM37.io.A := io.A(38)
  COM37.io.B := io.B(38)
  COM37.io.C := io.C(38)
  COM37.io.D := io.D(38)
  COM37.io.E := C(37)
  vecR(38) := COM37.io.R
  vecS(38) := COM37.io.S
  C(38) := COM37.io.P

  COM38.io.A := io.A(39)
  COM38.io.B := io.B(39)
  COM38.io.C := io.C(39)
  COM38.io.D := io.D(39)
  COM38.io.E := C(38)
  vecR(39) := COM38.io.R
  vecS(39) := COM38.io.S
  C(39) := COM38.io.P

  COM39.io.A := io.A(40)
  COM39.io.B := io.B(40)
  COM39.io.C := io.C(40)
  COM39.io.D := io.D(40)
  COM39.io.E := C(39)
  vecR(40) := COM39.io.R
  vecS(40) := COM39.io.S
  C(40) := COM39.io.P
  COM40.io.A := io.A(41)
  COM40.io.B := io.B(41)
  COM40.io.C := io.C(41)
  COM40.io.D := io.D(41)
  COM40.io.E := C(40)
  vecR(41) := COM40.io.R
  vecS(41) := COM40.io.S
  C(41) := COM40.io.P

  COM41.io.A := io.A(42)
  COM41.io.B := io.B(42)
  COM41.io.C := io.C(42)
  COM41.io.D := io.D(42)
  COM41.io.E := C(41)
  vecR(42) := COM41.io.R
  vecS(42) := COM41.io.S
  C(42) := COM41.io.P

  COM42.io.A := io.A(43)
  COM42.io.B := io.B(43)
  COM42.io.C := io.C(43)
  COM42.io.D := io.D(43)
  COM42.io.E := C(42)
  vecR(43) := COM42.io.R
  vecS(43) := COM42.io.S
  C(43) := COM42.io.P

  COM43.io.A := io.A(44)
  COM43.io.B := io.B(44)
  COM43.io.C := io.C(44)
  COM43.io.D := io.D(44)
  COM43.io.E := C(43)
  vecR(44) := COM43.io.R
  vecS(44) := COM43.io.S
  C(44) := COM43.io.P

  COM44.io.A := io.A(45)
  COM44.io.B := io.B(45)
  COM44.io.C := io.C(45)
  COM44.io.D := io.D(45)
  COM44.io.E := C(44)
  vecR(45) := COM44.io.R
  vecS(45) := COM44.io.S
  C(45) := COM44.io.P

  COM45.io.A := io.A(46)
  COM45.io.B := io.B(46)
  COM45.io.C := io.C(46)
  COM45.io.D := io.D(46)
  COM45.io.E := C(45)
  vecR(46) := COM45.io.R
  vecS(46) := COM45.io.S
  C(46) := COM45.io.P

  COM46.io.A := io.A(47)
  COM46.io.B := io.B(47)
  COM46.io.C := io.C(47)
  COM46.io.D := io.D(47)
  COM46.io.E := C(46)
  vecR(47) := COM46.io.R
  vecS(47) := COM46.io.S
  C(47) := COM46.io.P

  COM47.io.A := io.A(48)
  COM47.io.B := io.B(48)
  COM47.io.C := io.C(48)
  COM47.io.D := io.D(48)
  COM47.io.E := C(47)
  vecR(48) := COM47.io.R
  vecS(48) := COM47.io.S
  C(48) := COM47.io.P

  COM48.io.A := io.A(49)
  COM48.io.B := io.B(49)
  COM48.io.C := io.C(49)
  COM48.io.D := io.D(49)
  COM48.io.E := C(48)
  vecR(49) := COM48.io.R
  vecS(49) := COM48.io.S
  C(49) := COM48.io.P

  COM49.io.A := io.A(50)
  COM49.io.B := io.B(50)
  COM49.io.C := io.C(50)
  COM49.io.D := io.D(50)
  COM49.io.E := C(49)
  vecR(50) := COM49.io.R
  vecS(50) := COM49.io.S
  C(50) := COM49.io.P

  io.R := vecR(50) ## vecR(49) ## vecR(48) ## vecR(47) ## vecR(46) ## vecR(45) ## vecR(44) ## vecR(43) ## vecR(42) ## vecR(41) ## vecR(40) ##
    vecR(39) ## vecR(38) ## vecR(37) ## vecR(36) ## vecR(35) ## vecR(34) ## vecR(33) ## vecR(32) ##
    vecR(31) ## vecR(30) ## vecR(29) ## vecR(28) ## vecR(27) ## vecR(26) ## vecR(25) ## vecR(24) ##
    vecR(23) ## vecR(22) ## vecR(21) ## vecR(20) ## vecR(19) ## vecR(18) ## vecR(17) ## vecR(16) ##
    vecR(15) ## vecR(14) ## vecR(13) ## vecR(12) ## vecR(11) ## vecR(10) ## vecR(9) ## vecR(8) ##
    vecR(7) ## vecR(6) ## vecR(5) ## vecR(4) ## vecR(3) ## vecR(2) ## vecR(1) ## vecR(0)
  io.S := vecS(50) ## vecS(49) ## vecS(48) ##
    vecS(47) ## vecS(46) ## vecS(45) ## vecS(44) ## vecS(43) ## vecS(42) ## vecS(41) ## vecS(40) ## vecS(39) ##
    vecS(38) ## vecS(37) ## vecS(36) ## vecS(35) ## vecS(34) ##
    vecS(33) ## vecS(32) ## vecS(31) ## vecS(30) ## vecS(29) ## vecS(28) ## vecS(27) ## vecS(26) ## vecS(25) ##
    vecS(24) ## vecS(23) ## vecS(22) ## vecS(21) ## vecS(20) ##
    vecS(19) ## vecS(18) ## vecS(17) ## vecS(16) ## vecS(15) ## vecS(14) ## vecS(13) ## vecS(12) ## vecS(11) ##
    vecS(10) ## vecS(9) ## vecS(8) ## vecS(7) ## vecS(6) ## vecS(5) ## vecS(4) ## vecS(3) ## vecS(2) ## vecS(1) ## vecS(0)
}