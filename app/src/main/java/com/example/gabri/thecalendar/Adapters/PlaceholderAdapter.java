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
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gabri on 17/04/19.
 *
 * PlaceholderAdapter handle the horizntal RecyclerView
 *
 */

public class PlaceholderAdapter extends  RecyclerView.Adapter<PlaceholderAdapter.PlaceholderHolder>{

    /**
     * Contect of the MainActivity
     */
    private Context mContext;
    private List<Caregiver> caregivers= new ArrayList<Caregiver>();
    private HashMap<Caregiver,Integer> map;
    private List<String> colorSet;
    private Calendar date;
    private int hour;


    /**
     * Placeholder class
     */
    public class PlaceholderHolder extends RecyclerView.ViewHolder{

        private TextView careName;
        private CircleImageView careImage;
        private TextView roomNumber;
        private CardView placeholderCard;

        /**
         * Constructor which associate the view elements
         * @param itemView
         */
        public PlaceholderHolder(View itemView) {
            super(itemView);

            careName= itemView.findViewById(R.id.careName);
            careImage= itemView.findViewById(R.id.careImage);
            roomNumber= itemView.findViewById(R.id.roomNumber);
            placeholderCard=itemView.findViewById(R.id.slot_card_view);


        }
    }

    /**
     * Constructor
     * @param context
     * @param caregivers
     * @param date
     * @param hour
     * @param map
     */
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

    /**
     * Return the items inside the Horizontal RecyclerView
     * @return
     */
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
                    startDetailsReservationFragment(bundle);
                }
            });
        }
    }

    /**
     * This method prepare and execute the details reservation fragment.
     * @param bundle
     */
    private void startDetailsReservationFragment(Bundle bundle){
        DetailsReservationFragment detailReservationFragment = new DetailsReservationFragment();
        detailReservationFragment.setArguments(bundle);
        FragmentTransaction transaction = Data.getData().getMainPageActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, detailReservationFragment, "Reservation");
        transaction.addToBackStack("Slot");
        transaction.commit();
    }

    /**
     * Check id a placeholder can be modified or delete.
     * For example and "older" placeholde cannot be modified or removed
     * @return true if can be modified/deleted, false otherwise
     */
    private boolean placeholderEditable(){

        //Compare the date of the placeholder selected with the date of today. If the date of the Placeholder is older than today make it not cliccable.
        if(dateIsAfter(this.date) || slotOfEqualDateAvailable(this.date,this.hour))
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
