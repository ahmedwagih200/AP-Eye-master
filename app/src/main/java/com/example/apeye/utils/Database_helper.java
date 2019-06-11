//package com.example.apeye;
//
//import androidx.room.Room;
//import android.content.Context;
//import android.util.Log;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//public class Database_helper {
//
//
//    private static final String TAG = Database_helper.class.getSimpleName();
//    private static final String DATABASE_NAME = "AppDataBase.db";
//
//    private PlantDataBase plantDataBase;
//    private static Context appContext;
//
//    private static class Holder {
//        private static final Database_helper INSTANCE = new Database_helper();
//    }
//
//    public static synchronized Database_helper getInstance(Context context) {
//        appContext = context;
//        return Holder.INSTANCE;
//    }
//
//    public Database_helper() {
//        //call method that check if database not exists and copy pre populated file from assets
//        copyAttachedDatabase(appContext, DATABASE_NAME);
//        plantDataBase = Room.databaseBuilder(appContext,
//                PlantDataBase.class, DATABASE_NAME)
//                .addMigrations(PlantDataBase.MIGRATION_1_2)
//                .build();
//    }
//
//    public PlantDataBase getRoomDatabase() {
//        return plantDataBase;
//    }
//
//
//    private void copyAttachedDatabase(Context context, String databaseName) {
//        final File dbPath = context.getDatabasePath(databaseName);
//
//        // If the database already exists, return
//        if (dbPath.exists()) {
//            return;
//        }
//
//        // Make sure we have a path to the file
//        dbPath.getParentFile().mkdirs();
//
//        // Try to copy database file
//        try {
//            final InputStream inputStream = context.getAssets().open("databases/" + databaseName);
//            final OutputStream output = new FileOutputStream(dbPath);
//
//            byte[] buffer = new byte[8192];
//            int length;
//
//            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
//                output.write(buffer, 0, length);
//            }
//
//            output.flush();
//            output.close();
//            inputStream.close();
//        }
//        catch (IOException e) {
//            Log.d(TAG, "Failed to open file", e);
//            e.printStackTrace();
//        }
//    }
//
//}

