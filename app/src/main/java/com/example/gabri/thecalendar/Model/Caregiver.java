package com.example.gabri.thecalendar.Model;

/**
 * Created by Gabri on 17/04/19.
 */

public class Caregiver {

    private String name;
    private String image;
    private String room;


    public Caregiver(String name, String image, String room) {
        this.name = name;
        this.image = image;
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getRoom() {
        return room;
    }
}
