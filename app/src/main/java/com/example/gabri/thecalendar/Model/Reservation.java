package com.example.gabri.thecalendar.Model;

import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


/**
 * Created by Gabri on 19/04/19.
 */
@Table(database = AppDatabase.class)
public class Reservation extends BaseModel {

    /**
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
*/

    /**
     * As the primarykey the concatenation of Date+Slot+CaregiverID is used
     * In this way is possible to avoid to insert into DB the same Caregiver in two different
     * rooms at the same time
     */
    @Column
    @PrimaryKey
    String id;

    @Column
    String date;

    @Column
    int weekOfYear;

    @Column
    int slot;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Caregiver caregiver;

    @Column
    String patientName;

    @Column
    int roomNumber;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public Caregiver getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(Caregiver caregiver) {
        this.caregiver = caregiver;
    }

    public String getPatientName() {

        String patient=Character.toUpperCase(this.patientName.charAt(0))+this.patientName.substring(1);
        return patient;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }
}
