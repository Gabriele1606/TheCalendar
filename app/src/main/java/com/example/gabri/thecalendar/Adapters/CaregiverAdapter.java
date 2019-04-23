package com.example.gabri.thecalendar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.Model.AppParameter;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.Model.Glide.GlideApp;
import com.example.gabri.thecalendar.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Gabri on 18/04/19.
 */

public class CaregiverAdapter extends  RecyclerView.Adapter<CaregiverAdapter.CaregiverHolder>{


    private List<Caregiver> caregivers= new ArrayList<Caregiver>();
    private Context mContext;
    private HashMap<String, Integer> careHourWeekMap;

    public class CaregiverHolder extends RecyclerView.ViewHolder{

        private TextView caregiverName;
        private CircleImageView caregiverImage;
        private CardView cardview;
        private TextView careAge;
        private TextView careCity;
        private TextView hourWeek;
        private TextView extraHourWeek;

        public CaregiverHolder(View itemView) {
            super(itemView);

            caregiverName= itemView.findViewById(R.id.caregiverName_card);
            caregiverImage= itemView.findViewById(R.id.caregiverImage_card);
            cardview=itemView.findViewById(R.id.caregiver_card);
            careAge=itemView.findViewById(R.id.care_age);
            careCity=itemView.findViewById(R.id.care_city_card);
            hourWeek=itemView.findViewById(R.id.hour_week);
            extraHourWeek=itemView.findViewById(R.id.extra_hour_week);

        }
    }


    public CaregiverAdapter(Context context, List<Caregiver> caregivers, HashMap<String, Integer> careHourWeekMap){
        this.caregivers=caregivers;
        this.mContext=context;
        this.careHourWeekMap=careHourWeekMap;
    }

    @Override
    public CaregiverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.caregiver_card, parent, false);

        return new CaregiverHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CaregiverHolder holder, int position) {
        String shortName;
        String careTitle=caregivers.get(position).getName().getTitle();
        String careFirstName=caregivers.get(position).getName().getFirst();
        String careLastName=caregivers.get(position).getName().getLast();


        shortName=careTitle+" "+careFirstName+" "+careLastName;

        String caregiverTitle=caregivers.get(position).getName().getTitle();
        String caregiverFirst=caregivers.get(position).getName().getFirst();
        String caregiverLast=caregivers.get(position).getName().getLast();
        String caregiverImage=caregivers.get(position).getPicture().getThumbnail();
        holder.caregiverName.setText(shortName);
        GlideApp.with(mContext).load(caregiverImage).into(holder.caregiverImage);

        holder.extraHourWeek.setText("1");
        if(!this.careHourWeekMap.containsKey(this.caregivers.get(position).getEmail())) {
            holder.hourWeek.setText(Integer.toString(AppParameter.hourPerWeek));
        }else{
           holder.hourWeek.setText(Integer.toString(AppParameter.hourPerWeek-this.careHourWeekMap.get(this.caregivers.get(position).getEmail())));
        }
        holder.careAge.setText(Integer.toString(this.caregivers.get(position).getDob().getAge()));
        holder.careCity.setText(this.caregivers.get(position).getLocation().getCity());
        setTapCaregiver(holder, position);
    }

    @Override
    public int getItemCount() {
        return caregivers.size();
    }


    public void setTapCaregiver(final CaregiverHolder holder, final int position){
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Data.getData().getMainPageActivity().getSupportFragmentManager().findFragmentByTag("Caregivers").getClass());
                intent.putExtra("caregiver_selected", (Serializable) caregivers.get(position));
                Data.getData().getMainPageActivity().getSupportFragmentManager().findFragmentByTag("Reservation").onActivityResult(3333,RESULT_OK, intent);
                Data.getData().getMainPageActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }


}
