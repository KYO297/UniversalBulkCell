package com.KYO297.UniversalBulkCell.StringFormatting;

public abstract class MetricFormatter {
    private static final String[] PREFIXES = {"y", "z", "a", "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"};

    private static final double[] POWERS_OF_10 = {1e-24, 1e-21, 1e-18, 1e-15, 1e-12, 1e-9, 1e-6, 1e-3, 1.0, 1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24, 1e27, 1e30};

    private static final int ZERO_INDEX = 8;
    private static final int MAX_INDEX = PREFIXES.length - 1;

    public static String format(double value) {
        if (value == 0) return "0";

        int prefixIndex = (int) Math.floor(Math.log10(value) / 3.0) + ZERO_INDEX;

        if (prefixIndex < 0) prefixIndex = 0;
        else if (prefixIndex > MAX_INDEX) prefixIndex = MAX_INDEX;

        double scaled = value / POWERS_OF_10[prefixIndex];

        if (scaled >= 999.5 && prefixIndex < MAX_INDEX) {
            scaled /= 1000.0;
            prefixIndex++;
        }

        char[] buf = new char[8];
        int len;

        if (scaled >= 99.5) {
            long val = Math.round(scaled);
            buf[0] = (char) ('0' + (val / 100));
            buf[1] = (char) ('0' + ((val / 10) % 10));
            buf[2] = (char) ('0' + (val % 10));
            len = 3;
        } else if (scaled >= 9.95) {
            long val = Math.round(scaled * 10.0);
            buf[0] = (char) ('0' + (val / 100));
            buf[1] = (char) ('0' + ((val / 10) % 10));
            buf[2] = '.';
            buf[3] = (char) ('0' + (val % 10));
            len = 4;
        } else {
            long val = Math.round(scaled * 100.0);
            buf[0] = (char) ('0' + (val / 100));
            buf[1] = '.';
            buf[2] = (char) ('0' + ((val / 10) % 10));
            buf[3] = (char) ('0' + (val % 10));
            len = 4;
        }

        String prefix = PREFIXES[prefixIndex];
        if (!prefix.isEmpty()) {
            buf[len++] = ' ';
            buf[len++] = prefix.charAt(0);
        }

        return new String(buf, 0, len);
    }
}
