package com.example.gabri.thecalendar.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gabri.thecalendar.Model.Caregiver;
import com.example.gabri.thecalendar.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gabri on 17/04/19.
 */

public class PlaceholderAdapter extends  RecyclerView.Adapter<PlaceholderAdapter.PlaceholderHolder>{

    private List<Caregiver> caregivers= new ArrayList<Caregiver>();
    private Context mContext;


    public class PlaceholderHolder extends RecyclerView.ViewHolder{

        private TextView careName;
        private CircleImageView careImage;
        private TextView roomNumber;


        public PlaceholderHolder(View itemView) {
            super(itemView);

            careName= itemView.findViewById(R.id.careName);
            careImage= itemView.findViewById(R.id.careImage);
            roomNumber= itemView.findViewById(R.id.roomNumber);

        }
    }

    public PlaceholderAdapter(Context context, List<Caregiver> caregivers){
        this.mContext=context;
        this.caregivers=caregivers;
    }

    @Override
    public PlaceholderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.placeholder_card, parent, false);

        return new PlaceholderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceholderHolder holder, int position) {

        holder.careName.setText(caregivers.get(position).getName());
        holder.roomNumber.setText(caregivers.get(position).getRoom());

    }

    @Override
    public int getItemCount() {
        return caregivers.size();
    }






}
