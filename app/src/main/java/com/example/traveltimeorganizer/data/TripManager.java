package com.example.traveltimeorganizer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.utils.Constants;

import java.util.Locale;

public class TripManager extends SQLiteOpenHelper implements DatabaseModelManager {


    public TripManager(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addTrip(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(Constants.FROM_PLACE, trip.getFromPlace());
        values.put(Constants.TO_PLACE, trip.getToPlace());
        values.put(Constants.LATITUDE, trip.getFromLatitude());
        values.put(Constants.LONGITUDE, trip.getFromLongitude());
        values.put(Constants.IS_ACTIVE, trip.isActive());
        values.put(Constants.TRIP_TIME, trip.getTripTime());
        values.put(Constants.MIN_EARLIER, trip.getMinEarlier());
        values.put(Constants.REPEAT_ON, trip.getRepeatOn());
        values.put(Constants.EXECUTE_ON, trip.getExecuteOn());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert(Constants.TABLE_TRIPS_NAME, null, values);
    }

    public String getCreateTableQuery() {
        return String.format(Locale.getDefault(),"CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT NULL, %s TEXT NULL, " +
                "%s REAL NOT NULL, %s REAL NOT NULL, %s BOOLEAN NOT NULL, %s REAL NOT NULL, %s INTEGER NOT NULL, %s INTEGER NULL, %s TEXT NULL)",
                Constants.TABLE_TRIPS_NAME, Constants.ID, Constants.FROM_PLACE, Constants.TO_PLACE, Constants.LATITUDE, Constants.LONGITUDE, Constants.IS_ACTIVE, Constants.TRIP_TIME,
                Constants.MIN_EARLIER, Constants.REPEAT_ON, Constants.EXECUTE_ON);
    }
}
