package com.example.gabri.thecalendar.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.Model.Colors;
import com.example.gabri.thecalendar.R;
import com.raizlabs.android.dbflow.sql.language.Operator;

import java.util.ArrayList;
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

    public PlaceholderAdapter(Context context, List<Caregiver> caregivers, HashMap<Caregiver, Integer> map){
        this.mContext=context;
        this.caregivers=caregivers;
        this.map=map;
        this.colorSet= Colors.getColorSet();
    }

    @Override
    public PlaceholderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.placeholder_card, parent, false);

        return new PlaceholderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceholderHolder holder, int position) {



        String shortName;
        String careFirstName=caregivers.get(position).getName().getFirst();
        String careLastName=caregivers.get(position).getName().getLast();
        int room= map.get(caregivers.get(position));

        shortName=Character.toUpperCase(careFirstName.charAt(0))+careFirstName.substring(1)+" "+Character.toUpperCase(careLastName.charAt(0))+".";

        holder.careName.setText(shortName);
        holder.roomNumber.setText("#"+Integer.toString(room));
        Glide.with(mContext).load(caregivers.get(position).getPicture().getThumbnail()).into(holder.careImage);
        holder.placeholderCard.setCardBackgroundColor(Color.parseColor(colorSet.get(position)));

    }

    @Override
    public int getItemCount() {
        return caregivers.size();
    }






}
