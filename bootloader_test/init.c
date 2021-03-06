#include <stdint.h>
#include <stdio.h>
#include <unistd.h>

#include "platform.h"
#include "encoding.h"
#include "print.h"
#include "htif.h"

extern int main(int argc, char** argv);
extern void trap_entry();

static unsigned long mtime_lo(void)
{
  return *(volatile unsigned long *)(CLINT_CTRL_ADDR + CLINT_MTIME);
}

#ifdef __riscv32

static uint32_t mtime_hi(void)
{
  return *(volatile uint32_t *)(CLINT_CTRL_ADDR + CLINT_MTIME + 4);
}

uint64_t get_timer_value()
{
  while (1) {
    uint32_t hi = mtime_hi();
    uint32_t lo = mtime_lo();
    if (hi == mtime_hi())
      return ((uint64_t)hi << 32) | lo;
  }
}

#else /* __riscv32 */

uint64_t get_timer_value()
{
  return mtime_lo();
}

#endif


#ifdef USE_PLIC
extern void handle_m_ext_interrupt();
#endif

#ifdef USE_M_TIME
extern void handle_m_time_interrupt();
#endif

uintptr_t handle_trap(uintptr_t mcause, uintptr_t epc)
{
  if (0){
#ifdef USE_PLIC
    // External Machine-Level interrupt from PLIC
  } else if ((mcause & MCAUSE_INT) && ((mcause & MCAUSE_CAUSE) == IRQ_M_EXT)) {
    handle_m_ext_interrupt();
#endif
#ifdef USE_M_TIME
    // External Machine-Level interrupt from PLIC
  } else if ((mcause & MCAUSE_INT) && ((mcause & MCAUSE_CAUSE) == IRQ_M_TIMER)){
    handle_m_time_interrupt();
#endif
  }
  else {
    print_str("Trap! Sorry...\nMCAUSE: ");
    print_hex(mcause, 8);
    print_str("\nMEPC: ");
    print_hex(epc, 8);
    print_str("\nWell bye...\n");
    htif_poweroff();
  }
  return epc;
}

void _init()
{
  
  #ifndef NO_INIT
  write_csr(mtvec, &trap_entry);
  if (read_csr(misa) & (1 << ('F' - 'A'))) { // if F extension is present
    write_csr(mstatus, MSTATUS_FS | MSTATUS_XS); // allow FPU instructions without trapping, and also the custom instruction
    write_csr(fcsr, 0); // initialize rounding mode, undefined at reset
  }
  else {
     write_csr(mstatus, MSTATUS_XS); // allow custom instructions without trapping
  } 
  #endif
  
}

void _fini()
{
}
