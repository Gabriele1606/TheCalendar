package com.example.gabri.thecalendar.Model.Database;

import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Gabri on 26/04/19.
 */
@Table(database = AppDatabase.class)
public class LoginDB extends BaseModel{

    @SerializedName("uuid")
    @Column
    @PrimaryKey
    private String id;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
