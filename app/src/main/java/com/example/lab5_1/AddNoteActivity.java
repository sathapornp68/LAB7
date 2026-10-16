package com.example.lab5_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * View layer in MVC Pattern for adding new notes.
 * Delegates creation and persistence to NoteController.
 */
public class AddNoteActivity extends AppCompatActivity {

    private TextInputEditText etUserName, etUserEmail;
    private TextInputEditText etNoteTitle, etTextNoteContent, etCheckListItemInput;
    private RadioGroup rgNoteType;
    private RadioButton rbTextNote, rbCheckListNote;
    private LinearLayout llTextNoteContainer, llCheckListContainer, llCheckListItems;
    private Button btnAddCheckListItem, btnSaveNote, btnCancel;

    private NoteController noteController;
    private final List<CheckListItem> pendingCheckListItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Initialize Controller with Context
        noteController = NoteController.getInstance(this);

        initViews();
        setupEventListeners();
    }

    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etTextNoteContent = findViewById(R.id.etTextNoteContent);
        etCheckListItemInput = findViewById(R.id.etCheckListItemInput);

        rgNoteType = findViewById(R.id.rgNoteType);
        rbTextNote = findViewById(R.id.rbTextNote);
        rbCheckListNote = findViewById(R.id.rbCheckListNote);

        llTextNoteContainer = findViewById(R.id.llTextNoteContainer);
        llCheckListContainer = findViewById(R.id.llCheckListContainer);
        llCheckListItems = findViewById(R.id.llCheckListItems);

        btnAddCheckListItem = findViewById(R.id.btnAddCheckListItem);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupEventListeners() {
        // RadioGroup selection change event toggling forms
        rgNoteType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTextNote) {
                llTextNoteContainer.setVisibility(View.VISIBLE);
                llCheckListContainer.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbCheckListNote) {
                llTextNoteContainer.setVisibility(View.GONE);
                llCheckListContainer.setVisibility(View.VISIBLE);
            }
        });

        btnAddCheckListItem.setOnClickListener(v -> addCheckListItem());
        btnSaveNote.setOnClickListener(v -> saveNote());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void addCheckListItem() {
        String itemText = etCheckListItemInput.getText() != null ? etCheckListItemInput.getText().toString().trim() : "";
        if (TextUtils.isEmpty(itemText)) {
            Toast.makeText(this, "กรุณากรอกข้อความรายการเช็คลิสต์", Toast.LENGTH_SHORT).show();
            return;
        }

        CheckListItem newItem = new CheckListItem(itemText, false);
        pendingCheckListItems.add(newItem);

        // Add view row dynamically
        View rowView = LayoutInflater.from(this).inflate(R.layout.item_checklist_input, llCheckListItems, false);
        CheckBox cbItemCheck = rowView.findViewById(R.id.cbItemCheck);
        TextView tvItemText = rowView.findViewById(R.id.tvItemText);
        ImageButton btnRemoveItem = rowView.findViewById(R.id.btnRemoveItem);

        tvItemText.setText(newItem.getText());
        cbItemCheck.setChecked(newItem.isChecked());

        cbItemCheck.setOnCheckedChangeListener((buttonView, isChecked) -> newItem.setChecked(isChecked));

        btnRemoveItem.setOnClickListener(v -> {
            pendingCheckListItems.remove(newItem);
            llCheckListItems.removeView(rowView);
        });

        llCheckListItems.addView(rowView);
        etCheckListItemInput.setText("");
    }

    private void saveNote() {
        String userName = etUserName.getText() != null ? etUserName.getText().toString().trim() : "";
        String userEmail = etUserEmail.getText() != null ? etUserEmail.getText().toString().trim() : "";
        String title = etNoteTitle.getText() != null ? etNoteTitle.getText().toString().trim() : "";

        boolean success;

        // Delegate Model creation & persistence to Controller (MVC Pattern)
        if (rbTextNote.isChecked()) {
            String content = etTextNoteContent.getText() != null ? etTextNoteContent.getText().toString().trim() : "";
            success = noteController.createAndAddTextNote(userName, userEmail, title, content);
        } else {
            success = noteController.createAndAddCheckListNote(userName, userEmail, title, new ArrayList<>(pendingCheckListItems));
        }

        if (!success) {
            Toast.makeText(this, getString(R.string.msg_input_required), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, getString(R.string.msg_note_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
