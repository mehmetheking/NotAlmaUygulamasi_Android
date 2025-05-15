package com.mehmetbirolgolge.notuygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mehmetbirolgolge.notuygulamasi.adapter.NoteAdapter;
import com.mehmetbirolgolge.notuygulamasi.database.NoteDatabase;
import com.mehmetbirolgolge.notuygulamasi.model.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteDatabase noteDatabase;
    private NoteAdapter adapter;

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonShowAll;
    private FloatingActionButton fabAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Veritabanını başlat
        noteDatabase = new NoteDatabase(this);

        // UI elemanlarını bul
        recyclerView = findViewById(R.id.recycler_view);
        textViewEmpty = findViewById(R.id.text_view_empty);
        editTextSearch = findViewById(R.id.edit_text_search);
        buttonSearch = findViewById(R.id.button_search);
        buttonShowAll = findViewById(R.id.button_show_all);
        fabAddNote = findViewById(R.id.fab_add_note);

        // RecyclerView ayarla
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Adaptörü ayarla
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        // Yeni not ekleme
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            startActivityForResult(intent, 1);
        });

        // Not üzerine tıklama
        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
            intent.putExtra(AddEditNoteActivity.EXTRA_CONTENT, note.getContent());
            intent.putExtra(AddEditNoteActivity.EXTRA_PROCESSED_CONTENT, note.getProcessedContent());
            intent.putExtra(AddEditNoteActivity.EXTRA_PROCESSED_BY_AI, note.isProcessedByAI());
            startActivityForResult(intent, 2);
        });

        // Arama düğmesi
        buttonSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                List<Note> searchResults = noteDatabase.searchNotes(query);
                adapter.setNotes(searchResults);
                updateUI(searchResults.isEmpty());
                buttonShowAll.setVisibility(View.VISIBLE);
            } else {
                refreshNotes();
                buttonShowAll.setVisibility(View.GONE);
            }
        });

        // Tümünü göster düğmesi
        buttonShowAll.setOnClickListener(v -> {
            editTextSearch.setText("");
            refreshNotes();
            buttonShowAll.setVisibility(View.GONE);
        });

        // İlk yükleme
        refreshNotes();
    }

    private void refreshNotes() {
        List<Note> notes = noteDatabase.getAllNotes();
        adapter.setNotes(notes);
        updateUI(notes.isEmpty());
    }

    private void updateUI(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            refreshNotes();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNotes();
    }
}