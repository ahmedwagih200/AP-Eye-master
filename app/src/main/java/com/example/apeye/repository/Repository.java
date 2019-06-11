package com.example.apeye.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.apeye.model.Plant;
import com.example.apeye.utils.Plant_Dao;

import java.util.List;
import androidx.lifecycle.LiveData;

public class Repository {

    private Plant_Dao plant_dao;
    private LiveData <List<Plant>> allPlants;

    public Repository(Application application){
        PlantDataBase dataBase = PlantDataBase.getInstance(application);
        plant_dao = dataBase.plant_dao();

        allPlants = plant_dao.getPlants();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return allPlants;
    }

      public void insert(Plant plant) {
        new InsertAsyncTask(plant_dao).execute(plant);
    }

    private static class InsertAsyncTask extends AsyncTask<Plant, Void, Void>{

        private Plant_Dao plant_dao ;
        private InsertAsyncTask(Plant_Dao plant_dao){
            this.plant_dao = plant_dao;
        }
        @Override
        protected Void doInBackground(Plant... plants) {
            plant_dao.insert(plants[0]);
            return null;
        }
    }
}
