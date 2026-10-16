package com.example.lab5_1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Room Data Access Object (DAO) for note operations.
 */
@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<NoteEntity> getAllNotes();

    @Query("SELECT * FROM notes WHERE user_name LIKE '%' || :query || '%' ORDER BY id DESC")
    List<NoteEntity> searchNotesByUser(String query);

    @Insert
    long insertNote(NoteEntity note);

    @Update
    void updateNote(NoteEntity note);

    @Delete
    void deleteNote(NoteEntity note);

    @Query("DELETE FROM notes")
    void deleteAll();
}
