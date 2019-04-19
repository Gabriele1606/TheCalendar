package com.example.gabri.thecalendar.Model.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Calendar;

/**
 * Created by Gabri on 19/04/19.
 */
@Table(database = AppDatabase.class)
public class Prova extends BaseModel {


    @Column
    @PrimaryKey(autoincrement = true)
    int id;


    @Column
    int slot;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
