package com.example.notes.offlineDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notes.response.Note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    public void addNote(Note note);

    @Update
    public void updateNote(Note note);

    @Delete
    public void deleteNote(Note note);

    @Query("SELECT * FROM notes")
    public List<Note> getNotes();


}
