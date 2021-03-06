PREFIX=/opt/riscv64gc/bin
CC=$(PREFIX)/riscv64-unknown-elf-gcc
OBJDUMP=$(PREFIX)/riscv64-unknown-elf-objdump

TARGET=final.elf

ASM_SRCS += start.S
ASM_SRCS += entry.S
ASM_SRCS += custom.S
C_SRCS += init.c
C_SRCS += main.c
C_SRCS += htif.c
C_SRCS += print.c
#C_SRCS += FusedDotProduct.c

LINKER_SCRIPT := riscv.lds

INCLUDES += -I./
#INCLUDES += -I/usr/include

LDFLAGS += -T $(LINKER_SCRIPT) -nostartfiles #-nostdlib
#LDFLAGS += -L$(ENV_DIR) --specs=nano.specs
LDFLAGS += -fno-builtin-printf -static -lgcc

ASM_OBJS := $(ASM_SRCS:.S=.o)
C_OBJS := $(C_SRCS:.c=.o)

LINK_OBJS += $(ASM_OBJS) $(C_OBJS)
LINK_DEPS += $(LINKER_SCRIPT)

CLEAN_OBJS += $(TARGET) $(LINK_OBJS)

CFLAGS += -g
CFLAGS += -march=rv64ima
CFLAGS += -mabi=lp64
CFLAGS += -mcmodel=medany
#CFLAGS += --msoft-float

$(TARGET): $(LINK_OBJS) $(LINK_DEPS)
	$(CC) $(CFLAGS) $(INCLUDES) $(LINK_OBJS) -o $@ $(LDFLAGS)
	$(OBJDUMP) -d $@ > $@.dump

$(ASM_OBJS): %.o: %.S $(HEADERS)
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

$(C_OBJS): %.o: %.c $(HEADERS)
	$(CC) $(CFLAGS) $(INCLUDES) -include sys/cdefs.h -c -o $@ $<

.PHONY: clean
clean:
	rm -f $(CLEAN_OBJS) *.dump

