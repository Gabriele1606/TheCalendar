package com.example.gabri.thecalendar.Model;

import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Gabri on 18/04/19.
 */
@Table(database = AppDatabase.class)
public class Location extends BaseModel{

    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    private String street;
    @Column
    private String city;
    @Column
    private String state;
    @Column
    private String postcode;

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
