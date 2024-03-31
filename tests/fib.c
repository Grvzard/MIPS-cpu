
static char *str = "xxx\x06 xx";
static unsigned int seg7addr = 0xA0000000;

static void display(unsigned int num) { *(unsigned int *)seg7addr = num; }

static int func(int n) {
    int a, b, c;
    a = 1;
    b = 1;
    for (int i = 2; i < n; i++) {
        c = a + b;
        a = b;
        b = c;
        display(b);
    }
    return b;
}

void _main() {
    int *p = (int *)0x80;
    *p = func((int)(str[3]));
}
