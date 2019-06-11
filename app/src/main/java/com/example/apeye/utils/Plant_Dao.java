package com.example.apeye.utils;

import com.example.apeye.model.Plant;

import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface Plant_Dao {

    @Query("SELECT * FROM Plant ")
    LiveData<List<Plant>> getPlants();

    @Insert
    void insert(Plant plant);
}
