package com.example.gabri.thecalendar.Model;

import android.support.v7.widget.RecyclerView;

import com.example.gabri.thecalendar.Controller.AutoFill;
import com.example.gabri.thecalendar.MainPage;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Created by Gabri on 27/04/19.
 */
@RunWith(RobolectricTestRunner.class)
public class AutoFillTest {
    RecyclerView recyclerView;

    @Before
    public void setUp() throws Exception {
        MainPage mainPageActivity = Robolectric.setupActivity(MainPage.class);

        /**recyclerView = mainPageActivity.findViewById(R.id.slotHourRecycler);
        this.slotAdapter= new SlotAdapter(mContex,hours, date);
        recyclerView.setAdapter(slotAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContex));
         **/

    }

    @After
    public void tearDown() throws Exception {
        FlowManager.close();

    }
/**
    @Test
    public void start() throws Exception {
    }
**/
    /**
     * Creation of three Caregivens, two of them that have working hours available and the third not
     * have working hours available.
     * The Method will return the two caregivers with available working hours in this week.
     * @throws Exception
     */
    @Test
    public void getAvailableCaregiversInWeek() throws Exception {
        Caregiver caregiverWithWorkingHours_1= new Caregiver();
        Caregiver caregiverWithWorkingHours_2= new Caregiver();
        Caregiver caregiverWithNotWorkingHours_3= new Caregiver();
        ArrayList<Caregiver> caregivers= new ArrayList<>();
        String gennder="male";
        Name name= new Name();
        name.setTitle("Mr");
        name.setFirst("Gabriele");
        name.setLast("Bressan");
        Location location = new Location();
        Timezone timezone = new Timezone();
        String email= "gabriele@example.com";
        Login login= new Login();
        Birth birth = new Birth();
        String phone= "00000";
        String cell= "0000";
        Picture picture= new Picture();

        caregiverWithWorkingHours_1.setGender(gennder);
        caregiverWithWorkingHours_1.setName(name);
        caregiverWithWorkingHours_1.setLocation(location);
        caregiverWithWorkingHours_1.setTimezone(timezone);
        caregiverWithWorkingHours_1.setEmail(email);
        caregiverWithWorkingHours_1.setLogin(login);
        caregiverWithWorkingHours_1.setDob(birth);
        caregiverWithWorkingHours_1.setPhone(phone);
        caregiverWithWorkingHours_1.setCell(cell);
        caregiverWithWorkingHours_1.setPicture(picture);

        caregivers.add(caregiverWithWorkingHours_1);

        String gennder_2="male";
        Name name_2= new Name();
        name_2.setTitle("Mr");
        name_2.setFirst("Mario");
        name_2.setLast("Rossi");
        Location location_2 = new Location();
        Timezone timezone_2 = new Timezone();
        String email_2= "mario@example.com";
        Login login_2= new Login();
        Birth birth_2 = new Birth();
        String phone_2= "1234";
        String cell_2= "1234";
        Picture picture_2= new Picture();

        caregiverWithWorkingHours_2.setGender(gennder_2);
        caregiverWithWorkingHours_2.setName(name_2);
        caregiverWithWorkingHours_2.setLocation(location_2);
        caregiverWithWorkingHours_2.setTimezone(timezone_2);
        caregiverWithWorkingHours_2.setEmail(email_2);
        caregiverWithWorkingHours_2.setLogin(login_2);
        caregiverWithWorkingHours_2.setDob(birth_2);
        caregiverWithWorkingHours_2.setPhone(phone_2);
        caregiverWithWorkingHours_2.setCell(cell_2);
        caregiverWithWorkingHours_2.setPicture(picture_2);

        caregivers.add(caregiverWithWorkingHours_2);

        String gennder_3="male";
        Name name_3= new Name();
        name_3.setTitle("Mr");
        name_3.setFirst("Luca");
        name_3.setLast("Verdi");
        Location location_3 = new Location();
        Timezone timezone_3 = new Timezone();
        String email_3= "luca@example.com";
        Login login_3= new Login();
        Birth birth_3 = new Birth();
        String phone_3= "9876";
        String cell_3= "9876";
        Picture picture_3= new Picture();

        caregiverWithNotWorkingHours_3.setGender(gennder_3);
        caregiverWithNotWorkingHours_3.setName(name_3);
        caregiverWithNotWorkingHours_3.setLocation(location_3);
        caregiverWithNotWorkingHours_3.setTimezone(timezone_3);
        caregiverWithNotWorkingHours_3.setEmail(email_3);
        caregiverWithNotWorkingHours_3.setLogin(login_3);
        caregiverWithNotWorkingHours_3.setDob(birth_3);
        caregiverWithNotWorkingHours_3.setPhone(phone_3);
        caregiverWithNotWorkingHours_3.setCell(cell_3);
        caregiverWithNotWorkingHours_3.setPicture(picture_3);

        caregivers.add(caregiverWithNotWorkingHours_3);

        HashMap<String, Integer> careHourWeekMap;
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;
        AutoFill autoFill= new AutoFill(date, null);

        int slot=9;
        int roomNumber=1;
        Reservation reservation;
        for(int i=0;i<4;i++) {
            slot=slot+i;
            reservation = new Reservation();
            reservation.setId(dateInString + slot + caregiverWithWorkingHours_1.getEmail());
            reservation.setDate(dateInString);
            reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
            reservation.setSlot(slot);
            reservation.setCaregiver(caregiverWithWorkingHours_1);
            reservation.setPatientName("Patient");
            reservation.setRoomNumber(roomNumber);
            reservation.save();
        }

        slot=9;
        for(int i=0;i<4;i++) {
            slot=slot+i;
            reservation = new Reservation();
            reservation.setId(dateInString + slot + caregiverWithWorkingHours_2.getEmail());
            reservation.setDate(dateInString);
            reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
            reservation.setSlot(slot);
            reservation.setCaregiver(caregiverWithWorkingHours_2);
            reservation.setPatientName("Patient");
            reservation.setRoomNumber(roomNumber);
            reservation.save();
        }
        slot=9;
        for(int i=0;i<7;i++) {
            slot=slot+i;
            reservation = new Reservation();
            reservation.setId(dateInString + slot + caregiverWithNotWorkingHours_3.getEmail());
            reservation.setDate(dateInString);
            reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
            reservation.setSlot(slot);
            reservation.setCaregiver(caregiverWithNotWorkingHours_3);
            reservation.setPatientName("Patient");
            reservation.setRoomNumber(roomNumber);
            reservation.save();
        }

        careHourWeekMap=autoFill.getAvailableCaregiversInWeek(caregivers);

        int expectedNumberOfCareAvailable=2;
        int actualNumberOfCareAvailable=careHourWeekMap.size();

        assertEquals(expectedNumberOfCareAvailable,actualNumberOfCareAvailable);
        assertTrue(careHourWeekMap.containsKey(caregiverWithWorkingHours_1.getEmail()));
        assertTrue(careHourWeekMap.containsKey(caregiverWithWorkingHours_2.getEmail()));
    }

    /**
     * This method return the caregivers that work in a day and theirs room is the closest
     * to the room to be assigned.
     * Suppose there are two caregivers, the first works in room 2, the second in room 9 and the
     * auto-fill needs to assign the room number 1.
     *This method will return the first caregiver (That works in room 2).
     * @throws Exception
     */
    @Test
    public void getCaregiversWorkInDayNearestRoom() throws Exception {
        Caregiver careRoomTwo= new Caregiver();
        Caregiver CareRoomNine= new Caregiver();
        String gennder="male";
        Name name= new Name();
        name.setTitle("Mr");
        name.setFirst("Gabriele");
        name.setLast("Bressan");
        Location location = new Location();
        Timezone timezone = new Timezone();
        String email= "gabriele@example.com";
        Login login= new Login();
        Birth birth = new Birth();
        String phone= "00000";
        String cell= "0000";
        Picture picture= new Picture();

        careRoomTwo.setGender(gennder);
        careRoomTwo.setName(name);
        careRoomTwo.setLocation(location);
        careRoomTwo.setTimezone(timezone);
        careRoomTwo.setEmail(email);
        careRoomTwo.setLogin(login);
        careRoomTwo.setDob(birth);
        careRoomTwo.setPhone(phone);
        careRoomTwo.setCell(cell);
        careRoomTwo.setPicture(picture);

        String gennder_2="male";
        Name name_2= new Name();
        name_2.setTitle("Mr");
        name_2.setFirst("Mario");
        name_2.setLast("Rossi");
        Location location_2 = new Location();
        Timezone timezone_2 = new Timezone();
        String email_2= "mario@example.com";
        Login login_2= new Login();
        Birth birth_2 = new Birth();
        String phone_2= "1234";
        String cell_2= "1234";
        Picture picture_2= new Picture();

        CareRoomNine.setGender(gennder_2);
        CareRoomNine.setName(name_2);
        CareRoomNine.setLocation(location_2);
        CareRoomNine.setTimezone(timezone_2);
        CareRoomNine.setEmail(email_2);
        CareRoomNine.setLogin(login_2);
        CareRoomNine.setDob(birth_2);
        CareRoomNine.setPhone(phone_2);
        CareRoomNine.setCell(cell_2);
        CareRoomNine.setPicture(picture_2);

        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        int slot= 9;
        int roomNumber=2;

        Reservation reservation = new Reservation();
        reservation.setId(dateInString + slot + careRoomTwo.getEmail());
        reservation.setDate(dateInString);
        reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
        reservation.setSlot(slot);
        reservation.setCaregiver(careRoomTwo);
        reservation.setPatientName("Patient");
        reservation.setRoomNumber(roomNumber);
        reservation.save();

        slot= 12;
        roomNumber=9;

        reservation = new Reservation();
        reservation.setId(dateInString + slot + CareRoomNine.getEmail());
        reservation.setDate(dateInString);
        reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
        reservation.setSlot(slot);
        reservation.setCaregiver(CareRoomNine);
        reservation.setPatientName("Patient");
        reservation.setRoomNumber(roomNumber);
        reservation.save();

        HashMap<String, Integer> careNearestRoomMap;
        AutoFill autoFill= new AutoFill(date, null);

        int roomTarget=1;
        String careKeyExpected= careRoomTwo.getEmail();
        careNearestRoomMap= autoFill.getCaregiversWorkInDayNearestRoom(slot,roomTarget);
        assertTrue(careNearestRoomMap.containsKey(careKeyExpected));
    }

    /**
     * This mehtod will return a Set of caregivers that work less in the previous weeks (in particular
     * set of caregivers that work less in the previous for weeks).
     * Two caregiver are created. The first is the caregiver that worked more than the second.
     * this method will return the second caregiver.
     *
     * @throws Exception
     */
    @Test
    public void getCaregiversWorkLessPreviousWeeks() throws Exception {
        Caregiver careWorkMore= new Caregiver();
        Caregiver CareWorkLess= new Caregiver();
        String gennder="male";
        Name name= new Name();
        name.setTitle("Mr");
        name.setFirst("Gabriele");
        name.setLast("Bressan");
        Location location = new Location();
        Timezone timezone = new Timezone();
        String email= "gabriele@example.com";
        Login login= new Login();
        Birth birth = new Birth();
        String phone= "00000";
        String cell= "0000";
        Picture picture= new Picture();

        careWorkMore.setGender(gennder);
        careWorkMore.setName(name);
        careWorkMore.setLocation(location);
        careWorkMore.setTimezone(timezone);
        careWorkMore.setEmail(email);
        careWorkMore.setLogin(login);
        careWorkMore.setDob(birth);
        careWorkMore.setPhone(phone);
        careWorkMore.setCell(cell);
        careWorkMore.setPicture(picture);

        String gennder_2="male";
        Name name_2= new Name();
        name_2.setTitle("Mr");
        name_2.setFirst("Mario");
        name_2.setLast("Rossi");
        Location location_2 = new Location();
        Timezone timezone_2 = new Timezone();
        String email_2= "mario@example.com";
        Login login_2= new Login();
        Birth birth_2 = new Birth();
        String phone_2= "1234";
        String cell_2= "1234";
        Picture picture_2= new Picture();

        CareWorkLess.setGender(gennder_2);
        CareWorkLess.setName(name_2);
        CareWorkLess.setLocation(location_2);
        CareWorkLess.setTimezone(timezone_2);
        CareWorkLess.setEmail(email_2);
        CareWorkLess.setLogin(login_2);
        CareWorkLess.setDob(birth_2);
        CareWorkLess.setPhone(phone_2);
        CareWorkLess.setCell(cell_2);
        CareWorkLess.setPicture(picture_2);

        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        AutoFill autofill= new AutoFill(date, null);
        Calendar previousDate = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        previousDate.add(Calendar.DATE,-20);
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        int slot=9;
        int roomNumber=1;
        Reservation reservation;
        for(int i=0;i<3;i++) {
            slot=slot+i;
            reservation = new Reservation();
            reservation.setId(dateInString + slot + careWorkMore.getEmail());
            reservation.setDate(dateInString);
            reservation.setWeekOfYear(previousDate.get(Calendar.WEEK_OF_YEAR));
            reservation.setSlot(slot);
            reservation.setCaregiver(careWorkMore);
            reservation.setPatientName("Patient");
            reservation.setRoomNumber(roomNumber);
            reservation.save();
        }

        slot=9;
        for(int i=0;i<1;i++) {
            slot=slot+i;
            reservation = new Reservation();
            reservation.setId(dateInString + slot + CareWorkLess.getEmail());
            reservation.setDate(dateInString);
            reservation.setWeekOfYear(previousDate.get(Calendar.WEEK_OF_YEAR));
            reservation.setSlot(slot);
            reservation.setCaregiver(CareWorkLess);
            reservation.setPatientName("Patient");
            reservation.setRoomNumber(roomNumber);
            reservation.save();
        }

        HashMap<String, Integer> caresWorkLess= autofill.getCaregiversWorkLessPreviousWeeks();
        int numberOfResultsExpected=1;
        assertEquals(numberOfResultsExpected, caresWorkLess.size());
        assertTrue(caresWorkLess.containsKey(CareWorkLess.getEmail()));
    }

    @Test
    public void getEmptySlots() throws Exception {


        int totalSlot= (AppParameter.stopHour+1)-AppParameter.startHour;


        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        date.add(Calendar.DATE,1);
        AutoFill autofill= new AutoFill(date, null);
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;
        List<Reservation> reservations= SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString)).queryList();
        ArrayList<Integer> usedSlots=new ArrayList<>();
        for(int i=0;i<reservations.size();i++){
            if(!usedSlots.contains(reservations.get(i).getSlot()))
                usedSlots.add(reservations.get(i).getSlot());
        }

        int expected= totalSlot-usedSlots.size();


        int real=autofill.getEmptySlots(date).size();

        assertEquals(expected,real);
    }

    @Test
    public void getAvailableRooms() throws Exception {
        int availableRoomsExpected=AppParameter.roomNumber;
        int timeSlot=1;
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        date.add(Calendar.DATE,1);
        AutoFill autoFill= new AutoFill(date, null);
        int availableNumberActual=autoFill.getAvailableRooms(timeSlot).size();
        assertEquals(availableRoomsExpected,availableNumberActual);


    }

    @Test
    public void addReservationToDB() throws Exception {

        Caregiver caregiverExpected= new Caregiver();
        String gennder="male";
        Name name= new Name();
        name.setTitle("Mr");
        name.setFirst("Gabriele");
        name.setLast("Bressan");
        Location location = new Location();
        location.setCity("Milano");
        location.setPostcode("0000");
        location.setState("Italy");
        location.setStreet("via roma");
        Timezone timezone = new Timezone();
        String email= "gabriele@example.com";
        Login login= new Login();
        Birth birth = new Birth();
        String phone= "00000";
        String cell= "0000";
        Picture picture= new Picture();

        ArrayList<Caregiver> caregivers= new ArrayList<>();

        caregiverExpected.setGender(gennder);
        caregiverExpected.setName(name);
        caregiverExpected.setLocation(location);
        caregiverExpected.setEmail(email);
        caregiverExpected.setLogin(login);
        caregiverExpected.setDob(birth);
        caregiverExpected.setPhone(phone);
        caregiverExpected.setCell(cell);
        caregiverExpected.setPicture(picture);

        caregivers.add(caregiverExpected);

        int slotToReserve=8;
        int roomToReserve= 1;
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        date.add(Calendar.DATE,1);
        AutoFill autoFill= new AutoFill(date, null);

        autoFill.addReservationToDB(caregivers,slotToReserve,caregiverExpected.getEmail(),roomToReserve);

        List<Reservation> reservations= SQLite.select().from(Reservation.class).queryList();

        int reservationSizeExpected=1;
        int reservationSizeActual=reservations.size();

        assertEquals(reservationSizeExpected,reservationSizeActual);

        Caregiver caregiverActual=reservations.get(0).getCaregiver();

        assertEquals(caregiverExpected,caregiverActual);
    }


}