package com.example.gabri.thecalendar.Adapters;

import android.content.Context;
import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Glide.GlideApp;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.Model.Reservation_Table;
import com.example.gabri.thecalendar.R;
import com.example.gabri.thecalendar.ReservationFragment;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gabri on 17/04/19.
 */

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotHolder> {


    private List<String> hours = new ArrayList<String>();
    private Context mContext;
    private Calendar date;

    public class SlotHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView hour;
        private View view;
        private ImageView addButton;
        private RecyclerView placeholderRecyclerView;
        private CardView cardView;


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


        int dayOfMonth= date.get(Calendar.DAY_OF_MONTH);
        String month=date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        int year=date.get(Calendar.YEAR);

        String dateInString=dayOfMonth+"_"+month+"_"+year;


        List<Reservation> reservations=SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.slot.eq(position)).queryList();

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


    @Override
    public int getItemCount() {
        return hours.size();
    }

    //Remeber that Position=Hour
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

                    ReservationFragment reservationFragment = new ReservationFragment();
                    reservationFragment.setArguments(bundle);
                    FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, reservationFragment, "Reservation");
                    transaction.addToBackStack("Slot");
                    transaction.commit();


                }
            });
        }else{
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.lightGray));
            holder.addButton.setVisibility(View.INVISIBLE);
            holder.linearLayout.setClickable(false);
        }

    }

    /**
     * Method used to verify if the slot can be Cliccable or not (could be full)
     * @param fDate
     * @param hour
     */

    private boolean slotAvailable(Calendar fDate, int hour) {

        if (!reservationSlotComplete(fDate, hour) && respectHourOfWork(hour) && (dateIsAfter(fDate) || slotOfEqualDateAvailable(fDate, hour)))
            return true;
         else
            return false;
    }


    private boolean reservationSlotComplete(Calendar fDate, int hour){
        int dayOfMonth = fDate.get(Calendar.DAY_OF_MONTH);
        String month = fDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = fDate.get(Calendar.YEAR);
        String dateInString = dayOfMonth + "_" + month + "_" + year;
        List<Reservation> reservations = SQLite.select().from(Reservation.class).where(Reservation_Table.date.eq(dateInString), Reservation_Table.slot.eq(hour)).queryList();

        if(reservations.size()< AppParameter.roomNumber)
            return false;
        else
            return true;
    }

    private boolean respectHourOfWork(int hour){
        if(hour >= AppParameter.startHour && hour <= AppParameter.stopHour)
            return true;
        else
            return false;
    }

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
