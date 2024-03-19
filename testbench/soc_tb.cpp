#include "VSoc.h"
#include <verilated.h>
#include <verilated_vcd_c.h>

vluint64_t sim_time = 0;

int main(int argc, char **argv) {
  VerilatedContext *contextp = new VerilatedContext;
  contextp->commandArgs(argc, argv);
  VSoc *dut = new VSoc{contextp};

  Verilated::traceEverOn(true);
  VerilatedVcdC *m_trace = new VerilatedVcdC;
  dut->trace(m_trace, 5);
  m_trace->open("wave.vcd");

  dut->reset = 1;
  dut->clock = 0;
  dut->eval();
  dut->clock = 1;
  dut->eval();
  dut->reset = 0;

  while (sim_time < 340) {
    dut->clock ^= 1;
    dut->eval();
    m_trace->dump(sim_time);
    sim_time++;
  }

  m_trace->close();
  delete dut;
  delete contextp;
  return 0;
}