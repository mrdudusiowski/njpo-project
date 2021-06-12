package com.cpma.app.model.request;

public class DeviceRequest {
    private String androidID;
    private String manufacturer;
    private String phoneModel;
    private String deviceVersion;
    private String brand;
    private String product;
    private String serial;
    private int version;
    private String versionRelease;
    private int width;
    private int height;

    public String getAndroidID() {
        return androidID;
    }
    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getPhoneModel() {
        return phoneModel;
    }
    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }
    public String getDeviceVersion() {
        return deviceVersion;
    }
    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public String getVersionRelease() {
        return versionRelease;
    }
    public void setVersionRelease(String versionRelease) {
        this.versionRelease = versionRelease;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }
}

