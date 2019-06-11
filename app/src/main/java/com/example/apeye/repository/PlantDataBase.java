package com.example.apeye.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.example.apeye.model.Plant;
import com.example.apeye.utils.Plant_Dao;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Plant.class}, version = 1 , exportSchema = false)
public abstract class PlantDataBase extends RoomDatabase {

    private static PlantDataBase instance;

    public abstract Plant_Dao plant_dao();

    public static synchronized PlantDataBase getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PlantDataBase.class, "Plant_DB")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
    }

        return instance;
}

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private Plant_Dao plant_dao;

        private PopulateDbAsyncTask(PlantDataBase db) {
            plant_dao = db.plant_dao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            plant_dao.insert(new Plant("Tomato","The tomato is the edible, often red, berry of the plant Solanum lycopersicum, commonly known as a tomato plant. The species originated in western South America. The Nahuatl word tomatl gave rise to the Spanish word tomate, from which the English word tomato derived."));
            plant_dao.insert(new Plant("Corn","corn grows best when planted in several short rows instead of one long row. Corn has male flowers on top of the plant and female flowers called silks at leaf axils along the main stem. The tassel can produce up to a million pollen grains. ... After the plants are up, thin them to 12 inches apart."));
            plant_dao.insert(new Plant("potato", "The potato is a starchy, tuberous crop from the perennial nightshade Solanum tuberosum. In many contexts, potato refers to the edible tuber, but it can also refer to the plant itself. ... Wild potato species can be found throughout the Americas, from the United States to southern Chile."));
            plant_dao.insert(new Plant("Cherry","Cherry trees are a sight to behold in the spring, when they're covered in white or pink blossoms. ... Sour cherries are much smaller than sweet cherries and all varieties are self-fertile. They grow in zones 4 to 6. Cherry trees generally start bearing fruit in their fourth year; dwarf trees bear fruit a year earlier."));
            plant_dao.insert(new Plant("Grapes","A grape is a fruit, botanically a berry, of the deciduous woody vines of the flowering plant genus Vitis. Grapes can be eaten fresh as table grapes or they can be used for making wine, jam, juice, jelly, grape seed extract, raisins, vinegar, and grape seed oil."));
            plant_dao.insert(new Plant("Apple","Apples are extremely rich in important antioxidants, flavanoids, and dietary fiber. The phytonutrients and antioxidants in apples may help reduce the risk of developing cancer, hypertension, diabetes, and heart disease. This article provides a nutritional profile of the fruit and its possible health benefits."));
            plant_dao.insert(new Plant("strawberry","The garden strawberry is a widely grown hybrid species of the genus Fragaria, collectively known as the strawberries. It is cultivated worldwide for its fruit. The fruit is widely appreciated for its characteristic aroma, bright red color, juicy texture, and sweetness"));
            return null;
        }
    }
}
