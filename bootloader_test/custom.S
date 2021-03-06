#include "custom_insn.hS"

.section .text
.globl function1

function1:
	custom0_insn(a0, a0)

.section .data
var1:
	.long 0
var2:
	.long 0
