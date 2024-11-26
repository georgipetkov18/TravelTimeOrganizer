package com.example.traveltimeorganizer.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.example.traveltimeorganizer.R;
import com.example.traveltimeorganizer.TripViewActivity;
import com.example.traveltimeorganizer.data.TripManager;
import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.data.models.TripType;

import java.util.function.Consumer;

public class AppNotification extends BroadcastReceiver {
    public static final String appChannel = "appChannel";
    public static final String notificationTitleVar = "notificationTitle";
    public static final String notificationMessageVar = "notificationMessage";
    public static final String currentTripVar = "currentTrip";
    public static Consumer<Integer> statusChangeCallback;
    public static Consumer<Integer> deleteCallback;

    @Override
    public void onReceive(Context context, Intent intent) {
        TripManager tripManager = new TripManager(context);
        Trip current = (Trip) intent.getSerializableExtra(currentTripVar);

        Intent clickIntent = new Intent(context, TripViewActivity.class);
        clickIntent.putExtra(Constants.TRIP, current);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(context, appChannel)
                .setSmallIcon(R.drawable.baseline_map_24)
                .setContentTitle(intent.getStringExtra(notificationTitleVar))
                .setContentText(intent.getStringExtra(notificationMessageVar))
                .setContentIntent(pendingIntent)
                .build();

        new Handler(Looper.getMainLooper()).post(() -> {
            if (current != null) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(current.getId(), notification);
                if (current.getTripType() == TripType.repeatableOnce) {
                    tripManager.setActiveStatus(current.getId(), false);
                    statusChangeCallback.accept(current.getId());
                }
                else if (current.getTripType() == TripType.exactDate) {
                    tripManager.deleteTrip(current.getId());
                    deleteCallback.accept(current.getId());
                }
            }

        });
    }
}
