
static char *str = "xxx\x06 xx";

static int func(int n) {
    int a, b, c;
    a = 1;
    b = 1;
    for (int i = 2; i < n; i++) {
        c = a + b;
        a = b;
        b = c;
    }
    return b;
}

void _main() {
    int *p = (int *)0x80;
    *p = func((int)(str[3]));
}
