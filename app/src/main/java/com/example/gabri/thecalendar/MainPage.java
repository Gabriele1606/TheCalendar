package com.example.gabri.thecalendar;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gabri.thecalendar.API.API;
import com.example.gabri.thecalendar.Adapters.SlotAdapter;
import com.example.gabri.thecalendar.Model.APIResponse;
import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Controller.AutoFill;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.CaregiverDB;
import com.example.gabri.thecalendar.Model.Database.DBmanager;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import jp.wasabeef.blurry.Blurry;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * This is the Main Activity of the TheCalendar application.
 *
 */

public class MainPage extends AppCompatActivity {
    /**
     * In this variable is saved the context of the MainActivity (This) because
     * it will be used in the SlotAdapter Class
     */
    private final Context mContex=this;

    /**
     * This is the RecyclerView Adapter used to generate all the time slots
     * visible in the home page
     */
    private SlotAdapter slotAdapter;
    /**
     * Variable used to generate the HorizontalCalendar and it contains the date
     * of today.
     */
    private Calendar date;
    private RelativeLayout layoutcontent;
    private ImageView bluerImage;

    /**
     * Initialize the state of all variables when The main Activity is created.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        layoutcontent=findViewById(R.id.layoutcontent);
        bluerImage=findViewById(R.id.bluer_image);
        Data.getData().setMainPageActivity(this);
        fullScreenFunction();
        instantiateDatabase();
        setCalendar();
        getCaregivers();


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

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Method used to set the System UI Visibility to full screen.
     */

    private void fullScreenFunction() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * In this method the horizonatl calendar is instantiated and set with correct date,
     * referred to CEST timezone.
     *
     */
    private void setCalendar() {
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
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

        setDate(Calendar.getInstance(TimeZone.getTimeZone("CEST")));
        List<String> hours=generateHourList();
        createSlotAdapterRecycler(hours,Calendar.getInstance(TimeZone.getTimeZone("CEST")));

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                setDate(date);
                List<String> hours=generateHourList();
                createSlotAdapterRecycler(hours,date);
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

    /**
     * This method generate the hour list form 00:00 to 23:00 24H format
     * @return a list of hours
     */

    public List<String> generateHourList(){
        List<String> hours = new ArrayList<String>();
        String tmp="";

        for(int i=0;i<24;i++){
            if(i<10){
                tmp="0"+i+":00";
                hours.add(tmp);
            }else{
                tmp=i+":00";
                hours.add(tmp);
            }
        }
        return hours;
    }

    /**
     * This method create a slot adapter for the date specified as input
     * @param hours list of hours  generated
     * @param date date selected form the Horizontal calendar
     */
    public void createSlotAdapterRecycler(List<String> hours, Calendar date){
        RecyclerView recyclerView = findViewById(R.id.slotHourRecycler);
        this.slotAdapter= new SlotAdapter(mContex,hours, date);
        recyclerView.setAdapter(slotAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContex));



        if(dateIsEqual(date))
            recyclerView.scrollToPosition(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        else
            recyclerView.scrollToPosition(AppParameter.startHour);
    }

    /**
     * Mehtod use to instantiate DBFlow database.
     */
    private void instantiateDatabase(){
        FlowManager.init(this);
        //To reset Database -> PAY ATTENYION
        //FlowManager.getDatabase(AppDatabase.class).reset(this);
    }

    /**
     * Compare the Date of today, with the date taken as input
     * @param date date to compare with today.
     * @return true if the 2 dates are equal, false otherwise.
     */

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

    /**
     * This method check if the device is connected to internet.
     * @return true if the device is connected, false otherwise.
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null && activeNetworkInfo.isConnected())
            return true;
        return false;

    }

    private void getCaregivers(){

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

        Cache cache = new Cache(getCacheDir(), AppParameter.cacheSize);

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
                    Toast.makeText(getContext(),"For the first time, you need Internet connection!", Toast.LENGTH_LONG).show();
                }else{
                    for(int i=0;i<result.getCaregivers().size();i++)
                        addCaregiverToDb(result.getCaregivers().get(i));
                    notifyAutofillButton();

                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(getContext(),"Somenthing went wrong with Internet connection!",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *When the application starts for the first time, download all the caregivers and put them into cache memory.
     * When all the caregiver are available the FAB button will appear on the MainPage with Toast notification.
     *
     * This method also set the OnClickListener on the FAB button. If it is pressed the AutoFill procedure will starts.
     *
     */

    private void notifyAutofillButton(){
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(),"AutoFill function is now available!", Toast.LENGTH_LONG).show();
        //On click Listener on the FAB button in Home Page
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AutoFill autoFill= new AutoFill(date,slotAdapter);
                autoFill.start();
                Blurry.with(MainPage.this).capture((FrameLayout)findViewById(R.id.mainLayout)).into(bluerImage);
                layoutcontent.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                //Creation of dialog Alert to confirm the choice to start the autofill function
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                builder.setCancelable(false);
                builder.setTitle("Auto-Fill");
                builder.setMessage("Are you sure to fill empty slots?\nIt will follows the required criteria of Caregiver choice.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        fab.setVisibility(View.VISIBLE);
                        layoutcontent.setVisibility(View.GONE);
                        slotAdapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        fab.setVisibility(View.VISIBLE);
                        layoutcontent.setVisibility(View.GONE);
                        if(autoFill.isAlive()) {
                            autoFill.interrupt();
                        }
                        else {
                            autoFill.cancelReservationsDone();
                            slotAdapter.notifyDataSetChanged();
                        }


                    }
                });

                builder.show();

            }
        });

    }

    /**
     * This mehtod inser a carevire into DB.
     * @param caregiver
     */

    private void addCaregiverToDb(Caregiver caregiver){


        DBmanager dbManager= new DBmanager();
        CaregiverDB caregiverDB;
        caregiverDB=dbManager.getCaregiverFromDB(caregiver.getEmail());

        if (caregiverDB==null) {
            caregiverDB= new CaregiverDB();
            caregiverDB.setGender(caregiver.getGender());
            caregiverDB.setName(caregiver.getName());
            caregiverDB.setLocation(caregiver.getLocation());
            caregiverDB.setTimezone(caregiver.getTimezone());
            caregiverDB.setEmail(caregiver.getEmail());
            caregiverDB.setCell(caregiver.getPhone());
            caregiverDB.setPhone(caregiver.getPhone());
            caregiverDB.setDob(caregiver.getDob());
            caregiverDB.setLogin(caregiver.getLogin());
            caregiverDB.setPicture(caregiver.getPicture());
            caregiverDB.save();
        }

    }

    public void setDate(Calendar date){
        this.date=date;
    }

}
