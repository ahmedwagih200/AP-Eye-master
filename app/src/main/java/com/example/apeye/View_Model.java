package com.example.apeye;

import android.app.Application;

import com.example.apeye.model.Plant;
import com.example.apeye.repository.Repository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class View_Model extends AndroidViewModel {

    private Repository repository;
    private  LiveData <List<Plant>> allPlants;

    public View_Model(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allPlants = repository.getAllPlants();
    }

    public void insert(Plant plant){
        repository.insert(plant);
    }

    public LiveData <List<Plant>> getAllPlants() {
        return allPlants;
    }
}
