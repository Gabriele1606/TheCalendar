package com.example.gabri.thecalendar.Model;

/**
 * Created by Gabri on 18/04/19.
 */

public class Timezone {

    private String offset;
    private String description;

    public Timezone(String offset, String description) {
        this.offset = offset;
        this.description = description;
    }

    public String getOffset() {
        return offset;
    }

    public String getDescription() {
        return description;
    }
}
