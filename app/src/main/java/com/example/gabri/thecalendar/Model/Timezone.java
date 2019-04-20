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
public class Timezone extends BaseModel{

    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    private String offset;
    @Column
    private String description;


    public int getId() {
        return id;
    }

    public String getOffset() {
        return offset;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
