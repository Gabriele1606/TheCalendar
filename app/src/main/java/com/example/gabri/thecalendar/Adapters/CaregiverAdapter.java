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
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Data;
import com.example.gabri.thecalendar.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Gabri on 18/04/19.
 */

public class CaregiverAdapter extends  RecyclerView.Adapter<CaregiverAdapter.CaregiverHolder>{


    private List<Caregiver> caregivers= new ArrayList<Caregiver>();
    private Context mContext;

    public class CaregiverHolder extends RecyclerView.ViewHolder{

        private TextView caregiverName;
        private CircleImageView caregiverImage;
        private CardView cardview;

        public CaregiverHolder(View itemView) {
            super(itemView);

            caregiverName= itemView.findViewById(R.id.caregiverName_card);
            caregiverImage= itemView.findViewById(R.id.caregiverImage_card);
            cardview=itemView.findViewById(R.id.caregiver_card);

        }
    }


    public CaregiverAdapter(Context context, List<Caregiver> caregivers){
        this.caregivers=caregivers;
        this.mContext=context;
    }

    @Override
    public CaregiverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.caregiver_card, parent, false);

        return new CaregiverHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CaregiverHolder holder, int position) {
        String caregiverTitle=caregivers.get(position).getName().getTitle();
        String caregiverFirst=caregivers.get(position).getName().getFirst();
        String caregiverLast=caregivers.get(position).getName().getLast();
        String caregiverImage=caregivers.get(position).getPicture().getMedium();
        holder.caregiverName.setText(caregiverTitle+" "+caregiverFirst+" "+caregiverLast);
        Glide.with(mContext).load(caregiverImage).into(holder.caregiverImage);

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
