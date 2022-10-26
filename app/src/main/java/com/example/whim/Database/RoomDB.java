package com.example.whim.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.whim.Models.Notes;

@Database(entities = Notes.class, version = 2, exportSchema = false)
public abstract class RoomDB extends RoomDatabase{
    private static RoomDB database;
    private static String database_name = "NoteApp";

    public synchronized static RoomDB getInstance(Context context){
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, database_name)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract MainDAO mainDAO();
}
