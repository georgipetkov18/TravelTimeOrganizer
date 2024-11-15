package com.example.traveltimeorganizer.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    public static String formatTripTime(double seconds) {
        long secondsRounded = Math.round(seconds);
        long hours = secondsRounded / 3600;
        long minutes = (secondsRounded - (hours * 3600)) / 60;
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d ч. %d мин.", hours, minutes);
        }
        else {
            return String.format(Locale.getDefault(), "%d мин.", minutes);
        }
    }
}
