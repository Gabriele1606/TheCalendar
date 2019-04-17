package com.example.gabri.thecalendar.Model;

import com.example.gabri.thecalendar.MainPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gabri on 17/04/2019.
 */

public class Data {
    private static List<Object> instance = new ArrayList<Object>();
    private Map<String,Boolean> initialized = new HashMap<>();
    private static Data data= new Data();
    private MainPage mainPageActivity;



    public static List<Object> getInstance(){
        return instance;
    }

    public static Data getData(){
        return data;
    }

    public boolean isInitialized(String id){
        if (initialized.get(id)!=null){
            return initialized.get(id);
        }
        return false;
    }
    public void setInitialized(String id){
        initialized.put(id,new Boolean(true));
    }


    public void setMainPageActivity(MainPage mainPageActivity) {
        this.mainPageActivity = mainPageActivity;
    }
    public MainPage getMainPageActivity() {
        return mainPageActivity;
    }


}
