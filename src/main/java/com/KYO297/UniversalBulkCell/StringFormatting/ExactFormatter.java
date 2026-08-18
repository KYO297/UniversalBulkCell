package com.KYO297.UniversalBulkCell.StringFormatting;

import com.KYO297.UniversalBulkCell.Cell.UInt128;

public abstract class ExactFormatter {
    public static String format(long hi, long lo) {
        final long M32 = 0xFFFFFFFFL;
        final int len = 52;
        final char[] buf = new char[len];
        int pos = len;

        if (hi == 0 && lo == 0) return "0";

        while (hi != 0 || lo != 0) {
            if (pos % 4 == 1) buf[--pos] = ' ';

            long a3 = hi >>> 32, a2 = hi & M32;
            long a1 = lo >>> 32, a0 = lo & M32;

            long n, r, q3, q2, q1, q0;

            n = a3;
            q3 = n / 10;
            r = n % 10;

            n = (r << 32) | a2;
            q2 = n / 10;
            r = n % 10;

            n = (r << 32) | a1;
            q1 = n / 10;
            r = n % 10;

            n = (r << 32) | a0;
            q0 = n / 10;
            r = n % 10;

            buf[--pos] = (char) ('0' + r);
            hi = (q3 << 32) | q2;
            lo = (q1 << 32) | q0;
        }

        return new String(buf, pos, len - pos);
    }

    public static String format(UInt128 val) {
        return format(val.getHi(), val.getLo());
    }
}
