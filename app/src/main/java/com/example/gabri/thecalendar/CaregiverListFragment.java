package com.example.gabri.thecalendar;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gabri.thecalendar.API.API;
import com.example.gabri.thecalendar.Adapters.CaregiverAdapter;

import com.example.gabri.thecalendar.Model.Caregiver;

import com.example.gabri.thecalendar.Model.APIResponse;


import java.util.ArrayList;
import java.util.List;



import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Gabri on 18/04/19.
 */

public class CaregiverListFragment extends android.support.v4.app.Fragment{

    List<Caregiver> caregivers;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getCaregivers();
        this.view= inflater.inflate(R.layout.caregiver_list_fragment, container, false);

    return view;

    }


    public void getCaregivers(){
        //Send GET Request using Retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        API api = retrofit.create(API.class);
        Call<APIResponse> call = api.getResult();
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                APIResponse result=response.body();
                fillRecyclerView(result.getCaregivers());
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                System.out.println("Not work");

            }
        });
    }

    public void fillRecyclerView(ArrayList<Caregiver> caregivers){
        RecyclerView recyclerView = view.findViewById(R.id.caregiverListRecycler);
        CaregiverAdapter caregiverAdapter= new CaregiverAdapter(view.getContext(),caregivers);
        recyclerView.setAdapter(caregiverAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
    }

}
