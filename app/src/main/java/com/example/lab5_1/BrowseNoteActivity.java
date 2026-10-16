package com.example.lab5_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/**
 * BrowseNoteActivity implementing Room Database & Multitasking (Thread) search operations.
 */
public class BrowseNoteActivity extends AppCompatActivity {

    private TextInputEditText etSearchUser;
    private Button btnSearchUser;
    private LinearLayout llSearchLoadingContainer;
    private TextView tvSearchResultStatus;
    private RecyclerView rvSearchResults;

    private NoteController noteController;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_note);

        noteController = NoteController.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etSearchUser = findViewById(R.id.etSearchUser);
        btnSearchUser = findViewById(R.id.btnSearchUser);
        llSearchLoadingContainer = findViewById(R.id.llSearchLoadingContainer);
        tvSearchResultStatus = findViewById(R.id.tvSearchResultStatus);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnSearchUser.setOnClickListener(v -> performMultitaskingSearch());
    }

    /**
     * Multitasking search using Thread & Room Database.
     * Displays ProgressBar, delays 2 seconds, and displays search results or "ไม่พบข้อมูล".
     */
    private void performMultitaskingSearch() {
        String searchQuery = etSearchUser.getText() != null ? etSearchUser.getText().toString().trim() : "";

        // Hide result view and show ProgressBar
        llSearchLoadingContainer.setVisibility(View.VISIBLE);
        tvSearchResultStatus.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        btnSearchUser.setEnabled(false);

        // Multitasking via Background Thread querying Room Database
        new Thread(() -> {
            try {
                // Delay 2 seconds as specified in requirement
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final List<Note> results;
            if (!TextUtils.isEmpty(searchQuery)) {
                results = noteController.searchNotesByUser(searchQuery);
            } else {
                results = null;
            }

            // Update UI on main thread
            runOnUiThread(() -> {
                llSearchLoadingContainer.setVisibility(View.GONE);
                btnSearchUser.setEnabled(true);

                if (results != null && !results.isEmpty()) {
                    if (adapter == null) {
                        adapter = new NoteAdapter(BrowseNoteActivity.this, results);
                        rvSearchResults.setAdapter(adapter);
                    } else {
                        adapter.updateNotes(results);
                    }
                    rvSearchResults.setVisibility(View.VISIBLE);
                    tvSearchResultStatus.setVisibility(View.GONE);
                } else {
                    tvSearchResultStatus.setVisibility(View.VISIBLE);
                    tvSearchResultStatus.setText(getString(R.string.msg_not_found));
                }
            });
        }).start();
    }
}
