package com.example.lab5_1;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

/**
 * Controller class in MVC Pattern acting as mediator between Views (AddNoteActivity, MainActivity, BrowseNoteActivity)
 * and Models (User, Note, TextNote, CheckListNote, NoteRepository, Room Database).
 */
public class NoteController {

    private static NoteController instance;
    private final NoteRepository repository;

    private NoteController(Context context) {
        this.repository = NoteRepository.getInstance(context);
    }

    public static synchronized NoteController getInstance(Context context) {
        if (instance == null) {
            instance = new NoteController(context);
        } else if (context != null) {
            instance.repository.initDatabase(context);
        }
        return instance;
    }

    public static synchronized NoteController getInstance() {
        if (instance == null) {
            instance = new NoteController(null);
        }
        return instance;
    }

    /**
     * Gets all notes from Room Database Model.
     */
    public List<Note> getAllNotes() {
        return repository.getNotes();
    }

    /**
     * Searches notes by user name from Room Database Model.
     */
    public List<Note> searchNotesByUser(String query) {
        return repository.searchNotesByUser(query);
    }

    /**
     * Creates User and TextNote models and adds to Room Database.
     * @return true if successful, false if input validation fails.
     */
    public boolean createAndAddTextNote(String userName, String userEmail, String title, String content) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(title)) {
            return false;
        }

        // Create Model instances
        User user = new User(userName, userEmail);
        TextNote textNote = new TextNote(title, user, content != null ? content : "");

        // Delegate persistence to Room Repository Model
        repository.addNote(textNote);
        return true;
    }

    /**
     * Creates User and CheckListNote models and adds to Room Database.
     * @return true if successful, false if input validation fails.
     */
    public boolean createAndAddCheckListNote(String userName, String userEmail, String title, List<CheckListItem> items) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(title)) {
            return false;
        }

        // Create Model instances
        User user = new User(userName, userEmail);
        CheckListNote checkListNote = new CheckListNote(title, user, items);

        // Delegate persistence to Room Repository Model
        repository.addNote(checkListNote);
        return true;
    }

    /**
     * Register Event Listener for Model updates.
     */
    public void registerNoteAddedListener(OnNoteAddedListener listener) {
        repository.addOnNoteAddedListener(listener);
    }

    /**
     * Unregister Event Listener.
     */
    public void unregisterNoteAddedListener(OnNoteAddedListener listener) {
        repository.removeOnNoteAddedListener(listener);
    }
}
