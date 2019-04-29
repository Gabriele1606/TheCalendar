package com.example.gabri.thecalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.gabri.thecalendar.MainPage;

/**
 * Created by Gabri on 26/04/19.
 *
 * SplashActivity to create the logo
 *
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainPage.class));
        finish();
    }

}
