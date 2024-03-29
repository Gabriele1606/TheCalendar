package com.example.gabri.thecalendar.Model.Database;

import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.CaregiverDB;
import com.example.gabri.thecalendar.Model.CaregiverDB_Table;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.Model.Reservation_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gabri on 29/04/19.
 *
 * This Class is dedicated to handle the input/output operations from database.
 *
 */

public class DBmanager {




    public CaregiverDB getCaregiverFromDB(String careKey){
        CaregiverDB caregiver;
        caregiver= SQLite.select().from(CaregiverDB.class).where(CaregiverDB_Table.email.eq(careKey)).querySingle();
        return caregiver;
    }
    public List<Reservation> getReservationInASlot(Calendar date, int slot){
        List<Reservation> reservations;

        int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
        String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        int year=date.get(Calendar.YEAR);

        String dateInString=dayOfMonth+"_"+month+"_"+year;
        reservations=SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.slot.eq(slot)).queryList();

        return reservations;
    }

    public List<Reservation> getReservationInAWeekOfYear(Calendar date){
        List<Reservation> reservations;
        reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.weekOfYear.eq(date.get(Calendar.WEEK_OF_YEAR))).queryList();
        return reservations;

    }

    public List<Reservation> getReservationOfCaregiverInDate(Calendar date, int slot, Caregiver caregiver){
        List<Reservation> reservations;
        int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
        String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        int year=date.get(Calendar.YEAR);

        String dateInString=dayOfMonth+"_"+month+"_"+year;
        reservations = SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.caregiver_email.eq(caregiver.getEmail()), Reservation_Table.slot.eq(slot)).queryList();

        return reservations;
    }

    public List<Reservation> getReservationInADate(Calendar date){
        List<Reservation> reservations;

        int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
        String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        int year=date.get(Calendar.YEAR);

        String dateInString=dayOfMonth+"_"+month+"_"+year;
        reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString)).queryList();

        return reservations;
    }

    public ArrayList<CaregiverDB> getAllCaregivers(){
        ArrayList<CaregiverDB> caregiverDBs= (ArrayList<CaregiverDB>) SQLite.select().from(CaregiverDB.class).queryList();
        return caregiverDBs;
    }
    public void removeReservationFromDb(Reservation reservation){
        SQLite.delete(Reservation.class).where(Reservation_Table.id.eq(reservation.getId())).query();

    }

}
