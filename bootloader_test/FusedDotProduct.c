// See LICENSE for license details.

#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
//#include "include/accumulator.h"
//#include "include/translator.h"
#include "FusedDotProduct.h"

int main() {
/*
	uint64_t data [] = {0b0100000101100000000000000000000001000000101000000000000000000000, 0b0100000010000000000000000000000001000000010000000000000000000000};
*/
	//uint64_t result;
	//uint64_t expect_result =   0b11000011101100010110101000000000;

	//float data [] = {7.25, -20, -64.5625, 3.25};
	//uint64_t result;
	//float result0, result1;
	//float expect_result = -354.828125;
	union rs { 
		float f; 
		char c[8];
	};

	union rs mem[255];
	int i;
	for (i=0; i<256; i++){
		mem[i].f = rand();
	}
	union { float f; uint64_t i; } ina;
	union { float f; uint64_t i; } inb;
	union { float f; uint64_t i; } inc;
	union { float f; uint64_t i; } ind;
	ina.f = 7.25f;
	inb.f = -20.0f;
	inc.f = -64.5625f;
	ind.f = 3.25f;
 	printf("[INFO] Register Data: ina = 0x%lx\n", ina.i);
 	printf("[INFO] Register Data: inb = 0x%lx\n", inb.i);
	uint64_t rs1 = (ina.i << 32) + inb.i;
	uint64_t rs2 = (inc.i << 32) + ind.i;
	
/*
  uint16_t addr = 1;
  printf("[INFO] Write R[%d] = 0x%lx\n", addr, data[0]);
  doWrite(y, addr, data[0]);

  printf("[INFO] Read R[%d]\n", addr);
  doRead(y, addr);
  printf("[INFO]   Received 0x%lx (expected 0x%lx)\n", y, data[0]);
  assert(y == data[0]);

  uint64_t data_accum = -data[0] + data[1];
  printf("[INFO] Accum R[%d] with 0x%lx\n", addr, data_accum);
  doAccum(y, addr, data_accum);
  assert(y == data[0]);

  printf("[INFO] Read R[%d]\n", addr);
  doRead(y, addr);
  printf("[INFO]   Received 0x%lx (expected 0x%lx)\n", y, data[1]);
  assert(y == data[1]);

  uint64_t data_addr;
  doTranslate(data_addr, &data[2]);
  printf("[INFO] Load 0x%lx (virt: 0x%p, phys: 0x%p) via L1 virtual address\n",
         data[2], &data[2], (void *) data_addr);
  doLoad(y, addr, &data[2]);
  assert(y == data[1]);

  printf("[INFO] Read R[%d]\n", addr);
  doRead(y, addr);
  printf("[INFO]   Received 0x%lx (expected 0x%lx)\n", y, data[2]);
  assert(y == data[2]);
*/
  printf("[INFO] Register Data: rs1 = 0x%lx\n", rs1);
  printf("[INFO] Register Data: rs2 = 0x%lx\n", rs2);
/*
  for (int count = 0; count < 10; count++) {
	volatile uint64_t start_cycle, stop_cycle;
	uint64_t total = 0;

	start_cycle = 0x0;
*/
  	doAdd(result, rs1, rs2);
/*
	stop_cycle = 0x0;


  }
*/
  printf("[INFO] Received 0x%lx (expected 0x%lx)\n", result, expect_result);
  return 0;
}
