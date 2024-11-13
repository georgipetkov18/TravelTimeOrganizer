package com.example.traveltimeorganizer.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String formatTripTime(double seconds) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return formatter.format(LocalTime.ofSecondOfDay((long)seconds));
    }
}
