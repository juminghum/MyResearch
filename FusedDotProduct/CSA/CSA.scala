package CSA

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
}
