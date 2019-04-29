package com.example.gabri.thecalendar.Model;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.example.gabri.thecalendar.Adapters.SlotAdapter;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Handler;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by Gabri on 26/04/19.
 */

public class AutoFill extends Thread{

List<Reservation> reservationsOfTheDay;
HashMap<String, Integer> caresWorkLess;

private Calendar date;
private SlotAdapter slotAdapter;
private ArrayList<Reservation> reservationDone;
private ArrayList<Caregiver> allCaregivers;
private ArrayList<Integer> emptySlots;
private HashMap<String, Integer> caregiverHoursAvailable;
private HashMap<String, Integer> caregiverAlreadyAssignedToRoom;

    public AutoFill(Calendar date, SlotAdapter slotAdapter){
        this.date=date;
        this.slotAdapter=slotAdapter;
        this.reservationDone= new ArrayList<>();
        this.allCaregivers=getAllCaregivers();
        this.emptySlots= getEmptySlots(date);
        caregiverHoursAvailable = getAvailableCaregiversInWeek(new ArrayList<Caregiver>(allCaregivers));
        this.caregiverAlreadyAssignedToRoom= new HashMap<>();

    }


    public void run(){
        ArrayList<Integer> roomsAvilable;
        HashMap<String, Integer> caregiverHoursAvailableForRooms;
        HashMap<String, Integer> caregiverHoursAvailableTmp;
        HashMap<String, Integer> careNearestRoomMap ;
        HashMap<String, Integer> caregiverWorkLess ;


        for(int i=0;i<emptySlots.size() && !isInterrupted();i++) {
            roomsAvilable = getAvailableRooms(emptySlots.get(i));
            caregiverHoursAvailableForRooms = new HashMap<>(caregiverHoursAvailable);
            this.caregiverAlreadyAssignedToRoom= new HashMap<>();
            for (int j = 0; j < roomsAvilable.size() && !isInterrupted(); j++) {
                caregiverHoursAvailableTmp = new HashMap<>(caregiverHoursAvailableForRooms);
                careNearestRoomMap = getCaregiversWorkInDayNearestRoom(emptySlots.get(i), roomsAvilable.get(j));
                caregiverWorkLess = getCaregiversWorkLessPreviousWeeks();


                if (caregiverHoursAvailableForRooms.keySet().toArray().length > 0){
                    String careKey = (String) caregiverHoursAvailableForRooms.keySet().toArray()[0];


                    //If the CaregiversAvailable Set is not empty, Itersection with Caregivers that already works
                if (caregiverHoursAvailableTmp.keySet().size() > 1) {
                    caregiverHoursAvailableTmp.keySet().retainAll(careNearestRoomMap.keySet());

                    if (caregiverHoursAvailableTmp.keySet().size() > 1) {
                        careKey = (String) caregiverHoursAvailableTmp.keySet().toArray()[0];
                        caregiverHoursAvailableTmp.keySet().retainAll(caregiverWorkLess.keySet());

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
                    //Don't fill the slot
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
            //notifyAutofillCompleted();
        }

    }

    private void notifyAdapterChanged(){
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                slotAdapter.notifyDataSetChanged();
            }
        });
    }

    private void notifyAutofillCompleted(){
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),"Autofill completed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayList<Caregiver> getAllCaregivers(){
        ArrayList<CaregiverDB> caregiverDBs= (ArrayList<CaregiverDB>) SQLite.select().from(CaregiverDB.class).queryList();
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

    public HashMap<String, Integer> getAvailableCaregiversInWeek(ArrayList<Caregiver> allCaregivers){

        //Return all the reservations done in a specific weekOfYear, where the weekOfYear is obtained when user click on slotHour.
        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.weekOfYear.eq(date.get(Calendar.WEEK_OF_YEAR))).queryList();


        HashMap<String, Integer> careHourWeekMap = new HashMap<String, Integer>();
        for(int i=0;i<allCaregivers.size();i++){
            careHourWeekMap.put(allCaregivers.get(i).getEmail(),AppParameter.hourPerWeek);
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

    public ArrayList<Integer> getEmptySlots(Calendar date){
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString)).queryList();

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

    public ArrayList<Integer> getAvailableRooms(int slot){
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.slot.eq(slot)).queryList();

        ArrayList<Integer> rooms=new ArrayList<>();
        for(int i=0;i<AppParameter.roomNumber;i++){
            rooms.add(i);
        }

        ArrayList<Integer> roomsUsed=new ArrayList<>();
        for(int i=0;i<reservations.size();i++){
            roomsUsed.add(reservations.get(i).getRoomNumber());
        }

        rooms.removeAll(roomsUsed);
        //Integer[] roomsArray=rooms.toArray(new Integer[rooms.size()]);

        return rooms;
    }

    /**
     * This Method check if it is possible to fill an "Old" slot hour of today
     * @param fDate
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

    public void cancelReservationsDone(){

        for(int i=0; i<reservationDone.size();i++){
            SQLite.delete(Reservation.class).where(Reservation_Table.id.eq(reservationDone.get(i).getId())).query();
        }
        notifyAdapterChanged();

    }

}
