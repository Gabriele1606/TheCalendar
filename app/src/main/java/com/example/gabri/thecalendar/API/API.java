package com.example.gabri.thecalendar.API;


import com.example.gabri.thecalendar.Model.APIResponse;



import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Gabri on 18/04/19.
 *
 * Interface used to communicate with an external server using GET request.
 */

public interface API {
    /**
     * Url of the Server
     */
    String BASE_URL="https://randomuser.me/api/";

    @GET("?seed=empatica&page=1&results=100")
    Call<APIResponse> getResult();
}
