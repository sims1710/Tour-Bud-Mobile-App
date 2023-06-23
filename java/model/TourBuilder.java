package com.example.tourbud5.model;



import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Date;

public class TourBuilder {

    public static class tourBuilder{
        public tourBuilder(){};



        private ArrayList<String> RSVP= new ArrayList<>();
        private String owner;
        private String uri;
        private Date date;
        private LatLng Meetlocation;
        private String title;
        private ArrayList<String> tags= new ArrayList<String>();
        private String description;
        private String placeName;


        public ArrayList<String> getRSVP() {
            return RSVP;
        }

        public String getOwner() {
            return owner;
        }

        public String getUri() {
            return uri;
        }

        public Date getDate() {
            return date;
        }

        public  LatLng getMeetlocation() {
            return Meetlocation;
        }

        public  String getPlaceName() {
            return placeName;
        }


        public tourBuilder setPlaceName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public tourBuilder setRSVP(ArrayList<String> RSVP) {
            this.RSVP = RSVP;
            return this;
        }




        public tourBuilder setOwner(String owner) {
            this.owner = owner;
            return this;

        }




        public tourBuilder setUri(String uri) {
            this.uri = uri;
            return this;
        }




        public tourBuilder setDate(Date date) {
            this.date = date;
            return this;
        }




        public tourBuilder setMeetlocation(LatLng meetlocation) {
            Meetlocation = meetlocation;
            return this;
        }

        public String getTitle() { return title; }

        public tourBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() { return description; }
        public tourBuilder setDescription(String description) {
            this.description = description;
        return this;
        }

        public ArrayList<String> getTags() { return tags; }
        public tourBuilder setTags(ArrayList<String> tags) {
            this.tags = tags;
        return this;
        }




        public Tour build(){
            return new Tour(this); }


    }
}
