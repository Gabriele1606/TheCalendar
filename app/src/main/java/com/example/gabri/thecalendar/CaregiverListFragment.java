package com.example.gabri.thecalendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gabri.thecalendar.API.API;
import com.example.gabri.thecalendar.Adapters.CaregiverAdapter;

import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;

import com.example.gabri.thecalendar.Model.APIResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null && activeNetworkInfo.isConnected())
            return true;
        return false;

    }


    public void getCaregivers(){

        /**
         *
         * onLineInterceptor is used to read data from cache even if there is internet connection.
         * In this case the Internet consumpion is reduced.
         *
         * Cache-Control header means that the values returned will be put into the Cache and they
         * are accessible for max-age=60 seconds.
         *
         **/
        Interceptor onlineInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                int maxAge = 60; // read from cache for 60 seconds even if there is internet connection
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            }
        };

        /**
         *
         * offlineInterceptor is used to read data from cache if there is no internet connection.
         * In this case it is possible to access to data also in Offline conditions.
         *
         * Cache-Control header means that the values returned will be put into the Cache and they
         * are accessible for max-stalee=60*60*24*30 (30 days).
         *
         **/
        Interceptor offlineInterceptor= new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!isNetworkAvailable()) {
                    int maxStale = 60 * 60 * 24 * 30; // Offline cache available for 30 days
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
                return chain.proceed(request);
            }
        };

        Cache cache = new Cache(getContext().getCacheDir(), AppParameter.cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(offlineInterceptor)
                .addNetworkInterceptor(onlineInterceptor)
                .cache(cache)
                .build();


        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        API api = retrofit.create(API.class);
        Call<APIResponse> call = api.getResult();
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                APIResponse result=response.body();
                if(result==null){
                    Toast.makeText(view.getContext(),"For the first time, you need Internet connection!", Toast.LENGTH_LONG).show();
                }else{
                    fillRecyclerView(result.getCaregivers());
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(view.getContext(),"Somenthing went wrong with Internet connection!",Toast.LENGTH_LONG).show();

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
