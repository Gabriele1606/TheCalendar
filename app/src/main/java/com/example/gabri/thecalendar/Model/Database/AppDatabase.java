package com.example.gabri.thecalendar.Model.Database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Gabri on 19/04/19.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME ="THECALENDAR";
    public static final int VERSION = 1;

}
