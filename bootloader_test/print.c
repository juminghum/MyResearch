#include "print.h"
#include "htif.h"

#define IN_INIT_SECTION __attribute__((section(".init")))

void IN_INIT_SECTION print_chr(char ch)
{
	htif_console_putchar(ch);
}

void IN_INIT_SECTION print_str(const char *p)
{
	while (*p != 0)
		htif_console_putchar( *(p++) );
}

void IN_INIT_SECTION print_dec(unsigned int val)
{
	char buffer[10];
	char *p = buffer;
	while (val || p == buffer) {
		*(p++) = val % 10;
		val = val / 10;
	}
	while (p != buffer) {
		htif_console_putchar( '0' + *(--p) );
	}
}

//void IN_INIT_SECTION print_hex(unsigned int val, int digits)
void IN_INIT_SECTION print_hex(uint64_t val, int digits)
{
	for (int i = (4*digits)-4; i >= 0; i -= 4)
		htif_console_putchar("0123456789ABCDEF"[(val >> i) % 16]);
}
