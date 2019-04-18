package com.example.gabri.thecalendar.API;


import com.example.gabri.thecalendar.Model.APIResponse;



import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Gabri on 18/04/19.
 */

public interface API {

    String BASE_URL="https://randomuser.me/api/";

    @GET("?seed=empatica&page=1&results=100")
    Call<APIResponse> getResult();
}
