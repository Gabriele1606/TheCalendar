package com.example.gabri.thecalendar.Model;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Gabri on 26/04/19.
 */

public class AutoFill {


private Calendar date;

    public AutoFill(Calendar date){
        this.date=date;
    }


    public void start(){
        ArrayList<Caregiver> allCaregivers=getAllCaregivers();
        ArrayList<Integer> emptySlots= getEmptySlots();


        for(int i=0;i<emptySlots.size();i++) {

                HashMap<String, Integer> caregiverHoursAvailable = getAvailableCaregiversInWeek(new ArrayList<Caregiver>(allCaregivers));
                HashMap<String, Integer> careNearestRoomMap = getCaregiversWorkInDayNearestRoom(emptySlots.get(i));
                HashMap<String, Integer> caregiverWorkLess = getCaregiversWorkLessPreviousWeeks();


                String careKey = (String) caregiverHoursAvailable.keySet().toArray()[0];

                if (!caregiverHoursAvailable.keySet().isEmpty()) {
                    caregiverHoursAvailable.keySet().retainAll(careNearestRoomMap.keySet());
                    if (!caregiverHoursAvailable.keySet().isEmpty()) {
                        careKey = (String) caregiverHoursAvailable.keySet().toArray()[0];
                        caregiverHoursAvailable.keySet().retainAll(caregiverWorkLess.keySet());

                        if (caregiverHoursAvailable.keySet().size() >= 1) {
                            careKey = (String) caregiverHoursAvailable.keySet().toArray()[0];
                            addReservationToDB(allCaregivers, emptySlots.get(i), careKey, careNearestRoomMap.get(careKey));
                        } else {
                            addReservationToDB(allCaregivers, emptySlots.get(i), careKey, careNearestRoomMap.get(careKey));
                        }

                    } else if (caregiverHoursAvailable.keySet().size() == 1) {
                        careKey = (String) caregiverHoursAvailable.keySet().toArray()[0];
                        addReservationToDB(allCaregivers, emptySlots.get(i), careKey, careNearestRoomMap.get(careKey));

                    } else {
                        addReservationToDB(allCaregivers, emptySlots.get(i), careKey, 0);
                    }
                } else if (caregiverHoursAvailable.keySet().size() == 1) {
                    addReservationToDB(allCaregivers, emptySlots.get(i), careKey, 0);
                } else {
                    //Don't fill the slot
                }
        }
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

    private HashMap<String, Integer> getAvailableCaregiversInWeek(ArrayList<Caregiver> allCaregivers){

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

    private HashMap<String, Integer> getCaregiversWorkInDayNearestRoom(int hour){

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString)).queryList();
        HashMap<String, Integer> careWorkInDay = new HashMap<String, Integer>();




        Caregiver tmp;
        int room;
        for(int i=0;i<reservations.size();i++){
            tmp=reservations.get(i).getCaregiver();
            room=reservations.get(i).getRoomNumber();
            careWorkInDay.put(tmp.getEmail(),room);
        }

        Integer[] availableRooms=getAvailableRooms(hour);
        HashMap<String, Integer> careNearestRoomMap= new HashMap<>();
        int min=AppParameter.roomNumber;
        int tempValue=0;
        for(String careKey: careWorkInDay.keySet()){
            tempValue=findClosest(availableRooms, careWorkInDay.get(careKey));
            if(Math.abs(tempValue-careWorkInDay.get(careKey))<=min){
                min=Math.abs(tempValue-careWorkInDay.get(careKey));
                System.out.print(careKey+" "+tempValue);
                careNearestRoomMap.put(careKey,tempValue);
            }
        }

        Iterator<Map.Entry<String,Integer>> iter = careNearestRoomMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,Integer> entry = iter.next();
            if(Math.abs(min-entry.getValue())>min){
                careNearestRoomMap.remove(iter);
            }
        }

        return careNearestRoomMap;
    }


    private HashMap<String, Integer> getCaregiversWorkLessPreviousWeeks(){

        int weekOfTheYear= this.date.get(Calendar.WEEK_OF_YEAR);
        //Number given by the text rules.
        int numberOfPastWeek=4;

        List<Reservation> reservations= new ArrayList<>();
        for(int i=0;i<numberOfPastWeek;i++) {
            reservations.addAll(SQLite.select().from(Reservation.class).where(Reservation_Table.weekOfYear.eq(weekOfTheYear-i)).queryList());
        }

        HashMap<String, Integer> careWorkLess = new HashMap<String, Integer>();
        Caregiver tmp;
        for(int i=0;i<reservations.size();i++){
            tmp=reservations.get(i).getCaregiver();
            if(!careWorkLess.containsKey(tmp.getEmail()))
                careWorkLess.put(tmp.getEmail(), 1);
            else
                careWorkLess.put(tmp.getEmail(), careWorkLess.get(tmp.getEmail())+1);
        }
        return careWorkLess;
    }

    private ArrayList<Integer> getEmptySlots(){
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString)).queryList();

        ArrayList<Integer> slots=new ArrayList<>();
        for(int i=AppParameter.startHour;i<=AppParameter.stopHour;i++){
            slots.add(i);
        }

        ArrayList<Integer> usedSlots=new ArrayList<>();
        for(int i=0;i<reservations.size();i++){
            usedSlots.add(reservations.get(i).getSlot());
        }

        slots.removeAll(usedSlots);

        return slots;
    }

    private Integer[] getAvailableRooms(int slot){
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
        Integer[] roomsArray=rooms.toArray(new Integer[rooms.size()]);

        return roomsArray;
    }

    private int findClosest(Integer arr[], int target)
    {
        int n = arr.length;

        // Corner cases
        if (target <= arr[0])
            return arr[0];
        if (target >= arr[n - 1])
            return arr[n - 1];

        // Doing binary search
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;

            if (arr[mid] == target)
                return arr[mid];

            /* If target is less than array element,
               then search in left */
            if (target < arr[mid]) {

                // If target is greater than previous
                // to mid, return closest of two
                if (mid > 0 && target > arr[mid - 1])
                    return getClosest(arr[mid - 1],
                            arr[mid], target);

                /* Repeat for left half */
                j = mid;
            }

            // If target is greater than mid
            else {
                if (mid < n-1 && target < arr[mid + 1])
                    return getClosest(arr[mid],
                            arr[mid + 1], target);
                i = mid + 1; // update i
            }
        }

        // Only single element left after search
        return arr[mid];
    }

    // Method to compare which one is the more close
    // We find the closest by taking the difference
    //  between the target and both values. It assumes
    // that val2 is greater than val1 and target lies
    // between these two.
    private int getClosest(int val1, int val2,
                           int target)
    {
        if (target - val1 >= val2 - target)
            return val2;
        else
            return val1;
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

    }

}
