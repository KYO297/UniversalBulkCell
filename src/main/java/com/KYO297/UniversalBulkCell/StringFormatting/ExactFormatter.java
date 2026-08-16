package com.KYO297.UniversalBulkCell.StringFormatting;

import com.KYO297.UniversalBulkCell.Cell.UInt128;

public abstract class ExactFormatter {
    public static String format(UInt128 value) {
        final long M32 = 0xFFFFFFFFL;
        final char[] buf = new char[39];
        int pos = 39;

        long cHi = value.getHi(), cLo = value.getLo();

        while (cHi != 0 || cLo != 0) {
            long a3 = cHi >>> 32, a2 = cHi & M32;
            long a1 = cLo >>> 32, a0 = cLo & M32;

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
            cHi = (q3 << 32) | q2;
            cLo = (q1 << 32) | q0;
        }

        return new String(buf, pos, 39 - pos);
    }
}
