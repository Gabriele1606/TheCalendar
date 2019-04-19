package com.example.gabri.thecalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;

import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Gabri on 17/04/19.
 */




public class ReservationFragment extends android.support.v4.app.Fragment{

    public static final int MAX_ROOM=10;
    public static final int MIN_ROOM=1;

    View view;
    Bundle bundle;
    EditText patientName;
    Caregiver caregiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.reservation_fragment, container, false);

        patientName=view.findViewById(R.id.patient_name);


        //Retrieve data from Bundle
        bundle=getArguments();
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


    public void fillLayoutInformation(){
        Calendar data=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");


        CircleImageView careImage=view.findViewById(R.id.careImage);
        TextView resumeDate=(TextView)view.findViewById(R.id.resumeDate);
        TextView roomNumber=(TextView)view.findViewById(R.id.room_number);
        roomNumber.setText("1");
        ImageView backImage=view.findViewById(R.id.reservation_back_image);
        ImageView emailIcon=view.findViewById(R.id.mail_icon);
        ImageView phoneIcon=view.findViewById(R.id.phone_icon);
        ImageView positionIcon=view.findViewById(R.id.position_icon);
        Button reserveButton=view.findViewById(R.id.reserve_button);
        Glide.with(view).load(R.drawable.reservation_background).into(backImage);
        Glide.with(view).load(R.drawable.empty_user).into(careImage);
        Glide.with(view).load(R.drawable.email_icon).into(emailIcon);
        Glide.with(view).load(R.drawable.phone_icon).into(phoneIcon);
        Glide.with(view).load(R.drawable.position_icon).into(positionIcon);
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

     public void caregiverCardCliccable(){
         CardView caregiverInfo= (CardView)view.findViewById(R.id.caregiverInfo);
         caregiverInfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 CaregiverListFragment caregiverListFragment= new CaregiverListFragment();
                 FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.reservationLayout, caregiverListFragment, "Caregivers");
                 caregiverListFragment.setTargetFragment(ReservationFragment.this, 3333);
                 transaction.addToBackStack("Reservation");
                 transaction.commit();
             }
         });
     }

     public void setTapOnRoomSelection(){
         ImageView minusButton= view.findViewById(R.id.decrease);
         ImageView plusButton= view.findViewById(R.id.increase);
         final View v= this.view;

         minusButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 TextView roomNumber=(TextView)v.findViewById(R.id.room_number);
                 int number=Integer.parseInt(roomNumber.getText().toString());

                 if(number>MIN_ROOM){
                     number--;
                     roomNumber.setText(String.valueOf(number));
                 }else{
                     Toast.makeText(view.getContext(),"There are "+MAX_ROOM+" rooms!",Toast.LENGTH_SHORT).show();
                 }
             }
         });


         plusButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 TextView roomNumber=(TextView)v.findViewById(R.id.room_number);
                 int number=Integer.parseInt(roomNumber.getText().toString());

                 if(number<MAX_ROOM){
                     number++;
                     roomNumber.setText(String.valueOf(number));
                 }else{
                     Toast.makeText(view.getContext(),"There are "+MAX_ROOM+" rooms!",Toast.LENGTH_SHORT).show();
                 }

             }
         });
     }

     public void setTapReservationButton(){
         Button reserveButton=view.findViewById(R.id.reserve_button);
         final Caregiver care =this.caregiver;
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
             }
         });

     }

}
