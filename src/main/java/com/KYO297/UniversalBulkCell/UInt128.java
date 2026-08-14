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

    @Override
    public String toString() {
        if (isEmpty()) return "0";

        final long M32 = 0xFFFFFFFFL;   // isolates the low 32 bits as unsigned
        char[] buf = new char[39];      // 2^128 − 1 has 39 decimal digits
        int pos = 39;

        long cHi = hi, cLo = lo;

        while (cHi != 0 || cLo != 0) {

            // Decompose into four unsigned 32-bit limbs, most-significant first.
            long a3 = cHi >>> 32,   a2 = cHi & M32;
            long a1 = cLo >>> 32,   a0 = cLo & M32;

            // --- base-2^32 long division by 10 ---
            // n = (remainder_from_previous_limb << 32) | current_limb
            // n is always < 10 * 2^32 < 2^36, so it fits in a positive long.
            long n, r, q3, q2, q1, q0;

            n = a3;                 q3 = n / 10;  r = n % 10;
            n = (r << 32) | a2;     q2 = n / 10;  r = n % 10;
            n = (r << 32) | a1;     q1 = n / 10;  r = n % 10;
            n = (r << 32) | a0;     q0 = n / 10;  r = n % 10;

            buf[--pos] = (char) ('0' + r);    // lowest decimal digit of current value
            cHi = (q3 << 32) | q2;
            cLo = (q1 << 32) | q0;
        }

        return new String(buf, pos, 39 - pos);
    }
}