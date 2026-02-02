package com.zaw.workflow.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class FlowInstanceFormatUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FlowInstanceFormatUtils() {
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date firstNonNullDate(Date first, Date second) {
        return first != null ? first : second;
    }

    public static String formatDuration(Date startTime, Date endTime) {
        if (startTime == null) {
            return null;
        }
        long endMillis = endTime != null ? endTime.getTime() : System.currentTimeMillis();
        long durationMillis = Math.max(0, endMillis - startTime.getTime());
        if (durationMillis < 1000) {
            return durationMillis + "ms";
        }
        double seconds = durationMillis / 1000.0;
        if (seconds < 60) {
            return String.format("%.1fs", seconds);
        }
        long totalSeconds = durationMillis / 1000;
        long minutes = totalSeconds / 60;
        long secondsPart = totalSeconds % 60;
        if (minutes < 60) {
            return String.format("%dm%ds", minutes, secondsPart);
        }
        long hours = minutes / 60;
        long minutesPart = minutes % 60;
        return String.format("%dh%dm%ds", hours, minutesPart, secondsPart);
    }
}
