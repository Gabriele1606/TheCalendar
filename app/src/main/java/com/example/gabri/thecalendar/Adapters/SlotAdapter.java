package com.example.gabri.thecalendar.Adapters;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.gabri.thecalendar.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Database.DBmanager;
import com.example.gabri.thecalendar.Model.Glide.GlideApp;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.R;
import com.example.gabri.thecalendar.Fragment.ReservationFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.TimeZone;



/**
 * Created by Gabri on 17/04/19.
 *
 * SlotAdapter Class handle the hour slots RecyclerView
 *
 */

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotHolder> {
    /**
     * Contex of the MainActivity
     */
    private Context mContext;
    private List<String> hours = new ArrayList<String>();
    private Calendar date;

    public class SlotHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView hour;
        private View view;
        private ImageView addButton;
        private RecyclerView placeholderRecyclerView;
        private CardView cardView;

        /**
         * Constructor
         * @param itemView
         */
        public SlotHolder(View itemView) {
            super(itemView);
            linearLayout= itemView.findViewById(R.id.slotLinearLayout);
            hour=itemView.findViewById(R.id.hour);
            view=itemView;
            placeholderRecyclerView= itemView.findViewById(R.id.placeholderRecycler);
            cardView=itemView.findViewById(R.id.slotCardView);
            addButton=itemView.findViewById(R.id.add_button);
        }
    }

    /**
     * Slot Adapter constructor.
     * @param context
     * @param hours
     * @param date
     */
    public SlotAdapter(Context context, List<String> hours, Calendar date){
        this.mContext=context;
        this.hours=hours;
        this.date=date;
    }


    @Override
    public SlotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slot_card, parent, false);

        return new SlotHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SlotHolder holder, int position) {
        holder.hour.setText(hours.get(position));
        GlideApp.with(mContext).load(R.drawable.red_plus_icon).into(holder.addButton);
        setTapSlot(holder,this.date, position);

        DBmanager dbManager= new DBmanager();

        List<Reservation> reservations=dbManager.getReservationInASlot(this.date,position);
        List<Caregiver> caregivers = new ArrayList<Caregiver>();

        //HashMap with Caregiver and Room of the Caregiver
        HashMap<Caregiver, Integer> map = new HashMap<>();

        for(int i=0;i<reservations.size();i++){
            map.put(reservations.get(i).getCaregiver(), reservations.get(i).getRoomNumber());
            caregivers.add(reservations.get(i).getCaregiver());
        }

        RecyclerView recyclerView = holder.placeholderRecyclerView;
        PlaceholderAdapter placeholderAdapter= new PlaceholderAdapter(mContext,caregivers,this.date,position, map);
        recyclerView.setAdapter(placeholderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));

    }

    /**
     * Return the items inside the RecyclerView
     * @return
     */

    @Override
    public int getItemCount() {
        return hours.size();
    }

    /**
     * This method se the possibility to set the Tap on a specific slot.
     * The slot can be tap if the hour in not expired, the date is not expired and also if the slot is not full (All room full).
     * @param holder
     * @param date
     * @param position
     */
    private void setTapSlot(SlotHolder holder, Calendar date, int position) {
        final Calendar fDate=date;
        final int hour=position;

        if(slotAvailable(fDate,hour)) {
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.addButton.setVisibility(View.VISIBLE);
            holder.linearLayout.setClickable(true);
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DATE", fDate);
                    bundle.putInt("HOUR", hour);
                    startReservationFragment(bundle);
                }
            });
        }else{
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.lightGray));
            holder.addButton.setVisibility(View.INVISIBLE);
            holder.linearLayout.setClickable(false);
        }

    }

    /**
     * This method prepare and execute the reservation fragment.
     * @param bundle
     */
    public void startReservationFragment(Bundle bundle){
        ReservationFragment reservationFragment = new ReservationFragment();
        reservationFragment.setArguments(bundle);
        FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, reservationFragment, "Reservation");
        transaction.addToBackStack("Slot");
        transaction.commit();
    }

    /**
     * Method used to verify if the slot can be Clickable or not (could be full)
     * @param fDate
     * @param hour
     */

    private boolean slotAvailable(Calendar fDate, int hour) {

        if (!reservationSlotComplete(fDate, hour) && respectHourOfWork(hour) && (dateIsAfter(fDate) || slotOfEqualDateAvailable(fDate, hour)))
            return true;
         else
            return false;
    }

    /**
     * This method check if the slot is full or not (all rooms full or not).
     * @param fDate
     * @param hour
     * @return return true if the slot is full, false otherwise.
     */
    private boolean reservationSlotComplete(Calendar fDate, int hour){
        DBmanager dbManager= new DBmanager();
        List<Reservation> reservations=dbManager.getReservationInASlot(fDate,hour);
        if(reservations.size()< AppParameter.roomNumber)
            return false;
        else
            return true;
    }

    /**
     * this method check if the hour respect the assignement contraints of Hours of work.
     * @param hour
     * @return true the slot respect the hours of work, false otherwise.
     */
    private boolean respectHourOfWork(int hour){
        if(hour >= AppParameter.startHour && hour <= AppParameter.stopHour)
            return true;
        else
            return false;
    }

    /**
     * this method check if the date is expired or not.
     * @param fDate
     * @return true if the date is expired, false otherwise.
     */
    private boolean dateIsAfter(Calendar fDate){
        Calendar dateOfToday = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        if(fDate.compareTo(dateOfToday)>0)
            return true;
        else
            return false;
    }

    private boolean slotOfEqualDateAvailable(Calendar fDate, int hour){
        Calendar dateOfToday = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        int dayOfMonthToday = dateOfToday.get(Calendar.DAY_OF_MONTH);
        String monthToday = dateOfToday.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int yearToday = dateOfToday.get(Calendar.YEAR);
        String dateInStringToday = dayOfMonthToday + "_" + monthToday + "_" + yearToday;

        int dayOfMonth = fDate.get(Calendar.DAY_OF_MONTH);
        String month = fDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = fDate.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;

        if(dateInStringToday.equals(dateInString) && hour>Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            return true;
        else
            return false;

    }

}
