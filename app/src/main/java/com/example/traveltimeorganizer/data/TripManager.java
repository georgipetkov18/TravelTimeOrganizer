package com.example.traveltimeorganizer.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.utils.Constants;

import java.util.ArrayList;
import java.util.Locale;

public class TripManager extends SQLiteOpenHelper implements DatabaseModelManager {


    public TripManager(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(String.format(Locale.getDefault(), "DROP TABLE %s", Constants.TABLE_TRIPS_NAME));
        onCreate(sqLiteDatabase);
    }

    public void addTrip(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(Constants.FROM_PLACE, trip.getFromPlace());
        values.put(Constants.TO_PLACE, trip.getToPlace());
        values.put(Constants.FROM_LATITUDE, trip.getFromLatitude());
        values.put(Constants.FROM_LONGITUDE, trip.getFromLongitude());
        values.put(Constants.TO_LATITUDE, trip.getToLatitude());
        values.put(Constants.TO_LONGITUDE, trip.getToLongitude());
        values.put(Constants.IS_ACTIVE, trip.isActive());
        values.put(Constants.TRIP_TIME, trip.getTripTime());
        values.put(Constants.MIN_EARLIER, trip.getMinEarlier());
        values.put(Constants.REPEAT_ON_DAY, trip.getRepeatOnDay());
        values.put(Constants.REPEAT_ON_TIME, trip.getRepeatOnTime());
        values.put(Constants.EXECUTE_ON, trip.getExecuteOn());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert(Constants.TABLE_TRIPS_NAME, null, values);
    }

    public void setActiveStatus(int id, boolean isActive) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.IS_ACTIVE, isActive);
        sqLiteDatabase.update(Constants.TABLE_TRIPS_NAME, values, "id = ?", new String[]{Integer.toString(id)});
    }

    public String getCreateTableQuery() {
        return String.format(Locale.getDefault(),"CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT NULL, %s TEXT NULL, " +
                "%s REAL NOT NULL, %s REAL NOT NULL, %s REAL NOT NULL, %s REAL NOT NULL, %s BOOLEAN NOT NULL, %s REAL NOT NULL, %s INTEGER NOT NULL, %s INTEGER NULL, %s TEXT NULL, %s TEXT NULL)",
                Constants.TABLE_TRIPS_NAME, Constants.ID, Constants.FROM_PLACE, Constants.TO_PLACE, Constants.FROM_LATITUDE, Constants.FROM_LONGITUDE, Constants.TO_LATITUDE, Constants.TO_LONGITUDE, Constants.IS_ACTIVE, Constants.TRIP_TIME,
                Constants.MIN_EARLIER, Constants.REPEAT_ON_DAY, Constants.REPEAT_ON_TIME, Constants.EXECUTE_ON);
    }

    @SuppressLint("Range")
    public ArrayList<Trip> getTrips() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = {
                Constants.ID, Constants.FROM_PLACE, Constants.TO_PLACE, Constants.FROM_LATITUDE, Constants.FROM_LONGITUDE,
                Constants.TO_LATITUDE, Constants.TO_LONGITUDE, Constants.IS_ACTIVE, Constants.TRIP_TIME,
                Constants.MIN_EARLIER, Constants.REPEAT_ON_DAY, Constants.REPEAT_ON_TIME, Constants.EXECUTE_ON
        };
        Cursor cursor = sqLiteDatabase.query(Constants.TABLE_TRIPS_NAME, columns, null, null, null, null, null);

        ArrayList<Trip> trips = new ArrayList<>();
        while (cursor.moveToNext()) {
            Trip trip = new Trip();
            trip.setId(cursor.getInt(cursor.getColumnIndex(Constants.ID)));
            trip.setFromPlace(cursor.getString(cursor.getColumnIndex(Constants.FROM_PLACE)));
            trip.setToPlace(cursor.getString(cursor.getColumnIndex(Constants.TO_PLACE)));
            trip.setFromLatitude(cursor.getDouble(cursor.getColumnIndex(Constants.FROM_LATITUDE)));
            trip.setFromLongitude(cursor.getDouble(cursor.getColumnIndex(Constants.FROM_LONGITUDE)));
            trip.setToLatitude(cursor.getDouble(cursor.getColumnIndex(Constants.TO_LATITUDE)));
            trip.setToLongitude(cursor.getDouble(cursor.getColumnIndex(Constants.TO_LONGITUDE)));
            trip.setActive(cursor.getInt(cursor.getColumnIndex(Constants.IS_ACTIVE)) > 0);
            trip.setTripTime(cursor.getDouble(cursor.getColumnIndex(Constants.TRIP_TIME)));
            trip.setMinEarlier(cursor.getInt(cursor.getColumnIndex(Constants.MIN_EARLIER)));
            trip.setRepeatOnDay(cursor.getInt(cursor.getColumnIndex(Constants.REPEAT_ON_DAY)));
            trip.setRepeatOnTime(cursor.getString(cursor.getColumnIndex(Constants.REPEAT_ON_TIME)));
            trip.setExecuteOn(cursor.getString(cursor.getColumnIndex(Constants.EXECUTE_ON)));
            trips.add(trip);
        }
        cursor.close();

        return trips;
    }
}
