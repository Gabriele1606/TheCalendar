package com.example.gabri.thecalendar.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Gabri on 18/04/19.
 */

public class Login {

    @SerializedName("uuid")
    @Expose
    private String id;

    public Login(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
