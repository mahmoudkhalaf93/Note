package com.example.notes.offlineDatabase;
import android.content.Context;

import com.example.notes.response.Note;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class} ,version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
   private static NoteDatabase instance;

   public static NoteDatabase getInstance(Context context){
       if(instance==null){
           instance= Room.databaseBuilder(context,NoteDatabase.class,"noteDatabase")
                   .allowMainThreadQueries().build();

       }
return instance;
   }

public abstract NoteDao noteDao();
}
