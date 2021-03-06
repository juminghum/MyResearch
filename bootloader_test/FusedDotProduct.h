// See LICENSE for license details.

#ifndef SRC_MAIN_C_FUSEDDOTPRODUCT_H
#define SRC_MAIN_C_FUSEDDOTPRODUCT_H

#include "/home/miyahara/project/rocket-chip/rocc-software/src/xcustom.h"

#define funct_add 0
#define funct_sub 1
#define funct_D0_add 2
#define funct_D0_sub 3
#define funct_B0_add 4
#define funct_B0_sub 5
#define funct_B0_D0_add 6
#define funct_B0_D0_sub 7
//#define k_DO_LOAD 2
//#define k_DO_ACCUM 3

#define XCUSTOM_ACC 0
/*
//#define doWrite(y, rocc_rd, data)                                       \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, data, rocc_rd, k_DO_WRITE);
//#define doRead(y, rocc_rd)                                              \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, 0, rocc_rd, k_DO_READ);
//#define doLoad(y, rocc_rd, mem_addr)                                    \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, mem_addr, rocc_rd, k_DO_LOAD);
//#define doAccum(y, rocc_rd, data) \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, data, rocc_rd, k_DO_ACCUM);
*/
#define doAdd(y, rs1, rs2)                                       \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_add);
#define doSub(y, rs1, rs2)                                              \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_sub);
#define do_D0_Add(y, rs1, rs2)                                       \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_D0_add);
#define do_D0_Sub(y, rs1, rs2)                                              \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_D0_sub);
#define do_B0_Add(y, rs1, rs2)                                       \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_B0_add);
#define do_B0_Sub(y, rs1, rs2)                                              \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_B0_sub);
#define do_B0_D0_Add(y, rs1, rs2)                                       \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_B0_D0_add);
#define do_B0_D0_Sub(y, rs1, rs2)                                              \
  ROCC_INSTRUCTION(XCUSTOM_ACC, y, rs1, rs2, funct_B0_D0_sub);


#endif  // SRC_MAIN_C_FUSEDDOTPRODUCT_H
