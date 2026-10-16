package com.example.lab5_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * View layer in MVC Pattern with Multitasking (Thread) for navigating to BrowseNoteActivity.
 */
public class MainActivity extends AppCompatActivity implements OnNoteAddedListener {

    private RecyclerView rvNotes;
    private TextView tvEmptyState;
    private Button btnAddNote, btnBrowseNotes;
    private LinearLayout llMainLoadingContainer;

    private NoteAdapter adapter;
    private NoteController noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Controller with Room Database context
        noteController = NoteController.getInstance(this);

        initViews();
        setupRecyclerView();

        // Register event listener via Controller
        noteController.registerNoteAddedListener(this);

        // Click Event: Open AddNoteActivity
        btnAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        // Section 2: Multitasking Click Event with Thread, 2s Delay, and ProgressBar
        btnBrowseNotes.setOnClickListener(v -> loadBrowseNotesWithThread());
    }

    private void initViews() {
        rvNotes = findViewById(R.id.rvNotes);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnAddNote = findViewById(R.id.btn_add_note);
        btnBrowseNotes = findViewById(R.id.btnBrowseNotes);
        llMainLoadingContainer = findViewById(R.id.llMainLoadingContainer);
    }

    private void setupRecyclerView() {
        List<Note> notesList = noteController.getAllNotes();
        adapter = new NoteAdapter(this, notesList);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setAdapter(adapter);
        updateEmptyState();
    }

    /**
     * Section 2 Multitasking:
     * Starts background Thread, displays ProgressBar, delays 2 seconds, then opens BrowseNoteActivity.
     */
    private void loadBrowseNotesWithThread() {
        llMainLoadingContainer.setVisibility(View.VISIBLE);
        btnBrowseNotes.setEnabled(false);

        new Thread(() -> {
            try {
                // Delay 2 seconds as specified in requirement
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Update UI & Navigate on main thread
            runOnUiThread(() -> {
                llMainLoadingContainer.setVisibility(View.GONE);
                btnBrowseNotes.setEnabled(true);

                Intent intent = new Intent(MainActivity.this, BrowseNoteActivity.class);
                startActivity(intent);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNotesList();
    }

    private void refreshNotesList() {
        if (adapter != null) {
            List<Note> notes = noteController.getAllNotes();
            adapter.updateNotes(notes);
            updateEmptyState();
        }
    }

    private void updateEmptyState() {
        if (adapter != null && adapter.getItemCount() > 0) {
            rvNotes.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            rvNotes.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoteAdded(Note note) {
        refreshNotesList();
        Toast.makeText(this, "MVC Event: New Note added (" + note.getTitle() + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteController.unregisterNoteAddedListener(this);
    }
}
