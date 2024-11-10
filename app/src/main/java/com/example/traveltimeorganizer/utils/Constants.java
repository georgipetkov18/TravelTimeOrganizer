package com.example.traveltimeorganizer.utils;

import org.osmdroid.util.GeoPoint;

public class Constants {
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TEXT = "text";
    public static final GeoPoint CENTER_OF_BG = new GeoPoint(42.76, 25.23);
    public static final int AUTOCOMPLETE_TEXT_VIEW_DEFAULT_THRESHOLD = 3;
    public static final int AUTOCOMPLETE_TEXT_VIEW_DEFAULT_RESULTS_COUNT = 4;
    public static final String DATABASE_NAME = "TravelTimeOrganizerDb";
    public static final String TABLE_TRIPS_NAME = "trips";
    public static final String ID = "id";
    public static final String FROM_PLACE = "from_place";
    public static final String TO_PLACE = "to_place";
    public static final String IS_ACTIVE = "is_active";
    public static final String TRIP_TIME = "trip_time";
    public static final String MIN_EARLIER = "min_earlier";
    public static final String REPEAT_ON = "repeat_on";
    public static final String EXECUTE_ON = "execute_on";
    public static final String PLACE_INPUT_STRING_FORMAT = "%s - %s: %6.4f, %6.4f";
}
