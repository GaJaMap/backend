package com.map.gaja.client.domain.exception;

public class LocationOutsideKoreaException extends RuntimeException {
    private double latitude;
    private double longitude;

    public LocationOutsideKoreaException(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
