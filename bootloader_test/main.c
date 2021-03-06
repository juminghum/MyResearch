#include "print.h"
#include "htif.h"
#include "FusedDotProduct.h"
#include "encoding.h"
#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include <time.h>

//int function1(int);

int main(int a0, int a1) {

	//uint64_t data0 = 0b0100000011101000000000000000000011000001101000000000000000000000; 
	//uint64_t data1 = 0b1100001010000001001000000000000001000000010100000000000000000000;
	uint64_t data[] = {0b0100000011101000000000000000000011000001101000000000000000000000,
			   0b1100001010000001001000000000000001000000010100000000000000000000};
	//uint64_t data[] = {0b0100000011100000000000000000000001000001110000000000000000000000,
	//		   0b0100000101000000000000000000000001000011000010000000000000000000};

	float a = 7.25;
	float b = -20;
	float c = -64.5625;
	float d = 3.25;

/*
	float a = 24;
	float b = 7;
	float c = 136;
	float d = 12;
*/
	float result;
	float Subresult;
	uint64_t FDPresult;

	unsigned long start, end, cycle_count;
	long i;
	//start = read_csr(mcycle);
	//result = a * b + c * d;
	//Subresult = a * b - c * d;
	//end = read_csr(mcycle); 
	//cycle_count = end - start;
	start = read_csr(mcycle);
	result = a * b + c * d;
	end = read_csr(mcycle);
	cycle_count = end - start; 
	print_str("[FPU] A*B + C =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	print_str("FPU cycles: ");	
	print_hex(*((uint64_t*)&cycle_count), 16);	
	print_str("\n");
/*
	print_str("[FPU] A*B + C*D =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	print_str("[FPU] A*B - C*D =  ");
	print_hex(*((int*)&Subresult), 8); 
	print_str("\n");
	result = a * b + c;
	print_str("[FPU] A*B + C =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	result = a * b - c;
	print_str("[FPU] A*B - C =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	result = a + c * d;
	print_str("[FPU] A + C*D =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	result = a - c * d;
	print_str("[FPU] A - C*D =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	result = a + c;
	print_str("[FPU] A + C =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
	result = a - c;
	print_str("[FPU] A - C =  ");
	print_hex(*((int*)&result), 8); 
	print_str("\n");
/*
	print_str("FPU cycles: ");
	print_hex(*((uint64_t*)&cycle_count), 16);  
	print_str("\n");
	print_str("'start' address is: ");
	print_hex((uint64_t)&start, 16);
	print_str("\n");
	print_str("'end' address is: ");
	print_hex((uint64_t)&end, 16);
	print_str("\n");
	print_str("cycle count address is: ");
	print_hex((uint64_t)&cycle_count, 16);
	print_str("\n");
/*
	print_str("cycle count address is: ");
	print_hex(((uint64_t)&end), 8);
	print_str("\n");
*/
/*
*/
	start = read_csr(mcycle);
	do_D0_Add(FDPresult, data[0], data[1]);
	end = read_csr(mcycle);
	cycle_count = end - start; 
	do_D0_Add(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A*B + C = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	print_str("Accelerator cycles: ");	
	print_hex(*((uint64_t*)&cycle_count), 16);	
	print_str("\n");
/*
	doAdd(FDPresult, data[0], data[1]);
	//start = read_csr(mcycle);
	doAdd(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A*B + C*D = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n");
	doSub(FDPresult, data[0], data[1]);
	doSub(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A*B - C*D = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	start = read_csr(mcycle);
	do_D0_Add(FDPresult, data[0], data[1]);
	end = read_csr(mcycle);
	cycle_count = end - start; 
	do_D0_Add(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A*B + C = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	do_D0_Sub(FDPresult, data[0], data[1]);
	do_D0_Sub(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A*B - C = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	do_B0_Add(FDPresult, data[0], data[1]);
	do_B0_Add(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A + C*D = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	do_B0_Sub(FDPresult, data[0], data[1]);
	do_B0_Sub(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A - C*D = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	do_B0_D0_Add(FDPresult, data[0], data[1]);
	do_B0_D0_Add(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A + C = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n"); 
	do_B0_D0_Sub(FDPresult, data[0], data[1]);
	do_B0_D0_Sub(FDPresult, data[0], data[1]);
	print_str("[Accelerator] A - C = ");
	print_hex(*((uint64_t*)&FDPresult), 16);
	print_str("\n");
/*
	//end = read_csr(mcycle); 
	//cycle_count = end - start;
	print_str("Accelerator cycles: ");	
	print_hex(*((uint64_t*)&cycle_count), 16);	
	print_str("\n");
	print_str("'start' address is: ");
	print_hex((uint64_t)&start, 16);
	print_str("\n");
	print_str("'end' address is: ");
	print_hex((uint64_t)&end, 16);
	print_str("\n");
	print_str("cycle count address is: ");
	print_hex((uint64_t)&cycle_count, 16);
	print_str("\n");
*/
	htif_poweroff();
	return 0;
}

