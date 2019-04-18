package com.example.gabri.thecalendar;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gabri.thecalendar.Adapters.CaregiverAdapter;
import com.example.gabri.thecalendar.Adapters.PlaceholderAdapter;
import com.example.gabri.thecalendar.Model.Caregiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabri on 18/04/19.
 */

public class CaregiverListFragment extends android.support.v4.app.Fragment{

    List<Caregiver> caregivers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        caregivers=getCaregivers();

        View view= inflater.inflate(R.layout.caregiver_list_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.caregiverListRecycler);
        CaregiverAdapter caregiverAdapter= new CaregiverAdapter(view.getContext(),caregivers);
        recyclerView.setAdapter(caregiverAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));



    return view;

    }


    public List<Caregiver> getCaregivers(){
        List<Caregiver> caregivers= new ArrayList<>();

        Caregiver caregiver;

        for(int i=0; i<=10;i++){
            caregiver= new Caregiver("Gabriele", "@mipmap/ic_launcher_round", "#5");
            caregivers.add(caregiver);
        }
        return caregivers;
    }


}
