package com.example.gabri.thecalendar.Model;

/**
 * Created by Gabri on 18/04/19.
 */

public class Name {

    private String title;
    private String first;
    private String last;

    public Name(String title, String first, String last) {
        this.title = title;
        this.first = first;
        this.last = last;
    }


    public String getTitle() {
        return title;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }
}
