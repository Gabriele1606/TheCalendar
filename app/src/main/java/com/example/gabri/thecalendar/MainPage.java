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
import com.example.gabri.thecalendar.Model.Data;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();

        //When you start the Application, generate the HourList of Today
        generateHourList(Calendar.getInstance());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
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

    private void generateHourList(Calendar date){
        List<String> hours = new ArrayList<String>();
        String tmp="";

        for(int i=0;i<=12;i++){
            tmp=i+":"+date.get(Calendar.DAY_OF_MONTH);
            hours.add(tmp);
        }

        RecyclerView recyclerView = findViewById(R.id.slotHourRecycler);
        SlotAdapter slotAdapter= new SlotAdapter(mContex,hours, date);
        recyclerView.setAdapter(slotAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContex));
    }



}
