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
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

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

    View view;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.reservation_fragment, container, false);

        //Retrieve data from Bundle
        bundle=getArguments();
        fillLayoutInformation(bundle, view,null);
        caregiverCardCliccable(view);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode==3333){
                int addID = data.getIntExtra("addressID", 0);
                Caregiver caregiver= (Caregiver) data.getSerializableExtra("caregiver_selected");
                fillLayoutInformation(bundle,view,caregiver);
            }
        }
    }


    public void fillLayoutInformation(Bundle bundle, View view, @Nullable Caregiver caregiver){
        Calendar data=(Calendar) bundle.getSerializable("DATE");
        int hour=(int)bundle.getInt("HOUR");


        CircleImageView careImage=view.findViewById(R.id.careImage);
        TextView resumeDate=(TextView)view.findViewById(R.id.resumeDate);
        NumberPicker rooms=view.findViewById(R.id.number_picker);
        ImageView backImage=view.findViewById(R.id.reservation_back_image);
        ImageView emailIcon=view.findViewById(R.id.mail_icon);
        ImageView phoneIcon=view.findViewById(R.id.phone_icon);
        ImageView positionIcon=view.findViewById(R.id.position_icon);
        Glide.with(view).load(R.drawable.reservation_background).into(backImage);
        Glide.with(view).load(R.drawable.empty_user).into(careImage);
        Glide.with(view).load(R.drawable.email_icon).into(emailIcon);
        Glide.with(view).load(R.drawable.phone_icon).into(phoneIcon);
        Glide.with(view).load(R.drawable.position_icon).into(positionIcon);

        resumeDate.setText(data.get(Calendar.DAY_OF_MONTH)+" "+data.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH)+" "+ data.get(Calendar.YEAR)+" "+hour+":00");
        rooms.setMinValue(1);
        rooms.setMaxValue(10);
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

     public void caregiverCardCliccable(View view){
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

}
