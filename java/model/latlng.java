package com.example.tourbud5.model;

public class latlng {



        latlng(){}
        private double lat;
        private double lng;
        latlng(double lat,double lng){
            this.lat=lat;
            this.lng=lng; }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

    } // no public constructor for LatLng, hence unable to deserialize

