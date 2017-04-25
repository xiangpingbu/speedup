def convert_n_bytes(n, b):
    bits = b*8
    return (n + 2**(bits-1)) % 2**bits - 2**(bits-1)

def convert_4_bytes(n):
    return convert_n_bytes(n, 4)

def getHashCode(s):
    h = 0
    n = len(s)
    for i, c in enumerate(s):
        h = h + ord(c)*31**(n-1-i)
    return convert_4_bytes(h)

with open('/Users/lifeng/Downloads/Pruned_new.tsv') as fp:
    for line in fp:
        data = line.split('\t')
        key = data[0]
        code = getHashCode(key)

        table = "ccnb_v2_"+ str(abs(code)% 10)
        print '%s %s' %(key,abs(code)% 10)