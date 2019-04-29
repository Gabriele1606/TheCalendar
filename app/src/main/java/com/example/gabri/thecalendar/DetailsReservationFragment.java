package com.example.gabri.thecalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Glide.GlideApp;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.Model.Reservation_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Gabri on 23/04/19.
 */

public class DetailsReservationFragment extends android.support.v4.app.Fragment{

    View view;
    Bundle bundle;
    EditText patientName;
    Caregiver caregiver;
    List<Integer> roomsAvailable=new ArrayList<>();
    Reservation reservation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.reservation_fragment, container, false);

        patientName=view.findViewById(R.id.patient_name);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);


        //Retrieve data from Bundle
        bundle=getArguments();
        prepareForReservationDetails();
        takeReservationFromDB();
        fillLayoutInformation();
        setTapDeleteButton();
        setTapModifyButton();

        //findRoomsAvailable();

        //caregiverCardCliccable();
        //setTapOnRoomSelection();
        //setTapReservationButton();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

    }



    public void prepareForReservationDetails(){
        Button reservation= view.findViewById(R.id.reserve_button);
        TableLayout tableButtons= view.findViewById(R.id.reservation_detail_table_buttons);
        ImageView minusButton= view.findViewById(R.id.decrease);
        ImageView plusButton= view.findViewById(R.id.increase);
        ImageView redPlus= view.findViewById(R.id.select_care_button);
        TextView patientNameText= view.findViewById(R.id.patient_name_text);
        EditText patientName=view.findViewById(R.id.patient_name);

        redPlus.setVisibility(View.INVISIBLE);
        reservation.setVisibility(View.GONE);
        minusButton.setVisibility(View.GONE);
        plusButton.setVisibility(View.GONE);
        tableButtons.setVisibility(View.VISIBLE);
        patientName.setVisibility(View.GONE);
        patientNameText.setVisibility(View.VISIBLE);
    }

    public void takeReservationFromDB(){
        Calendar date=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");
        int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
        String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        int year=date.get(Calendar.YEAR);
        String dateInString=dayOfMonth+"_"+month+"_"+year;

        this.caregiver=(Caregiver) bundle.getSerializable("CAREGIVER");

        List<Reservation> reservations = SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.caregiver_email.eq(this.caregiver.getEmail()), Reservation_Table.slot.eq(hour)).queryList();
        this.reservation=reservations.get(0);

    }

    public void fillLayoutInformation(){
        Calendar date=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");

        CircleImageView careImage=view.findViewById(R.id.careImage);
        TextView careName=view.findViewById(R.id.caregiverName_frag);
        TextView careMail=view.findViewById(R.id.care_mail);
        TextView carePhone=view.findViewById(R.id.care_phone);
        TextView careCity=view.findViewById(R.id.care_city);
        TextView resumeDate=(TextView)view.findViewById(R.id.resumeDate);
        TextView roomNumber=(TextView)view.findViewById(R.id.room_number);
        TextView patientNameText= view.findViewById(R.id.patient_name_text);
        ImageView backImage=view.findViewById(R.id.reservation_back_image);
        ImageView emailIcon=view.findViewById(R.id.mail_icon);
        ImageView phoneIcon=view.findViewById(R.id.phone_icon);
        ImageView positionIcon=view.findViewById(R.id.position_icon);
        Button reserveButton=view.findViewById(R.id.reserve_button);

        GlideApp.with(view).load(R.drawable.reservation_background).into(backImage);
        GlideApp.with(view).load(this.caregiver.getPicture().getMedium()).into(careImage);
        GlideApp.with(view).load(R.drawable.email_icon).into(emailIcon);
        GlideApp.with(view).load(R.drawable.phone_icon).into(phoneIcon);
        GlideApp.with(view).load(R.drawable.position_icon).into(positionIcon);

        patientNameText.setText(this.reservation.getPatientName());
        roomNumber.setText(Integer.toString(this.reservation.getRoomNumber()));
        resumeDate.setText(date.get(Calendar.DAY_OF_MONTH)+" "+date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH)+" "+ date.get(Calendar.YEAR)+" "+hour+":00");
        careName.setText(this.caregiver.getName().getFirst()+" "+this.caregiver.getName().getLast());
        careMail.setText(this.caregiver.getEmail());
        carePhone.setText(this.caregiver.getCell());
        careCity.setText(this.caregiver.getLocation().getCity());


    }

    public void setTapDeleteButton(){
        Button deleteButton= view.findViewById(R.id.delete_button);
        final Calendar date=(Calendar) bundle.getSerializable("DATE");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this reservation?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeReservationFromDB();
                        refreshCalendar(date);
                        dialogInterface.dismiss();
                        closeFragment();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        });

    }

    public void setTapModifyButton(){
        Button modifyButton= view.findViewById(R.id.modify_button);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReservationFromDB();
                prepareForNewReservation();
            }
        });
    }

    public void removeReservationFromDB(){
        SQLite.delete(Reservation.class).where(Reservation_Table.id.eq(this.reservation.getId())).query();
    }

    public void closeFragment(){
        Data.getData().getMainPageActivity().getSupportFragmentManager().popBackStack();

    }

    public void prepareForNewReservation(){
        Button reservation= view.findViewById(R.id.reserve_button);
        TableLayout tableButtons= view.findViewById(R.id.reservation_detail_table_buttons);
        ImageView minusButton= view.findViewById(R.id.decrease);
        ImageView plusButton= view.findViewById(R.id.increase);
        ImageView redPlus= view.findViewById(R.id.select_care_button);
        TextView patientNameText= view.findViewById(R.id.patient_name_text);
        EditText patientName=view.findViewById(R.id.patient_name);

        GlideApp.with(view).load(R.drawable.red_plus_icon).into(redPlus);
        redPlus.setVisibility(View.VISIBLE);
        reservation.setVisibility(View.VISIBLE);
        minusButton.setVisibility(View.VISIBLE);
        plusButton.setVisibility(View.VISIBLE);
        tableButtons.setVisibility(View.GONE);
        patientName.setVisibility(View.VISIBLE);
        patientNameText.setVisibility(View.GONE);
        patientName.setText(this.reservation.getPatientName());

        findRoomsAvailable();
        setTapOnRoomSelection();
        caregiverCardCliccable();
        setTapReservationButton();



    }

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

                CaregiverListFragment caregiverListFragment= new CaregiverListFragment();
                FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.reservationLayout, caregiverListFragment, "Caregivers");
                caregiverListFragment.setArguments(bundle);
                caregiverListFragment.setTargetFragment(DetailsReservationFragment.this, 3333);
                transaction.addToBackStack("Reservation");
                transaction.commit();
            }
        });
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

    public void refreshCalendar(Calendar date){
        List<String> hours= ((MainPage)getActivity()).generateHourList();
        ((MainPage)getActivity()).createSlotAdapterRecycler(hours,date);
    }


    public void findRoomsAvailable(){
        Calendar date=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");
        int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
        String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        int year=date.get(Calendar.YEAR);

        String dateInString=dayOfMonth+"_"+month+"_"+year;
        for(int i = 0; i< AppParameter.roomNumber; i++){
            this.roomsAvailable.add(i);
        }

        List<Reservation> reservations=SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.slot.eq(hour)).queryList();

        List<Integer> roomAlreadyUsed= new ArrayList<>();

        for(int i=0;i<reservations.size();i++){
            roomAlreadyUsed.add(reservations.get(i).getRoomNumber());
        }

        this.roomsAvailable.removeAll(roomAlreadyUsed);

    }

}
