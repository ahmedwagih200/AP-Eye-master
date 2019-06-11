package com.example.apeye.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Plant")
public class Plant {

    @PrimaryKey(autoGenerate = true)
    private int ID;
    private String Name;
    private String Info;

    public Plant(String Name, String Info) {
        this.Name = Name;
        this.Info = Info;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getInfo() {
        return Info;
    }
}
