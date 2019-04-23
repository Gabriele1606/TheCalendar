package com.example.gabri.thecalendar.Model;

import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import retrofit2.http.FieldMap;

/**
 * Created by Gabri on 17/04/19.
 */
@Table(database = AppDatabase.class)
public class Caregiver extends BaseModel implements Serializable{

    @SerializedName("gender")
    @Column
    private String gender;
    @SerializedName("name")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Name name;
    @SerializedName("location")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Location location;
    @SerializedName("timezone")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Timezone timezone;
    @SerializedName("email")
    @Column
    @PrimaryKey
    private String email;
    @SerializedName("login")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Login login;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("dob")
    private Birth dob;

    @SerializedName("phone")
    @Column
    private String phone;
    @SerializedName("cell")
    @Column
    private String cell;

    @SerializedName("picture")
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Picture picture;



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



    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Birth getDob() {
        return dob;
    }

    public void setDob(Birth dob) {
        this.dob = dob;
    }

    /**
     * Since the JSON response not give the of the Caregivers: two Caregivers objects are equal if the have the same email.
     * Email by definition is unique for each person in the world.
     * @param otherCare
     * @return
     */
    @Override
    public boolean equals(Object otherCare) {
        if(otherCare == null) return false;
        else if (otherCare instanceof Caregiver && this.getEmail().equals(((Caregiver) otherCare).getEmail()))
            return true;
        else return false;
    }

}
