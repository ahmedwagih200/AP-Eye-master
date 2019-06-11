package com.example.apeye.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apeye.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Abdel-Rahman El-Shikh on 08-Jun-19.
 */
public class RecyclerPlantKindAdapter extends RecyclerView.Adapter<RecyclerPlantKindAdapter.ViewHolder> {
    private static final String TAG = "RecyclerPlantKindAdapte";
    //vars
    private int[] mPlants;
    private ArrayList<String> mNames ;
    private Context mContext;
    private int selectedPosition=-1;

    public RecyclerPlantKindAdapter(int[] mPlants, ArrayList<String> mNames, Context mContext) {
        this.mPlants = mPlants;
        this.mNames = mNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerPlantKindAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_check_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerPlantKindAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.image.setImageDrawable(mContext.getResources().getDrawable(mPlants[position]));
        holder.name.setText(mNames.get(position));
        holder.bind(holder.mCardView);

//        holder.mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(selectedPosition == -1){
//                    holder.mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
//                }else{
//                    if (selectedPosition == holder.getAdapterPosition())
//                }
//                if(!holder.checked){
//                    holder.checked = true;
//                    holder.mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
//                }
//                else{
//                    holder.checked = false;
//                    holder.mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
//                }
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mPlants.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView mCardView;
        TextView name;
        boolean checked;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imagePlantkind);
            name = itemView.findViewById(R.id.textPlantName);
            mCardView = itemView.findViewById(R.id.cardView);
            checked = false;
        }
        void bind(CardView cardView){
            if (selectedPosition == -1) {
                mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                if (selectedPosition == getAdapterPosition()) {
                    mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                }
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                    if (selectedPosition != getAdapterPosition()) {
                        notifyItemChanged(selectedPosition);
                        selectedPosition = getAdapterPosition();
                    }
                }
            });
        }
        }
    public String getSelected() {
        if (selectedPosition != -1) {
            return mNames.get(selectedPosition);
        }
        return null;
    }
    }


