package com.example.gabri.thecalendar;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gabri.thecalendar.Adapters.SlotAdapter;
import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Database.AppDatabase;
import com.example.gabri.thecalendar.Model.Reservation;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class MainPage extends AppCompatActivity {


    final Context mContex=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Data.getData().setMainPageActivity(this);
        fullScreenFunction();

    /**
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
**/


        instantiateDatabase();
        setCalendar();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fullScreenFunction() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setCalendar() {
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        //System.out.println("------------------------------>"+startDate.toString());
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        endDate.add(Calendar.MONTH, 1);


        Calendar todayItaly= Calendar.getInstance(TimeZone.getTimeZone("CEST"));

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .defaultSelectedDate(todayItaly)
                .build();



        //horizontalCalendar.selectDate(tmp, true);
        //horizontalCalendar.goToday(true);

        //When you start the Application, generate the HourList of Today
        generateHourList(Calendar.getInstance(TimeZone.getTimeZone("CEST")));


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                System.out.println("Date selected ----> " +date.get(Calendar.DAY_OF_MONTH)+" "+date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
                generateHourList(date);
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });
    }

    public void generateHourList(Calendar date){

        System.out.println("Genero lista di ----> " +date.get(Calendar.DAY_OF_MONTH)+" "+date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));

        List<String> hours = new ArrayList<String>();
        String tmp="";

        for(int i=0;i<=24;i++){
            tmp=i+":00";
            hours.add(tmp);
        }

        RecyclerView recyclerView = findViewById(R.id.slotHourRecycler);
        SlotAdapter slotAdapter= new SlotAdapter(mContex,hours, date);
        recyclerView.setAdapter(slotAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContex));

        if(dateIsEqual(date))
            recyclerView.scrollToPosition(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        else
            recyclerView.scrollToPosition(AppParameter.startHour);


    }

    private void instantiateDatabase(){
        FlowManager.init(this);

        //To reset Database -> PAY ATTENYION
        //FlowManager.getDatabase(AppDatabase.class).reset(this);
    }

    public boolean dateIsEqual(Calendar date){
        Calendar dateOfToday = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        int dayOfMonthToday = dateOfToday.get(Calendar.DAY_OF_MONTH);
        String monthToday = dateOfToday.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int yearToday = dateOfToday.get(Calendar.YEAR);
        String dateInStringToday = dayOfMonthToday + "_" + monthToday + "_" + yearToday;

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = date.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        if(dateInString.equals(dateInStringToday))
            return true;
        else
            return false;
    }



}
