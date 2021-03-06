package com.example.maola.degradotourmap.Model;

import java.util.List;

/**
 * Created by Maola on 07/08/2017.
 */

public class Report {



    public String userId;
    public String title;
    public String description;
    public String typology;
    public String picture;

    public String markerID;
    public Double lat, lng;
    public List<String> time;
    //Data di segnalazione
    public String reportingDate;
    public List <String> comments;
    public int points;

    public Report(){}


    public Report(String userId, Double lat, Double lng, String title, String description, String typology, String picture, List<String> time, String reportingDate, List<String> comments, int points, String markerID) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.typology = typology;
        this.picture = picture;
        this.time = time;
        this.reportingDate = reportingDate;
        this.comments = comments;
        this.points = points;
        this.markerID = markerID;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypology() {
        return typology;
    }

    public void setTypology(String typology) {
        this.typology = typology;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getMarkerID() {
        return markerID;
    }

    public void setMarkerID(String markerID) {
        this.markerID = markerID;
    }
}
