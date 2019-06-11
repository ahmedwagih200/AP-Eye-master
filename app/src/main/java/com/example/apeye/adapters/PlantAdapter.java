package com.example.apeye.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apeye.ui.main.fragments.Details;
import com.example.apeye.R;
import com.example.apeye.model.Plant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantHolder> {

    private List<Plant> plants = new ArrayList<>();
    private List<Integer> images = new ArrayList<>();
    private Context mcontext;

    public PlantAdapter(Context context) {
        mcontext = context;
    }

    @NonNull
    @Override
    public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plant_item, parent, false);
        return new PlantHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantHolder holder, int position) {
        setImages();
        Plant currentPlant = plants.get(position);
        holder.name.setText(currentPlant.getName());
        Integer currentImage = images.get(position);
        holder.image.setImageResource(currentImage);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    class PlantHolder extends RecyclerView.ViewHolder {
        private TextView name ;
        private ImageView image;

        PlantHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_view);
            image = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Plant clickedPlant = plants.get(getAdapterPosition());
                    String details = clickedPlant.getInfo();
                    Intent intent = new Intent(mcontext, Details.class);
                    intent.putExtra("info",details);
                    mcontext.startActivity(intent);
                }
            });
        }
    }

    private void setImages(){
        images.add(R.drawable.tomato);
        images.add(R.drawable.corn);
        images.add(R.drawable.potatoes);
        images.add(R.drawable.cherry);
        images.add(R.drawable.graps);
        images.add(R.drawable.apple);
        images.add(R.drawable.strawberrypng);
    }

    public void getPlants(List<Plant> plants){
        this.plants = plants;
        notifyDataSetChanged();
    }
}
