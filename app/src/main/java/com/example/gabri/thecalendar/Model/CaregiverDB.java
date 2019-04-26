package com.example.gabri.thecalendar.Model;

import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Gabri on 26/04/19.
 */
@Table(database = AppDatabase.class)
public class CaregiverDB extends BaseModel{

    @Column
    private String gender;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Name name;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Location location;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Timezone timezone;
    @Column
    @PrimaryKey
    private String email;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Login login;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Birth dob;
    @Column
    private String phone;
    @Column
    private String cell;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private Picture picture;


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Birth getDob() {
        return dob;
    }

    public void setDob(Birth dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
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
        else if (otherCare instanceof CaregiverDB && this.getEmail().equals(((CaregiverDB) otherCare).getEmail()))
            return true;
        else return false;
    }

}
