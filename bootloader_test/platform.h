// See LICENSE for license details.

#ifndef _SIFIVE_PLATFORM_H
#define _SIFIVE_PLATFORM_H

#include "const.h"
#include "devices/clint.h"
#include "devices/plic.h"

// Some things missing from the official encoding.h
#define MCAUSE_INT         0x80000000
#define MCAUSE_CAUSE       0x7FFFFFFF

/****************************************************************************
 * Platform definitions
 *****************************************************************************/

// Memory map
#define MASKROM_MEM_ADDR _AC(0x00010000,UL)
#define CLINT_CTRL_ADDR _AC(0x02000000,UL)
#define PLIC_CTRL_ADDR _AC(0x0C000000,UL)
#define MEM_CTRL_ADDR _AC(0x80000000,UL)

// Helper functions
#define _REG32(p, i) (*(volatile uint32_t *) ((p) + (i)))
#define _REG32P(p, i) ((volatile uint32_t *) ((p) + (i)))
#define CLINT_REG(offset) _REG32(CLINT_CTRL_ADDR, offset)
#define PLIC_REG(offset) _REG32(PLIC_CTRL_ADDR, offset)

// Misc

#include <stdint.h>

#endif /* _SIFIVE_PLATFORM_H */
