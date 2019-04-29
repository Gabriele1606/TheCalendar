package com.example.gabri.thecalendar.Fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gabri.thecalendar.API.API;
import com.example.gabri.thecalendar.Adapters.CaregiverAdapter;

import com.example.gabri.thecalendar.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;

import com.example.gabri.thecalendar.Model.APIResponse;
import com.example.gabri.thecalendar.Model.Database.DBmanager;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
 *
 *
 * CaregiverListFragment is a class which handle the procedure to require caregiver using API and visualize them
 * in caregiver_list_fragment.xml
 */

public class CaregiverListFragment extends android.support.v4.app.Fragment{

    List<Caregiver> caregivers;
    View view;
    Bundle bundle;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view= inflater.inflate(R.layout.caregiver_list_fragment, container, false);
        getCaregivers();
        setSwipeAction();
        this.bundle=getArguments();

        progressBar = view.findViewById(R.id.care_loading_bar);
        progressBar.setVisibility(View.VISIBLE);

        return view;

    }

    /**
     * This method add the functionality to swipe to refresh the caregivers list
     */
    public void setSwipeAction(){
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getCaregivers();
            }
        });
    }

    /**
     * This method check if the device is connected to internet.
     * @return true if the device is connected, false otherwise.
     */
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
                    filterAvailableCaregiversInSlot(result.getCaregivers());
                    HashMap<String, Integer> careHourWeekMap = getAvailableCaregiversInWeek(result.getCaregivers());
                    fillRecyclerView(result.getCaregivers(), careHourWeekMap);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(view.getContext(),"Somenthing went wrong with Internet connection!",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void fillRecyclerView(List<Caregiver> caregivers, HashMap<String, Integer> careHourWeekMap){
        RecyclerView recyclerView = view.findViewById(R.id.caregiverListRecycler);
        CaregiverAdapter caregiverAdapter= new CaregiverAdapter(view.getContext(),caregivers, careHourWeekMap);
        recyclerView.setAdapter(caregiverAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
    }

    public void filterAvailableCaregiversInSlot(List<Caregiver> allCaregivers){
        Calendar date= (Calendar)bundle.getSerializable("DATE");
        int hour= (int)bundle.getInt("HOUR");
        DBmanager dbManager=new DBmanager();
        List<Reservation> reservations= dbManager.getReservationInASlot(date, hour);
        List<Caregiver> alreadyBusyCaregivers= new ArrayList<>();
        for(int i=0;i<reservations.size();i++){
            alreadyBusyCaregivers.add(reservations.get(i).getCaregiver());
        }
        //From all Caregivers, the busyCaregivers are removed in such a way to visualize only the available.
        allCaregivers.removeAll(alreadyBusyCaregivers);

    }

    /**
     * Return all the caregivers that still have working hours.
     * @param allCaregivers
     * @return HashMap which contains CaregiverKey and the hours available.
     */
    public HashMap<String, Integer> getAvailableCaregiversInWeek(List<Caregiver> allCaregivers){
        Calendar date= (Calendar)bundle.getSerializable("DATE");
        int hour= (int)bundle.getInt("HOUR");
        DBmanager dbManager=new DBmanager();
        //Return all the reservations done in a specific weekOfYear, where the weekOfYear is obtained when user click on slotHour.
        List<Reservation> reservations= dbManager.getReservationInAWeekOfYear(date);
        HashMap<String, Integer> careHourWeekMap = new HashMap<String, Integer>();
        Caregiver tmp;
        for (int i=0; i<reservations.size();i++){
            tmp=reservations.get(i).getCaregiver();
            if(!careHourWeekMap.containsKey(tmp.getEmail())){
                careHourWeekMap.put(tmp.getEmail(),1);
            }else{
                careHourWeekMap.put(tmp.getEmail(), careHourWeekMap.get(tmp.getEmail())+1);
            }
        }

        //From the MAP and from the List of Caregivers to visualize I remove caregivers that for a specified week has reached 5+1 hours per Week.
        int count=careHourWeekMap.size();
        for(int i=0; i< allCaregivers.size() && count>0;i++){
            String careKey=allCaregivers.get(i).getEmail();
            if(careHourWeekMap.containsKey(careKey)){
                if(careHourWeekMap.get(careKey)>=(AppParameter.hourPerWeek+AppParameter.extraHoursPerWeek)){
                    allCaregivers.remove(i);
                }
                count--;
            }
        }

        return careHourWeekMap;
    }


}
