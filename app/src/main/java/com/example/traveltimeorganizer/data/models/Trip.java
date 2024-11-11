package com.example.traveltimeorganizer.data.models;

import androidx.annotation.Nullable;

public class Trip {
    private int id;

    @Nullable
    private String fromPlace;

    @Nullable
    private String toPlace;

    private double fromLatitude;

    private double fromLongitude;

    private double toLatitude;

    private double toLongitude;

    private boolean isActive;

    private double tripTime;

    private int minEarlier;

    @Nullable
    private Integer repeatOnDay;

    @Nullable
    private String repeatOnTime;

    @Nullable
    private String executeOn;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(@Nullable String fromPlace) {
        this.fromPlace = fromPlace;
    }

    @Nullable
    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(@Nullable String toPlace) {
        this.toPlace = toPlace;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getTripTime() {
        return tripTime;
    }

    public void setTripTime(double tripTime) {
        this.tripTime = tripTime;
    }

    public int getMinEarlier() {
        return minEarlier;
    }

    public void setMinEarlier(int minEarlier) {
        this.minEarlier = minEarlier;
    }

    @Nullable
    public Integer getRepeatOnDay() {
        return repeatOnDay;
    }

    public void setRepeatOnDay(@Nullable Integer repeatOnDay) {
        this.repeatOnDay = repeatOnDay;
    }

    @Nullable
    public String getExecuteOn() {
        return executeOn;
    }

    public void setExecuteOn(@Nullable String executeOn) {
        this.executeOn = executeOn;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    @Nullable
    public String getRepeatOnTime() {
        return repeatOnTime;
    }

    public void setRepeatOnTime(@Nullable String repeatOnTime) {
        this.repeatOnTime = repeatOnTime;
    }
}