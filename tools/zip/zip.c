#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h>

const char *NUMBERS = "0123456789";
const int MAX_LOAD_BYTES = 1048576;
const int MAX_KV_BYTES = 40;

typedef struct {
    unsigned int geo;
    unsigned int os;
    unsigned int cid;
    unsigned int hour;
} index_t;

typedef struct {
    int size;
    char *pos;
    char addr[MAX_LOAD_BYTES];
} buf_t;

buf_t g_buf;

void print_millis() {
    struct timeval t;
    gettimeofday(&t, NULL);
    printf("%ld.%06d\n", t.tv_sec, t.tv_usec);
}

// 9 + 3 + 14 + 5 bits
unsigned int pack_index(index_t idx) {
    unsigned int index = idx.geo;

    index <<= 3;
    index = idx.os;

    index <<= 14;
    index += idx.cid;

    index <<= 5;
    index += idx.hour;

    return index;
}

index_t unpack_index(unsigned int index) {
    index_t idx;

    idx.hour = index & 0x1f;
    index >>= 5;

    idx.cid = index & 0x3fff;
    index >>= 14;

    idx.os = index & 0x7;
    index >>= 3;

    idx.geo = index;

    return idx;
}

void test_pack_unpack(unsigned int n) {
    unsigned int index[2];
    index[0] = n;
    index_t idx = unpack_index(index[0]);
    index[1] = pack_index(idx);

    printf("index 0x%x --> 0x%x, match %d\n", index[0], index[1], index[0] == index[1]);
}


size_t read_lines(FILE *f) {
    g_buf.pos = g_buf.addr;
    size_t n = fread(g_buf.addr, 1, MAX_LOAD_BYTES, f);

    // EOF
    if (n < MAX_LOAD_BYTES) {
        g_buf.size = n;
        printf("read %ld bytes\n", n);
        return n;
    }

    // make sure line read
    while (n-- > 0) {
        if (g_buf.addr[n] == '\n') break;
    }
    g_buf.size = n;

    long offset = n;
    offset -= MAX_LOAD_BYTES;
    fseek(f, offset, SEEK_CUR);

    printf("read %ld bytes\n", n);
    return n;
}

char *read_until(const char *delims) {
    while (g_buf.pos++ < g_buf.addr + g_buf.size) {
        if (NULL != strchr(delims, *(g_buf.pos-1))) return g_buf.pos - 1;
    }
    return NULL;
}

char *read_index(void) {
    char *begin = read_until(NUMBERS);
    if (NULL == begin) return NULL;

    char *end = read_until("\t");
    if (NULL == end) return NULL;

    *end = '\0';
    return begin;
}

char *read_key(void) {
    char *begin = read_until("\"");
    if (NULL == begin) return NULL;

    char *end = read_until("\"");
    if (NULL == end) return NULL;

    *end = '\0';
    return begin+1;
}

char *read_value(void) {
    char *begin = read_until(NUMBERS);
    if (NULL == begin) return NULL;

    char *end = read_until(",}");
    if (NULL == end) return NULL;

    *end = '\0';
    return begin;
}

unsigned int parse_index(char *str) {
    index_t idx;
    sscanf(str, "%d-%d-%d-%d", &idx.geo, &idx.os, &idx.cid, &idx.hour);
    //printf("index %s, %d %d %d %d\n", str, idx.geo, idx.os, idx.cid, idx.hour);
    return pack_index(idx);
}

unsigned int parse_kv(char *kstr, char *vstr) {
    unsigned int k = atoi(kstr);
    unsigned int v = lround(atof(vstr)*100000);
    //printf("k %s %d v %s %d\n", kstr, k, vstr, v);
    return (k << 17) + v;
}

void test_parse_index(char *str) {
    unsigned int index = parse_index(str);
    index_t idx = unpack_index(index);
    printf("index %s %d geo %d os %d cid %d hour %d\n",
            str, index, idx.geo, idx.os, idx.cid, idx.hour);
}

void parse_lines(FILE *fidx, FILE *fout) {
    char *kstr = NULL;
    char *vstr = NULL;

    while (NULL != (kstr = read_index())) {
        unsigned int uint = parse_index(kstr);
        fwrite(&uint, sizeof(uint), 1, fidx);
        do {
            if (NULL != (kstr = read_key()) && NULL != (vstr = read_value())) {
                uint = parse_kv(kstr, vstr);
                fwrite(&uint, sizeof(uint), 1, fout);
            }
        } while (*g_buf.pos != '\n');
    }
}

int do_index(char *argv[]) {
    char fname[256];
    strcpy(fname, argv[0]);
    FILE *fin = fopen(fname, "r");

    strcat(fname, ".dat");
    FILE *fout = fopen(fname, "w");

    strcat(fname, ".idx");
    FILE *fidx = fopen(fname, "w");

    while (read_lines(fin) > 0) {
        parse_lines(fidx, fout);
    }

    fclose(fin);
    fclose(fout);
    fclose(fidx);
    return 0;
}

long do_search(char *argv[]) {
    char fname[256];
    strcpy(fname, argv[0]);
    FILE *fdata = fopen(fname, "r");

    strcat(fname, ".idx");
    FILE *fidx = fopen(fname, "r");

    unsigned int index = parse_index(argv[1]);

    // binary search
    long begin = ftell(fidx);
    fseek(fidx, 0, SEEK_END);
    long end = ftell(fidx) / sizeof(index) - 1;
    //printf("begin %ld end %ld \n", begin, end);

    print_millis();

    unsigned int uint = 0;
    long pos = -1;
    do {
        pos = (begin + end) / 2;
        fseek(fidx, pos << 2, SEEK_SET);
        fread(&uint, sizeof(uint), 1, fidx);
        //printf("pos %ld value %d\n", pos, uint);
        if (index < uint) {
            end = pos - 1;
        } else if (index > uint) {
            begin = pos + 1;
        }
    } while (begin <= end && uint != index);

    print_millis();

    if (uint == index) {
        unsigned int buf[MAX_KV_BYTES/4];
        fseek(fdata, pos * MAX_KV_BYTES, SEEK_SET);
        fread(buf, 1, MAX_KV_BYTES, fdata);

        print_millis();

        for (int i = 0; i < MAX_KV_BYTES/4; i++) {
            printf("%d: %1.5f, ", buf[i] >> 17, (buf[i] & 0x1ffff) / 100000.0);
        }
        puts("\n");
    } else {
        pos = -1;
    }

    fclose(fdata);
    fclose(fidx);

    printf("index %s %d pos %ld\n", argv[1], index, pos);
    return pos;
}

// prog cmd params...
int main(int argc, char *argv[]) {
    if (0 == strcmp(argv[1], "index")) {
        do_index(argv+2);
    } else if (0 == strcmp(argv[1], "search")) {
        do_search(argv+2);
    }
    return 0;
}
