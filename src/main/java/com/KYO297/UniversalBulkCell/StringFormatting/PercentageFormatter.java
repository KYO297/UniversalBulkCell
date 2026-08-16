package com.KYO297.UniversalBulkCell.StringFormatting;

import java.util.Locale;

public abstract class PercentageFormatter {
    public static String format(double value) {
        if (value == 0.0d) return "0 %";

        if (value >= 1e-3 && value < 1e3) {
            String format = "%." + (3 - (int) Math.ceil(Math.log10(value))) + "f %%";
            return String.format(Locale.US, format, value);
        }

        String format = "%.2E";
        String str = String.format(Locale.US, format, value);

        int eIndex = str.indexOf('E');
        if (eIndex == -1) return str;

        String base = str.substring(0, eIndex);
        String exponent = String.valueOf(Integer.parseInt(str.substring(eIndex + 1)));

        return base + " × 10" + toSuperscript(exponent) + " %";
    }

    private static String toSuperscript(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '-' -> sb.append('⁻');
                case '0' -> sb.append('⁰');
                case '1' -> sb.append('¹');
                case '2' -> sb.append('²');
                case '3' -> sb.append('³');
                case '4' -> sb.append('⁴');
                case '5' -> sb.append('⁵');
                case '6' -> sb.append('⁶');
                case '7' -> sb.append('⁷');
                case '8' -> sb.append('⁸');
                case '9' -> sb.append('⁹');
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
