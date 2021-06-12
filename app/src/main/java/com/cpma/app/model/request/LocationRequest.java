package com.cpma.app.model.request;


public class LocationRequest {
    private Double latitude;
    private Double longitude;
    private String androidID;

    public LocationRequest(Double latitude, Double longitude, String androidID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.androidID = androidID;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public String getAndroidID() {
        return androidID;
    }
    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }
}
