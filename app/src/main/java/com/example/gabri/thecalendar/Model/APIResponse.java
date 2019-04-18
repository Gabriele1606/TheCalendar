package com.example.gabri.thecalendar.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * Created by Gabri on 18/04/19.
 */

public class APIResponse {


    @SerializedName("results")
    @Expose
    private ArrayList<Caregiver> caregivers;

    public APIResponse(ArrayList<Caregiver> caregivers) {
        this.caregivers = caregivers;
    }

    public ArrayList<Caregiver> getCaregivers() {
        return caregivers;
    }
}
