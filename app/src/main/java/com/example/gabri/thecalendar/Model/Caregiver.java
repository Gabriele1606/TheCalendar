package com.example.gabri.thecalendar.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Gabri on 17/04/19.
 */

public class Caregiver implements Serializable{


    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("name")
    @Expose
    private Name name;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("timezone")
    @Expose
    private Timezone timezone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("login")
    @Expose
    private Login login;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("cell")
    @Expose
    private String cell;

    @SerializedName("picture")
    @Expose
    private Picture picture;

    public Caregiver(String gender, Name name, Location location, Timezone timezone, String email, String phone, String cell, Picture picture, Login login) {
        this.gender = gender;
        this.name = name;
        this.location = location;
        this.timezone = timezone;
        this.email = email;
        this.phone = phone;
        this.cell = cell;
        this.picture = picture;
        this.login=login;
    }


    public String getGender() {
        return gender;
    }

    public Name getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCell() {
        return cell;
    }

    public Picture getPicture() {
        return picture;
    }

    public Login getLogin(){return login;}

}
