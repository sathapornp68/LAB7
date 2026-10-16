package com.example.lab5_1;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Notes with Room Database integration and Event-driven notifications.
 */
public class NoteRepository {
    private static NoteRepository instance;
    private NoteDao noteDao;
    private final List<OnNoteAddedListener> listeners = new ArrayList<>();

    private NoteRepository(Context context) {
        if (context != null) {
            AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
            this.noteDao = db.noteDao();

            // Populate initial sample data if database is empty
            if (noteDao.getAllNotes().isEmpty()) {
                User demoUser = new User("John Doe", "john@example.com");
                TextNote welcomeNote = new TextNote("Welcome Note", demoUser, "Welcome to Room Database Note App! This note is persisted in SQLite via Room.");
                noteDao.insertNote(NoteMapper.toEntity(welcomeNote));

                List<CheckListItem> demoItems = new ArrayList<>();
                demoItems.add(new CheckListItem("Design Room Entity & DAO", true));
                demoItems.add(new CheckListItem("Setup AppDatabase & TypeConverters", true));
                demoItems.add(new CheckListItem("Connect Room with MVC Architecture", true));
                demoItems.add(new CheckListItem("Test SQLite persistence on Emulator", false));
                CheckListNote projectTasks = new CheckListNote("Room Project Tasks", demoUser, demoItems);
                noteDao.insertNote(NoteMapper.toEntity(projectTasks));
            }
        }
    }

    public static synchronized NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context);
        }
        return instance;
    }

    public static synchronized NoteRepository getInstance() {
        if (instance == null) {
            instance = new NoteRepository(null);
        }
        return instance;
    }

    public void initDatabase(Context context) {
        if (noteDao == null && context != null) {
            AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
            this.noteDao = db.noteDao();
        }
    }

    public List<Note> getNotes() {
        if (noteDao != null) {
            List<NoteEntity> entities = noteDao.getAllNotes();
            return NoteMapper.toDomainModelList(entities);
        }
        return new ArrayList<>();
    }

    public List<Note> searchNotesByUser(String query) {
        if (noteDao != null) {
            List<NoteEntity> entities = noteDao.searchNotesByUser(query);
            return NoteMapper.toDomainModelList(entities);
        }
        return new ArrayList<>();
    }

    public void addNote(Note note) {
        if (noteDao != null) {
            NoteEntity entity = NoteMapper.toEntity(note);
            noteDao.insertNote(entity);
        }
        notifyNoteAdded(note);
    }

    public void addOnNoteAddedListener(OnNoteAddedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnNoteAddedListener(OnNoteAddedListener listener) {
        listeners.remove(listener);
    }

    private void notifyNoteAdded(Note note) {
        for (OnNoteAddedListener listener : listeners) {
            listener.onNoteAdded(note);
        }
    }
}
