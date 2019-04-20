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
public class Picture extends BaseModel{


    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    private String large;
    @Column
    private String medium;
    @Column
    private String thumbnail;


    public String getLarge() {
        return large;
    }

    public String getMedium() {
        return medium;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
