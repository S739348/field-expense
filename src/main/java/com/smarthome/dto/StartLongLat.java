package com.smarthome.dto;

public class StartLongLat {
    private double lat;
    private double lang;
    private String name;
    private String email;

    public StartLongLat(double lat, double lang, String name, String email) {
        this.lat = lat;
        this.lang = lang;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
