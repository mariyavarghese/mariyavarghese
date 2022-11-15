package com.oges.ttsec.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventModel {

    @SerializedName("event_listing")
    @Expose
    private List<EventListing> eventListing = null;

    public EventModel() {
    }

    public EventModel(List<EventListing> eventListing) {
        super();
        this.eventListing = eventListing;
    }

    public List<EventListing> getEventListing() {
        return eventListing;
    }

    public void setEventListing(List<EventListing> eventListing) {
        this.eventListing = eventListing;
    }

    public class EventListing {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("evnt_name")
        @Expose
        private String evntName;

        public EventListing() {
        }

        public EventListing(Integer id, String evntName) {
            super();
            this.id = id;
            this.evntName = evntName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEvntName() {
            return evntName;
        }

        public void setEvntName(String evntName) {
            this.evntName = evntName;
        }

    }
}






