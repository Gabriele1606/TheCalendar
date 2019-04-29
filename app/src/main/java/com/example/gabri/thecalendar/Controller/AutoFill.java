package com.example.gabri.thecalendar.Controller;

import android.os.Looper;

import com.example.gabri.thecalendar.Adapters.SlotAdapter;
import com.example.gabri.thecalendar.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.CaregiverDB;
import com.example.gabri.thecalendar.Model.Database.DBmanager;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.Model.Reservation_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gabri on 26/04/19.
 *
 * This Class handle with Auto Fill feature require by the assignment.
 * It Extends the Thread in such a way to not freeze the GUI when runs
 *
 */

public class AutoFill extends Thread{

List<Reservation> reservationsOfTheDay;
HashMap<String, Integer> caresWorkLess;

    /**
     * date selected on the calendar
     */
    private Calendar date;
    /**
     * slot adapter associated with calendar
     */
    private SlotAdapter slotAdapter;
    /**
     * Contains all the reservations computed by the autofill feature.
     */
    private ArrayList<Reservation> reservationDone;
    /**
     * All caregiver take from DB
     */
    private ArrayList<Caregiver> allCaregivers;
    private ArrayList<Integer> emptySlots;

    /**
     * HashMap of caregivers that have hour avaible in the considered date
     *
     */
    private HashMap<String, Integer> caregiverHoursAvailable;
    private HashMap<String, Integer> caregiverAlreadyAssignedToRoom;


    /**
     * Construtor
     * @param date date selected on the calendar
     * @param slotAdapter slot adapter associated with calendar
     */
    public AutoFill(Calendar date, SlotAdapter slotAdapter){
        this.date=date;
        this.slotAdapter=slotAdapter;
        this.reservationDone= new ArrayList<>();
        this.allCaregivers=getAllCaregivers();
        this.emptySlots= getEmptySlots(date);
        caregiverHoursAvailable = getAvailableCaregiversInWeek(new ArrayList<Caregiver>(allCaregivers));
        this.caregiverAlreadyAssignedToRoom= new HashMap<>();

    }


    /**
     * Core of the thread.
     * This method creates three different Sets:
     *      1)the first contains all the caregiver that still have hours of work available (overtime not considered) in the week considered
     *
     *      2)The second set contains the caregivers that already working on the same day, in the the nearest room. My solution to the “nearest room”
     *      open point was to take the caregiver that already working on the same day in the room closest to the one to be assigned.
     *
     *      3)The last set contains caregivers that have worked less hours in the past 4 weeks.
     *
     *
     *      Now, if multiple caregivers are eligible, it is possible to define the priority scale by making the intersection of the three sets
     *      in the following order: the first set with the second and if more than one caregiver are available, the result is intersected with the third.
     *      Could happen that in the final set result the solution is not unique, in that case the first caregiver in set is taken.
     *
     *      My interpretation of the assignment requirements is that in all empty slots, all the rooms needs to be filled.
     *      So this method iterate on all empty slots and all rooms available (at least O(n^2) but it is acceptable since the max number of slots is 8 and the max
     *      number of rooms is 10).
     */
    public void run(){
        ArrayList<Integer> roomsAvilable;
        HashMap<String, Integer> caregiverHoursAvailableForRooms;
        HashMap<String, Integer> caregiverHoursAvailableTmp;
        HashMap<String, Integer> careNearestRoomMap ;
        HashMap<String, Integer> caregiverWorkLess ;

        //Iteration over all empty slots
        for(int i=0;i<emptySlots.size() && !isInterrupted();i++) {
            roomsAvilable = getAvailableRooms(emptySlots.get(i));
            caregiverHoursAvailableForRooms = new HashMap<>(caregiverHoursAvailable);
            this.caregiverAlreadyAssignedToRoom= new HashMap<>();
            //Iteration over all rooms available
            for (int j = 0; j < roomsAvilable.size() && !isInterrupted(); j++) {
                caregiverHoursAvailableTmp = new HashMap<>(caregiverHoursAvailableForRooms);
                careNearestRoomMap = getCaregiversWorkInDayNearestRoom(emptySlots.get(i), roomsAvilable.get(j));
                caregiverWorkLess = getCaregiversWorkLessPreviousWeeks();


                if (caregiverHoursAvailableForRooms.keySet().toArray().length > 0){
                    String careKey = (String) caregiverHoursAvailableForRooms.keySet().toArray()[0];


                    //If the CaregiversAvailable Set is not empty, compute intersection with Caregivers that already works.
                if (caregiverHoursAvailableTmp.keySet().size() > 1) {
                    caregiverHoursAvailableTmp.keySet().retainAll(careNearestRoomMap.keySet());
                        //If the result of the previous intersection give more caregivers, compute the intersection with caregivers that have work less in the last 4 weeks
                    if (caregiverHoursAvailableTmp.keySet().size() > 1) {
                        careKey = (String) caregiverHoursAvailableTmp.keySet().toArray()[0];
                        caregiverHoursAvailableTmp.keySet().retainAll(caregiverWorkLess.keySet());
                            //If the result of the previous intersection give more caregivers, take the first.
                        if (caregiverHoursAvailableTmp.keySet().size() >= 1) {
                            careKey = (String) caregiverHoursAvailableTmp.keySet().toArray()[0];
                            addReservationToDB(allCaregivers, emptySlots.get(i), careKey, roomsAvilable.get(j));
                        } else {
                            addReservationToDB(allCaregivers, emptySlots.get(i), careKey, roomsAvilable.get(j));
                        }

                    } else if (caregiverHoursAvailableTmp.keySet().size() == 1) {
                        careKey = (String) caregiverHoursAvailableTmp.keySet().toArray()[0];
                        addReservationToDB(allCaregivers, emptySlots.get(i), careKey, roomsAvilable.get(j));

                    } else {
                        addReservationToDB(allCaregivers, emptySlots.get(i), careKey, roomsAvilable.get(j));
                    }
                } else if (caregiverHoursAvailableTmp.keySet().size() == 1) {
                    addReservationToDB(allCaregivers, emptySlots.get(i), careKey, roomsAvilable.get(j));
                } else {
                    //Leave the slot empty
                }
                caregiverHoursAvailableForRooms.remove(careKey);

                if (caregiverHoursAvailable.get(careKey) - 1 > 0)
                    caregiverHoursAvailable.put(careKey, caregiverHoursAvailable.get(careKey) - 1);
                else
                    caregiverHoursAvailable.remove(careKey);
            }
        }

        }
        if(isInterrupted()){
            cancelReservationsDone();
        }else {
            notifyAdapterChanged();
        }

    }

    /**
     * This method notify to the Slot Adapter that the dataset is changed.
     */
    private void notifyAdapterChanged(){
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                slotAdapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<Caregiver> getAllCaregivers(){
        DBmanager dbManager= new DBmanager();
        ArrayList<CaregiverDB> caregiverDBs= dbManager.getAllCaregivers();
        ArrayList<Caregiver> caregivers= new ArrayList<>();
        Caregiver caregiver;
        for(int i=0;i< caregiverDBs.size();i++){
            caregiver=new Caregiver();
            caregiver.setGender(caregiverDBs.get(i).getGender());
            caregiver.setName(caregiverDBs.get(i).getName());
            caregiver.setLocation(caregiverDBs.get(i).getLocation());
            caregiver.setTimezone(caregiverDBs.get(i).getTimezone());
            caregiver.setEmail(caregiverDBs.get(i).getEmail());
            caregiver.setCell(caregiverDBs.get(i).getPhone());
            caregiver.setPhone(caregiverDBs.get(i).getPhone());
            caregiver.setDob(caregiverDBs.get(i).getDob());
            caregiver.setLogin(caregiverDBs.get(i).getLogin());
            caregiver.setPicture(caregiverDBs.get(i).getPicture());
            caregivers.add(caregiver);
        }
    return caregivers;
    }


    /**
     * This method return an HashMap of all the caregiver that still have hours of work available (overtime not considered) in the week considered
     * @param allCaregivers
     * @return
     */
    public HashMap<String, Integer> getAvailableCaregiversInWeek(ArrayList<Caregiver> allCaregivers){

        //Return all the reservations done in a specific weekOfYear, where the weekOfYear is obtained when user click on slotHour.
        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.weekOfYear.eq(date.get(Calendar.WEEK_OF_YEAR))).queryList();


        HashMap<String, Integer> careHourWeekMap = new HashMap<String, Integer>();
        for(int i=0;i<allCaregivers.size();i++){
            careHourWeekMap.put(allCaregivers.get(i).getEmail(), AppParameter.hourPerWeek);
        }

        Caregiver tmp;
        for(int i=0;i<reservations.size();i++){
            tmp=reservations.get(i).getCaregiver();
            if(careHourWeekMap.containsKey(tmp.getEmail())){
                if(careHourWeekMap.get(tmp.getEmail())-1>0)
                    careHourWeekMap.put(tmp.getEmail(),careHourWeekMap.get(tmp.getEmail())-1);
                else
                    careHourWeekMap.remove(tmp.getEmail());
            }
        }

        return careHourWeekMap;
    }

    /**
     * This method return an Hashmap  with all caregivers that already working on the same day, in the the nearest room. My solution to the “nearest room”
     *      open point was to take the caregiver that already working on the same day in the room closest to the one to be assigned.
     *      My solution to the “nearest room” open point was to take the caregiver that already working on the same day in the room closest to the one to be assigned.
     *      For example, suppose you have two caregivers: Lucas working in the room number 2, Ivan working in the room number 9 and the room number 0 has to be assigned to one of the two.
     *      The room number 0 will be assigned to Lucas because he is nearest (2-0=2 smaller than 9-0=9).
     * @param hour
     * @param roomTarget
     * @return
     */

    public HashMap<String, Integer> getCaregiversWorkInDayNearestRoom(int hour, int roomTarget){

        if(reservationsOfTheDay==null) {
            int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
            String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
            int year = date.get(Calendar.YEAR);
            String dateInString = dayOfMonth + "_" + month + "_" + year;
            reservationsOfTheDay = SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString)).queryList();
        }
        Caregiver tmp;
        int room;
        int min = AppParameter.roomNumber;
        HashMap<String, Integer> careNearestRoomMap = new HashMap<>();
        for (int i = 0; i < reservationsOfTheDay.size(); i++) {
            tmp = reservationsOfTheDay.get(i).getCaregiver();
            room = reservationsOfTheDay.get(i).getRoomNumber();
            if (Math.abs(room - roomTarget) < min && !caregiverAlreadyAssignedToRoom.containsKey(tmp.getEmail())) {
                min = Math.abs(room - roomTarget);
                careNearestRoomMap.clear();
                careNearestRoomMap.put(tmp.getEmail(), room);
                caregiverAlreadyAssignedToRoom.put(tmp.getEmail(), room);
            } else if (Math.abs(room - roomTarget) == min && !caregiverAlreadyAssignedToRoom.containsKey(tmp.getEmail())) {
                careNearestRoomMap.put(tmp.getEmail(), room);
                caregiverAlreadyAssignedToRoom.put(tmp.getEmail(), room);
            }
        }

        return careNearestRoomMap;
    }

    /**
     * Return all caregivers that have worked less hours in the past 4 weeks.
     * @return
     */
    public HashMap<String, Integer> getCaregiversWorkLessPreviousWeeks(){

         if(caresWorkLess==null) {
             int weekOfTheYear = this.date.get(Calendar.WEEK_OF_YEAR);
             List<Reservation> reservations = new ArrayList<>();
             //Number given by the text rules.
             int numberOfPastWeek = 4;
             for (int i = 0; i < numberOfPastWeek; i++) {
                 reservations.addAll(SQLite.select().from(Reservation.class).where(Reservation_Table.weekOfYear.eq(weekOfTheYear - i)).queryList());
             }

             HashMap<String, Integer> careWorkWeeksBefore = new HashMap<String, Integer>();
             Caregiver tmp;
             for (int i = 0; i < reservations.size(); i++) {
                 tmp = reservations.get(i).getCaregiver();
                 if (!careWorkWeeksBefore.containsKey(tmp.getEmail()))
                     careWorkWeeksBefore.put(tmp.getEmail(), 1);
                 else
                     careWorkWeeksBefore.put(tmp.getEmail(), careWorkWeeksBefore.get(tmp.getEmail()) + 1);
             }
             caresWorkLess = new HashMap<String, Integer>();
             int min= Integer.MAX_VALUE;
             for(String careKey: careWorkWeeksBefore.keySet()){
                 if(careWorkWeeksBefore.get(careKey)<min){
                     min=careWorkWeeksBefore.get(careKey);
                     caresWorkLess.clear();
                     caresWorkLess.put(careKey,min);
                 }else if(careWorkWeeksBefore.get(careKey)==min){
                     caresWorkLess.put(careKey,min);
                 }
             }

         }
        return caresWorkLess;
    }


    /**
     * This method returns all empty slots bu removing from all slots, the used slots.
     * @param date
     * @return
     */
    public ArrayList<Integer> getEmptySlots(Calendar date){
        DBmanager dBmanager= new DBmanager();
        List<Reservation> reservations= dBmanager.getReservationInADate(date);
        ArrayList<Integer> slots=new ArrayList<>();
        for(int i=AppParameter.startHour;i<=AppParameter.stopHour;i++){
            //In this case, it is possible to avoid to fill the "old" slot of Today
            if(slotIsToFill(i))
                slots.add(i);
        }

        ArrayList<Integer> usedSlots=new ArrayList<>();
        for(int i=0;i<reservations.size();i++){
            if(!usedSlots.contains(reservations.get(i).getSlot()))
                usedSlots.add(reservations.get(i).getSlot());
        }

        slots.removeAll(usedSlots);

        return slots;
    }

    /**
     * This method return all the available rooms in a slot by removind the used room (room reserved) from all rooms.
     * @param slot
     * @return
     */
    public ArrayList<Integer> getAvailableRooms(int slot){

        DBmanager dBmanager= new DBmanager();
        List<Reservation> reservations= dBmanager.getReservationInASlot(this.date,slot);

        ArrayList<Integer> rooms=new ArrayList<>();
        for(int i=0;i<AppParameter.roomNumber;i++){
            rooms.add(i);
        }

        ArrayList<Integer> roomsUsed=new ArrayList<>();
        for(int i=0;i<reservations.size();i++){
            roomsUsed.add(reservations.get(i).getRoomNumber());
        }

        rooms.removeAll(roomsUsed);
        return rooms;
    }

    /**
     * This Method check if it is possible to fill an "Old" slot hour of today
     * @param hour
     * @return
     */
    public boolean slotIsToFill(int hour){
        Calendar dateOfToday = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        int dayOfMonthToday = dateOfToday.get(Calendar.DAY_OF_MONTH);
        String monthToday = dateOfToday.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int yearToday = dateOfToday.get(Calendar.YEAR);
        String dateInStringToday = dayOfMonthToday + "_" + monthToday + "_" + yearToday;

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        if(dateInStringToday.equals(dateInString)){
            if(hour>Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                return true;
            }else
                return false;
        }else
            return true;

    }


    /**
     * Add the reservation to DB
     * @param caregivers
     * @param slot
     * @param careId
     * @param roomNumber
     */

    public void addReservationToDB(ArrayList<Caregiver> caregivers, int slot, String careId, int roomNumber){
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        boolean notFound=true;
        Caregiver caregiver= new Caregiver();
        for(int i=0; i<caregivers.size() && notFound;i++){
            if(caregivers.get(i).getEmail().equals(careId)){
                caregiver=caregivers.get(i);
                notFound=false;
            }
        }
        Reservation reservation = new Reservation();
        reservation.setId(dateInString+slot+careId);
        reservation.setDate(dateInString);
        reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
        reservation.setSlot(slot);
        reservation.setCaregiver(caregiver);
        reservation.setPatientName("Patient");
        reservation.setRoomNumber(roomNumber);
        reservation.save();
        reservationDone.add(reservation);


    }


    /**
     * Cancel all the reservations done in case of problems.
     */
    public void cancelReservationsDone(){

        for(int i=0; i<reservationDone.size();i++){
            SQLite.delete(Reservation.class).where(Reservation_Table.id.eq(reservationDone.get(i).getId())).query();
        }
        notifyAdapterChanged();

    }

}
