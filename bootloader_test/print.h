#ifndef PRINT_H
#define PRINT_H

#include <stdint.h>
#include <stdbool.h>

void print_chr(char ch);
void print_str(const char *s);
void print_dec(unsigned int val);
//void print_hex(unsigned int val, int digits);
void print_hex(uint64_t val, int digits);


#endif
