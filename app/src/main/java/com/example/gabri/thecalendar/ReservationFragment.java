package com.example.gabri.thecalendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gabri.thecalendar.Model.Data;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Gabri on 17/04/19.
 */

public class ReservationFragment extends android.support.v4.app.Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.reservation_fragment, container, false);

        //Retrieve data from Bundle
        Bundle bundle=getArguments();
        fillLayoutInformation(bundle, view);
        caregiverCardCliccable(view);


        return view;
    }


    public void fillLayoutInformation(Bundle bundle, View view){
        Calendar data=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");

        //ToDo also with Caregiver Photo, Name, Patient Name, RoomNumber;

        TextView resumeDate=(TextView)view.findViewById(R.id.resumeDate);

        resumeDate.setText(data.get(Calendar.DAY_OF_MONTH)+" "+data.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH)+" "+ data.get(Calendar.YEAR)+" "+hour+":00");



    }

     public void caregiverCardCliccable(View view){
         CardView caregiverInfo= (CardView)view.findViewById(R.id.caregiverInfo);
         caregiverInfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 CaregiverListFragment caregiverListFragment= new CaregiverListFragment();
                 FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.reservationLayout, caregiverListFragment, "Caregivers");
                 transaction.addToBackStack(null);
                 transaction.commit();
             }
         });
     }

}
