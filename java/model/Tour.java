package com.example.tourbud5.model;



import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Date;

public class Tour {
    Tour(){}
    public static String [] possibleTags={"Food","Shopping","Nature"};


    Tour(TourBuilder.tourBuilder builder){
        this.owner=builder.getOwner();
        this.uri= builder.getUri();
        this.date=builder.getDate();
        this.Meetlocation=new latlng(builder.getMeetlocation().latitude,builder.getMeetlocation().longitude);
        //fixed meetLocation to use latlng since LatLng does not have public constructor
        this.placeName=builder.getPlaceName();
        this.title=builder.getTitle();
        this.tags=builder.getTags();
        this.description=builder.getDescription();
    }



    private ArrayList<String> RSVP= new ArrayList<>();
    private String owner;
    private String uri;
    private Date date;
    private latlng Meetlocation;
    private String placeName;
    private String title;
    private ArrayList<String> tags= new ArrayList<>();
    private String description;


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ArrayList<String> getTags() { return tags; }
    public void setTags(ArrayList<String> tags) { this.tags = tags; }

    public ArrayList<String> getRSVP() {
        return RSVP;
    }
    public void setRSVP(ArrayList<String> RSVP) {
        this.RSVP = RSVP;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public  latlng getMeetlocation() {
        return Meetlocation;
    }
    public void setMeetlocation(latlng meetlocation) {
        Meetlocation = meetlocation;
    }

}

