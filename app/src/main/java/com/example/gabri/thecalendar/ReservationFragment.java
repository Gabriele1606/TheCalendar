package com.example.gabri.thecalendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Gabri on 17/04/19.
 */

public class ReservationFragment extends android.support.v4.app.Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("PARTITO");
        View view= inflater.inflate(R.layout.reservation_fragment, container, false);


        return view;
    }


}
