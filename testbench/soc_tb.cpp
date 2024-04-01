#include "VSoc.h"
#include <verilated.h>
#include <verilated_vcd_c.h>

vluint64_t sim_time = 0;

#define CYCLE 2

static inline void dut_half(VSoc *dut) {
  for (int i = 0; i < CYCLE; i++) {
    dut->clock ^= 1;
    dut->eval();
  }
}

int main(int argc, char **argv) {
  VerilatedContext *contextp = new VerilatedContext;
  contextp->commandArgs(argc, argv);
  VSoc *dut = new VSoc{contextp};

  Verilated::traceEverOn(true);
  VerilatedVcdC *m_trace = new VerilatedVcdC;
  dut->trace(m_trace, 5);
  m_trace->open("wave.vcd");
  dut->clock = 1;

  dut->reset = 1;
  dut_half(dut);
  dut_half(dut);
  dut->reset = 0;

  while (sim_time <= 480) {
    dut_half(dut);
    m_trace->dump(sim_time);
    sim_time++;
  }

  m_trace->close();
  delete dut;
  delete contextp;
  return 0;
}