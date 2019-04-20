package com.example.gabri.thecalendar.Model;

/**
 * Created by Gabri on 20/04/19.
 */

public final class AppParameter {

    /**
     * Constant to set the number of rooms
     */
    public static final int roomNumber=10;

    /**
     * Constant to identify the START working hour 24H format
     * By default the working time is 9-17, so the first slot reservable is at 09:00
     */
    public static final int startHour=9;

    /**
     * Constant to identify the STOP working hour 24H format.
     *
     * By default the working time is 9-17, so the last slot reservable is at 16:00
     */
    public static final int stopHour=16;

}
