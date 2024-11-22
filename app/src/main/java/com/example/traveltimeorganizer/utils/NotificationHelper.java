package com.example.traveltimeorganizer.utils;

import static java.time.DayOfWeek.*;
import static java.time.temporal.TemporalAdjusters.next;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.provider.Settings;
import android.util.ArrayMap;
import android.widget.TextView;

import com.example.traveltimeorganizer.data.models.Trip;
import com.google.android.material.timepicker.TimeFormat;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationHelper {
    private static NotificationManager notificationManager = null;
    private static AlarmManager alarmManager = null;
    private static final Map<Integer, PendingIntent> pendingIntents = new HashMap<>();

    public static void createNotificationChannel(Context context) {
        String name = "Travel time organizer notification channel";
        String desc = "Channel used to send trip notifications.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(AppNotification.appChannel, name, importance);
        channel.setDescription(desc);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static boolean checkNotificationPermissions(Context context) {
        boolean isEnabled = notificationManager.areNotificationsEnabled();

        if (!isEnabled) {
            // Open the app notification settings if notifications are not enabled
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);

            return false;
        }
        return true;
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleNotification(Context context, Trip trip) {
        if (pendingIntents.get(trip.getId()) != null) {
            return;
        }

        Intent intent = new Intent(context, AppNotification.class);

        String title = "TravelTimeOrganizer";
        String from = trip.getFromPlace() != null ? trip.getFromPlace() : trip.getFromLatitude() + ", " + trip.getFromLongitude();
        String to = trip.getToPlace() != null ? trip.getToPlace() : trip.getToLatitude() + ", " + trip.getToLongitude();
        String message = String.format(Locale.getDefault(), "Предприемете Вашето пътуване от %s до %s сега.", from, to);

        intent.putExtra(AppNotification.notificationTitleVar, title);
        intent.putExtra(AppNotification.notificationMessageVar, message);
        intent.putExtra(AppNotification.currentTripVar, trip);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                AppNotification.notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );


        if (trip.getRepeatOnDay() == null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    getTimeExact(trip),
                    pendingIntent
            );
        } else if (trip.getRepeatOnDay() == 0) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    getTimeOnce(trip),
                    pendingIntent
            );
        } else {
            for (Map.Entry<Integer, String> entry : Constants.DAY_OF_WEEK_INDEX_MAP.entrySet()) {
                if ((trip.getRepeatOnDay() & entry.getKey()) == entry.getKey()) {
                    DayOfWeek dayOfWeek;
                    switch (entry.getKey()) {
                        case 2:
                            dayOfWeek = MONDAY;
                            break;

                        case 4:
                            dayOfWeek = TUESDAY;
                            break;

                        case 8:
                            dayOfWeek = WEDNESDAY;
                            break;

                        case 16:
                            dayOfWeek = THURSDAY;
                            break;

                        case 32:
                            dayOfWeek = FRIDAY;
                            break;

                        case 64:
                            dayOfWeek = SATURDAY;
                            break;

                        case 128:
                            dayOfWeek = SUNDAY;
                            break;

                        default:
                            return;
                    }
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            getTimeRepeating(trip, dayOfWeek),
                            AlarmManager.INTERVAL_DAY * 7,
                            pendingIntent
                    );
                }
            }
        }


        pendingIntents.put(trip.getId(), pendingIntent);
    }

    public static void cancelNotification(int tripId) {
        PendingIntent current = pendingIntents.get(tripId);
        if (current == null) {
            return;
        }
        alarmManager.cancel(current);
        pendingIntents.remove(tripId);
    }

    private static long getTimeExact(Trip trip) {
        if (trip.getExecuteOn() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(trip.getExecuteOn(), formatter);
            Instant instant = dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli() - ((long) trip.getTripTime() * 1000) - ((long) trip.getMinEarlier() * 60 * 1000);
        }
        return 0;
    }

    private static long getTimeOnce(Trip trip) {
        if (trip.getRepeatOnTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime alarmTime = LocalTime.parse(trip.getRepeatOnTime(), formatter);
            LocalDateTime alarmDateTime = LocalDate.now().atTime(alarmTime);
            LocalDateTime now = LocalDateTime.now();
            Instant instant = now.isAfter(alarmDateTime) ?
                    alarmDateTime.plusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant() :
                    alarmDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();

            return instant.toEpochMilli() - ((long) trip.getTripTime() * 1000) - ((long) trip.getMinEarlier() * 60 * 1000);
        }

        return 0;
    }

    private static long getTimeRepeating(Trip trip, DayOfWeek dayofWeek) {
        if (trip.getRepeatOnTime() != null && trip.getRepeatOnDay() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime alarmTime = LocalTime.parse(trip.getRepeatOnTime(), formatter);
            LocalDateTime alarmDateTime = LocalDate.now().atTime(alarmTime);

            if (LocalDateTime.now().isAfter(alarmDateTime)) {
                alarmDateTime = alarmDateTime.with(next(dayofWeek));
            }

            Instant instant = alarmDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli() - ((long) trip.getTripTime() * 1000) - ((long) trip.getMinEarlier() * 60 * 1000);
        }

        return 0;
    }
}