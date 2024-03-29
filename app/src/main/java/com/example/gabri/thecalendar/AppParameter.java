package com.example.gabri.thecalendar;

/**
 * Created by Gabri on 20/04/19.
 *
 * This Class is dedicated to contains all the parameters useful for future development,
 * or in the case in which the assignment constraints change during the application lifecycle.
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


    /**
     * Constant used to set the Cache Size of 10MB.
     * Pay attention to modify this value.
     */
    public static final int cacheSize= 10 * 1024 * 1024;


    /**
     * Constant used to indicate the Hours per Week.
     * By constraints is set to 5 Hours
     */

    public static final int hourPerWeek= 5;

    /**
     * Constant used to indicate the extra Hours per Week.
     * By constraints is set to 1 Hours
     */

    public static final int extraHoursPerWeek=1;


}
