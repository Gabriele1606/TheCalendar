package com.example.gabri.thecalendar.Model.Database;

import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Gabri on 26/04/19.
 */
@Table(database = AppDatabase.class)
public class NameDB extends BaseModel{

    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    private String title;
    @Column
    private String first;
    @Column
    private String last;

    public int getId() {
        return id;
    }

    public String getTitle() {
        String careTitle =this.title;

        String shortName=Character.toUpperCase(careTitle.charAt(0))+careTitle.substring(1);

        return shortName;
    }

    public String getFirst() {
        String careFirst =this.first;

        String shortName=Character.toUpperCase(careFirst.charAt(0))+careFirst.substring(1);

        return shortName;
    }

    public String getLast() {
        String careLast =this.last;

        String shortName=Character.toUpperCase(careLast.charAt(0))+careLast.substring(1);

        return shortName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
