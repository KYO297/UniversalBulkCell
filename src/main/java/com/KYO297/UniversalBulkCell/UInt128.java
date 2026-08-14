package com.KYO297.UniversalBulkCell;

import java.math.BigInteger;

public class UInt128 extends Number {
    private long hi = 0;
    private long lo = 0;

    public UInt128(long hi, long lo) {
        this.hi = hi;
        this.lo = lo;
    }

    public UInt128() {
    }

    private static void longToByteArr(long val, byte[] ret, int start) {
        int idx = start + 7;
        while (val != 0) {
            ret[idx] = (byte) val;
            val = val >>> 8;
            idx--;
        }
    }

    public long insert(long amount, boolean simulate) {
        if (amount <= 0) return 0;

        if (simulate) {
            if (hi != -1 || lo >= 0 || ~lo >= amount) {
                return amount;
            } else {
                return ~lo;
            }
        } else {
            final long newLo = lo + amount;
            final long of = (lo & ~newLo) >>> 63;

            if (hi == -1 && of == 1) {
                final long ret = ~lo;
                lo = -1;
                return ret;
            } else {
                hi += of;
                lo = newLo;
                return amount;
            }
        }
    }

    public long extract(long amount, boolean simulate) {
        if (amount <= 0) return 0;

        if (simulate) {
            if (hi != 0 || lo < 0 || lo >= amount) {
                return amount;
            } else {
                return lo;
            }
        } else {
            final long newLo = lo - amount;
            final long uf = (~lo & newLo) >>> 63;

            if (hi == 0 && uf == 1) {
                final long ret = lo;
                lo = 0;
                return ret;
            } else {
                hi -= uf;
                lo = newLo;
                return amount;
            }
        }
    }

    @Override
    public double doubleValue() {
        final long mask = 0xffffffffL;
        final double d = Math.scalb((double) (hi >>> 32), 96);
        final double c = Math.scalb((double) (hi & mask), 64);
        final double b = Math.scalb((double) (lo >>> 32), 32);
        final double a = (double) (lo & mask);
        return a + b + c + d;
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public long longValue() {
        if (isLong()) return lo;
        return Long.MAX_VALUE;
    }

    @Override
    public int intValue() {
        if (isLong() && lo <= (long) Integer.MAX_VALUE) return (int) lo;
        return Integer.MAX_VALUE;
    }

    public BigInteger toBigInteger() {
        if (isLong()) return BigInteger.valueOf(lo);
        final byte[] ret = new byte[16];
        longToByteArr(hi, ret, 0);
        longToByteArr(lo, ret, 8);
        return new BigInteger(1, ret);
    }

    public long getHi() {
        return hi;
    }

    public long getLo() {
        return lo;
    }

    public boolean isEmpty() {
        return (hi == 0 && lo == 0);
    }

    public boolean isFull() {
        return (hi == -1 && lo == -1);
    }

    public boolean isLong() {
        return (hi == 0 && lo >= 0);
    }

    public double fillPercentage() {
        return doubleValue() / 3402823669209384634633746074317682114.55d;
    }

    public long[] divideByInt(int divisor) {
        long d = Integer.toUnsignedLong(divisor);

        // Split the 128-bit number into four 32-bit chunks (A3, A2, A1, A0)
        long a3 = hi >>> 32;
        long a2 = hi & 0xFFFFFFFFL;
        long a1 = lo >>> 32;
        long a0 = lo & 0xFFFFFFFFL;

        long rem = 0L;

        // Process each chunk from highest to lowest
        long chunk3 = (rem << 32) | a3;
        long q3 = Long.divideUnsigned(chunk3, d);
        rem = Long.remainderUnsigned(chunk3, d);

        long chunk2 = (rem << 32) | a2;
        long q2 = Long.divideUnsigned(chunk2, d);
        rem = Long.remainderUnsigned(chunk2, d);

        long chunk1 = (rem << 32) | a1;
        long q1 = Long.divideUnsigned(chunk1, d);
        rem = Long.remainderUnsigned(chunk1, d);

        long chunk0 = (rem << 32) | a0;
        long q0 = Long.divideUnsigned(chunk0, d);
        rem = Long.remainderUnsigned(chunk0, d);

        // Reassemble the quotient back into two 64-bit longs
        long qHi = (q3 << 32) | (q2 & 0xFFFFFFFFL);
        long qLo = (q1 << 32) | (q0 & 0xFFFFFFFFL);

        return new long[]{qHi, qLo, rem};
    }
}