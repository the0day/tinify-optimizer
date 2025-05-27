package com.the0day.tinify.util;

public class StringFormatUtil {
    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, false);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit * 10) return String.format("%,d B", bytes);
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String humanReadableKb(long bytes) {
        if (bytes < 10240) {
            return String.format("%,d B", bytes);
        }
        double kb = bytes / 1024.0;
        return String.format("%.1f KB", kb);
    }
}
