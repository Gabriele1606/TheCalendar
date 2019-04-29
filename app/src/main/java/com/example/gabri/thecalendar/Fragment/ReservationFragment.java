package com.example.gabri.thecalendar.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.AppParameter;
import com.example.gabri.thecalendar.MainPage;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Database.DBmanager;
import com.example.gabri.thecalendar.Model.Glide.GlideApp;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Gabri on 17/04/19.
 *
 * ReservationFragment is a class which handle the procedure to reserve a caregiver in a specific
 * Date and slot for a specific patient.
 * It visualize all the information on reservation_fragment.xml
 *
 *
 */




public class ReservationFragment extends android.support.v4.app.Fragment{

    View view;
    Bundle bundle;
    EditText patientName;
    Caregiver caregiver;
    List<Integer> roomsAvailable=new ArrayList<>();


    /**
     *  Method invoke when fragent is created.
     * Basically this method set all the graphical component and retrieve the information from DB
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.reservation_fragment, container, false);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        patientName=view.findViewById(R.id.patient_name);

        //Retrieve data from Bundle
        bundle=getArguments();
        findRoomsAvailable();
        fillLayoutInformation();
        caregiverCardCliccable();
        setTapOnRoomSelection();
        setTapReservationButton();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode==3333){
                int addID = data.getIntExtra("addressID", 0);
                this.caregiver= (Caregiver) data.getSerializableExtra("caregiver_selected");
                fillLayoutInformation();
                setTapOnRoomSelection();
                setTapReservationButton();
            }
        }
    }

    /**
     * Method invoked when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

    }

    /**
     * This method associate all the graphical components with the relative objects and set the text and
     * pictures.
     */

    public void fillLayoutInformation(){
        Calendar data=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");


        CircleImageView careImage=view.findViewById(R.id.careImage);
        ImageView redPlus= view.findViewById(R.id.select_care_button);
        TextView resumeDate=(TextView)view.findViewById(R.id.resumeDate);
        TextView roomNumber=(TextView)view.findViewById(R.id.room_number);
        //Get First room available. Caution with NullPointer
        roomNumber.setText(Integer.toString(this.roomsAvailable.get(0)));
        ImageView backImage=view.findViewById(R.id.reservation_back_image);
        ImageView emailIcon=view.findViewById(R.id.mail_icon);
        ImageView phoneIcon=view.findViewById(R.id.phone_icon);
        ImageView positionIcon=view.findViewById(R.id.position_icon);
        Button reserveButton=view.findViewById(R.id.reserve_button);
        GlideApp.with(view).load(R.drawable.reservation_background).into(backImage);
        GlideApp.with(view).load(R.drawable.empty_user).into(careImage);
        GlideApp.with(view).load(R.drawable.email_icon).into(emailIcon);
        GlideApp.with(view).load(R.drawable.phone_icon).into(phoneIcon);
        GlideApp.with(view).load(R.drawable.position_icon).into(positionIcon);
        GlideApp.with(view).load(R.drawable.red_plus_icon).into(redPlus);
        resumeDate.setText(data.get(Calendar.DAY_OF_MONTH)+" "+data.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH)+" "+ data.get(Calendar.YEAR)+" "+hour+":00");
     ;
        if(caregiver!=null){
            careImage= view.findViewById(R.id.careImage);
            TextView careName=view.findViewById(R.id.caregiverName_frag);
            TextView careMail=view.findViewById(R.id.care_mail);
            TextView carePhone=view.findViewById(R.id.care_phone);
            TextView careCity=view.findViewById(R.id.care_city);

            Glide.with(view).load(caregiver.getPicture().getMedium()).into(careImage);
            careName.setText(caregiver.getName().getFirst()+" "+caregiver.getName().getLast());
            careMail.setText(caregiver.getEmail());
            carePhone.setText(caregiver.getCell());
            careCity.setText(caregiver.getLocation().getCity());

        }


    }

    /**
     * This method set the caregiver card clickable in such a way to select the caregiver from
     * the caregiver fragment list.
     */
     public void caregiverCardCliccable(){
         final Calendar data=(Calendar) bundle.getSerializable("DATE");
         final int hour=(int)bundle.getInt("HOUR");
         final CardView caregiverInfo= (CardView)view.findViewById(R.id.caregiverInfo);

         caregiverInfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Bundle bundle = new Bundle();
                 bundle.putSerializable("DATE", data);
                 bundle.putInt("HOUR", hour);

                 startCaregiverListFragment(bundle);
             }
         });
     }


    /**
     * This mehtod start the Caregiver list fragment
     * @param bundle
     */
    private void startCaregiverListFragment(Bundle bundle){
         CaregiverListFragment caregiverListFragment= new CaregiverListFragment();
         FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.reservationLayout, caregiverListFragment, "Caregivers");
         caregiverListFragment.setArguments(bundle);
         caregiverListFragment.setTargetFragment(ReservationFragment.this, 3333);
         transaction.addToBackStack("Reservation");
         transaction.commit();

     }

     public void setTapOnRoomSelection(){
         ImageView minusButton= view.findViewById(R.id.decrease);
         ImageView plusButton= view.findViewById(R.id.increase);
         final View v= this.view;
         //Limits of the index to iterate.
         final int minIndex=0;
         final int maxIndex=this.roomsAvailable.size()-1;
         final List<Integer> tmp=this.roomsAvailable;

         minusButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 TextView roomNumber=(TextView)v.findViewById(R.id.room_number);
                 int number=tmp.indexOf(Integer.parseInt(roomNumber.getText().toString()));
                 if(number>minIndex){
                     number--;
                     roomNumber.setText(String.valueOf(tmp.get(number)));
                 }else{
                     Toast.makeText(view.getContext(),"No more rooms available!",Toast.LENGTH_SHORT).show();
                 }
             }
         });


         plusButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 TextView roomNumber=(TextView)v.findViewById(R.id.room_number);
                 int number=tmp.indexOf(Integer.parseInt(roomNumber.getText().toString()));

                 if(number<maxIndex){
                     number++;
                     roomNumber.setText(String.valueOf(tmp.get(number)));
                 }else{
                     Toast.makeText(view.getContext(),"No more rooms available!",Toast.LENGTH_SHORT).show();
                 }

             }
         });
     }


    /**
     * This method set the OnClickListener on the RESERVE BUTTON
     */
    public void setTapReservationButton(){
         Button reserveButton=view.findViewById(R.id.reserve_button);
         final Caregiver care =this.caregiver;
         final Calendar date=(Calendar) bundle.getSerializable("DATE");
         final EditText patName=this.patientName;
         reserveButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(care==null){
                     Toast.makeText(view.getContext(), "Please select a Caregiver", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if(patName.getText().toString().matches("")){
                     Toast.makeText(view.getContext(), "Please insert Patient name", Toast.LENGTH_SHORT).show();
                     patName.setError("");
                     return;
                 }

                 addReservationToDB();
                 refreshCalendar(date);
                 Data.getData().getMainPageActivity().getSupportFragmentManager().popBackStack();
                 Toast.makeText(view.getContext(), "The reservation was successful!", Toast.LENGTH_LONG).show();

             }
         });

     }


    /**
     * If there are not empty fields, this method put the reservation into database.
     */

    public void addReservationToDB(){
         Calendar date= (Calendar)bundle.getSerializable("DATE");
         int slot= (int)bundle.getInt("HOUR");
         String careId=this.caregiver.getEmail();
         int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
         String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
         int year=date.get(Calendar.YEAR);

         String dateInString=dayOfMonth+"_"+month+"_"+year;

         Reservation reservation = new Reservation();
         reservation.setId(dateInString+slot+careId);
         reservation.setDate(dateInString);
         reservation.setWeekOfYear(date.get(Calendar.WEEK_OF_YEAR));
         reservation.setSlot(slot);
         reservation.setCaregiver(this.caregiver);
         reservation.setPatientName(this.patientName.getText().toString());
         TextView roomNumber=(TextView)view.findViewById(R.id.room_number);
         int number=Integer.parseInt(roomNumber.getText().toString());
         reservation.setRoomNumber(number);
         reservation.save();

     }

    /**
     * After the reservation is completed, this method refresh the calendar view.
     * @param date
     */

    public void refreshCalendar(Calendar date){
        List<String> hours= ((MainPage)getActivity()).generateHourList();
         ((MainPage)getActivity()).createSlotAdapterRecycler(hours,date);
     }


    /**
     * This method find all the rooms available in a specific slot of specified date.
     */
    public void findRoomsAvailable(){
         Calendar date=(Calendar) bundle.getSerializable("DATE");
         int hour=(int)bundle.getInt("HOUR");
         DBmanager dBmanager= new DBmanager();
         for(int i = 0; i< AppParameter.roomNumber; i++){
             this.roomsAvailable.add(i);
         }

         List<Reservation> reservations=dBmanager.getReservationInASlot(date,hour);

         List<Integer> roomAlreadyUsed= new ArrayList<>();

         for(int i=0;i<reservations.size();i++){
             roomAlreadyUsed.add(reservations.get(i).getRoomNumber());
         }

         this.roomsAvailable.removeAll(roomAlreadyUsed);

     }

}
