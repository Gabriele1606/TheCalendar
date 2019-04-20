package com.example.gabri.thecalendar.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Reservation;
import com.example.gabri.thecalendar.Model.Reservation_Table;
import com.example.gabri.thecalendar.R;
import com.example.gabri.thecalendar.ReservationFragment;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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


        public SlotHolder(View itemView) {
            super(itemView);
            linearLayout= itemView.findViewById(R.id.slotLinearLayout);
            hour=itemView.findViewById(R.id.hour);
            view=itemView;
            placeholderRecyclerView= itemView.findViewById(R.id.placeholderRecycler);
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
        Glide.with(mContext).load(R.drawable.red_plus_icon).into(holder.addButton);
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
        PlaceholderAdapter placeholderAdapter= new PlaceholderAdapter(mContext,caregivers,map);
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

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("DATE", fDate);
                bundle.putInt("HOUR", hour);

                ReservationFragment reservationFragment= new ReservationFragment();
                reservationFragment.setArguments(bundle);
                System.out.println("Data "+Data.getData().getMainPageActivity().getPackageName());
                FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, reservationFragment, "Reservation");
                transaction.addToBackStack("Slot");
                transaction.commit();


            }
        });

    }
}
