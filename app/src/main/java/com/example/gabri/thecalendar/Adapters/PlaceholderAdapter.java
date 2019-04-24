package com.example.gabri.thecalendar.Adapters;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.DetailsReservationFragment;
import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Colors;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Glide.GlideApp;
import com.example.gabri.thecalendar.R;
import com.example.gabri.thecalendar.ReservationFragment;
import com.raizlabs.android.dbflow.sql.language.Operator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gabri on 17/04/19.
 */

public class PlaceholderAdapter extends  RecyclerView.Adapter<PlaceholderAdapter.PlaceholderHolder>{

    private List<Caregiver> caregivers= new ArrayList<Caregiver>();
    private HashMap<Caregiver,Integer> map;
    private Context mContext;
    private List<String> colorSet;
    private Calendar date;
    private int hour;



    public class PlaceholderHolder extends RecyclerView.ViewHolder{

        private TextView careName;
        private CircleImageView careImage;
        private TextView roomNumber;
        private CardView placeholderCard;


        public PlaceholderHolder(View itemView) {
            super(itemView);

            careName= itemView.findViewById(R.id.careName);
            careImage= itemView.findViewById(R.id.careImage);
            roomNumber= itemView.findViewById(R.id.roomNumber);
            placeholderCard=itemView.findViewById(R.id.slot_card_view);


        }
    }

    public PlaceholderAdapter(Context context, List<Caregiver> caregivers, Calendar date,int hour, HashMap<Caregiver, Integer> map){
        this.mContext=context;
        this.caregivers=caregivers;
        this.map=map;
        this.date=date;
        this.colorSet= Colors.getColorSet();
        this.hour=hour;
    }


    @Override
    public PlaceholderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.placeholder_card, parent, false);

        return new PlaceholderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceholderHolder holder, int position) {


        setTapPlaceholder(holder, position);
        String shortName;
        String careFirstName=caregivers.get(position).getName().getFirst();
        String careLastName=caregivers.get(position).getName().getLast();
        int room= map.get(caregivers.get(position));



        shortName=careFirstName+" "+careLastName.charAt(0)+".";

        holder.careName.setText(shortName);
        holder.roomNumber.setText("#"+Integer.toString(room));
        GlideApp.with(mContext).load(caregivers.get(position).getPicture().getThumbnail()).into(holder.careImage);

        //Since 10 color type were Hardcoded, the modulus value can be computed when there are more than 10 rooms
        position=position%10;
        holder.placeholderCard.setCardBackgroundColor(Color.parseColor(colorSet.get(position)));

    }

    @Override
    public int getItemCount() {
        return caregivers.size();
    }

    private void setTapPlaceholder(PlaceholderHolder holder, int position){
        final Caregiver selectedCare= this.caregivers.get(position);
        final Calendar selectedDate= this.date;
        final int hour= this.hour;
        if(placeholderEditable()) {
            holder.placeholderCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAREGIVER", selectedCare);
                    bundle.putSerializable("DATE", selectedDate);
                    bundle.putInt("HOUR", hour);

                    DetailsReservationFragment detailReservationFragment = new DetailsReservationFragment();
                    detailReservationFragment.setArguments(bundle);
                    FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, detailReservationFragment, "Reservation");
                    transaction.addToBackStack("Slot");
                    transaction.commit();
                }
            });
        }
    }

    private boolean placeholderEditable(){
        Calendar dateOfToday= Calendar.getInstance();

        dateOfToday.set(Calendar.MILLISECOND, 0);
        dateOfToday.set(Calendar.HOUR , 0);
        dateOfToday.set(Calendar.MINUTE , 0);
        dateOfToday.set(Calendar.SECOND , 0);

        this.date.set(Calendar.MILLISECOND, 0);
        this.date.set(Calendar.HOUR , 0);
        this.date.set(Calendar.MINUTE , 0);
        this.date.set(Calendar.SECOND , 0);

        //Compare the date of the placeholder selected with the date of today. If the date of the Placeholder is older than today make it not cliccable.
        if(this.date.compareTo(dateOfToday)>=0 && this.hour>Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            return true;
        else
            return false;
    }

}
