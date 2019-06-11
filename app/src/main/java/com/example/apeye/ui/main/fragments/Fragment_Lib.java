package com.example.apeye.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apeye.R;
import com.example.apeye.View_Model;
import com.example.apeye.adapters.PlantAdapter;
import com.example.apeye.model.Plant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_Lib extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_lib, container ,false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final PlantAdapter plant_adapter = new PlantAdapter(getContext());
        recyclerView.setAdapter(plant_adapter);

        View_Model view_model = ViewModelProviders.of(this).get(View_Model.class);
        view_model.getAllPlants().observe(this, new Observer<List<Plant>>() {
            @Override
            public void onChanged(List<Plant> plants) {

                plant_adapter.getPlants(plants);
            }
        });
        return rootView;
    }
}

