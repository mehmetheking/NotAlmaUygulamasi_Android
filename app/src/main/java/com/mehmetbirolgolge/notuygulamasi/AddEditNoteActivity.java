package com.mehmetbirolgolge.notuygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mehmetbirolgolge.notuygulamasi.helpers.AIHelper;
import com.mehmetbirolgolge.notuygulamasi.model.Note;
import com.mehmetbirolgolge.notuygulamasi.database.NoteDatabase;

import java.util.Date;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.mehmetbirolgolge.notuygulamasi.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.mehmetbirolgolge.notuygulamasi.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.mehmetbirolgolge.notuygulamasi.EXTRA_CONTENT";
    public static final String EXTRA_PROCESSED_CONTENT = "com.mehmetbirolgolge.notuygulamasi.EXTRA_PROCESSED_CONTENT";
    public static final String EXTRA_PROCESSED_BY_AI = "com.mehmetbirolgolge.notuygulamasi.EXTRA_PROCESSED_BY_AI";

    private EditText editTextTitle;
    private EditText editTextContent;
    private CheckBox checkBoxAI;
    private Button buttonPreview;
    private Button buttonSave;

    private boolean isEditing = false;
    private boolean isPreviewActive = false;
    private String originalContent;
    private NoteDatabase noteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        // Veritabanı başlat
        noteDatabase = new NoteDatabase(this);

        // UI elemanlarını bul
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.edit_text_content);
        checkBoxAI = findViewById(R.id.checkbox_ai);
        buttonPreview = findViewById(R.id.button_preview);
        buttonSave = findViewById(R.id.button_save);

        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);

        // Düzenleme durumunda mı yoksa yeni not oluşturma durumunda mıyız?
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Notu Düzenle");
            isEditing = true;
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextContent.setText(intent.getStringExtra(EXTRA_CONTENT));
            checkBoxAI.setChecked(intent.getBooleanExtra(EXTRA_PROCESSED_BY_AI, false));
        } else {
            setTitle("Yeni Not");
            isEditing = false;
        }

        // CheckBox durumu değiştiğinde buttonPreview'ı etkinleştir/devre dışı bırak
        checkBoxAI.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonPreview.setEnabled(isChecked && editTextContent.getText().toString().trim().length() > 0);
            if (!isChecked && isPreviewActive) {
                // CheckBox işareti kaldırıldığında, önizleme aktifse orijinal içeriğe geri dön
                resetPreview();
            }
        });

        // EditText değiştiğinde buttonPreview'ı kontrol et
        editTextContent.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                buttonPreview.setEnabled(checkBoxAI.isChecked() && s.toString().trim().length() > 0);
                if (isPreviewActive) {
                    // İçerik değişirse önizlemeyi sıfırla
                    resetPreview();
                }
            }
        });

        // Önizleme butonu
        buttonPreview.setOnClickListener(v -> {
            if (isPreviewActive) {
                // Önizleme modundan çık
                resetPreview();
            } else {
                // Önizleme moduna gir
                showPreview();
            }
        });

        // Kaydetme butonu
        buttonSave.setOnClickListener(v -> saveNote());
    }

    private void resetPreview() {
        isPreviewActive = false;
        buttonPreview.setText("AI Önizleme");
        if (originalContent != null) {
            editTextContent.setText(originalContent);
            originalContent = null;
        }
    }

    private void showPreview() {
        String content = editTextContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Not içeriği boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        originalContent = content;

        // Başlık boşsa otomatik oluştur
        if (editTextTitle.getText().toString().trim().isEmpty()) {
            String title = AIHelper.generateTitle(content);
            editTextTitle.setText(title);
        }

        // İçeriği işle
        String processedContent = AIHelper.processContent(content);
        editTextContent.setText(processedContent);

        isPreviewActive = true;
        buttonPreview.setText("Orijinale Dön");
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = isPreviewActive ? originalContent : editTextContent.getText().toString().trim();
        String processedContent = isPreviewActive ? editTextContent.getText().toString().trim() : null;

        if (title.isEmpty()) {
            // Başlık yoksa otomatik oluştur
            title = AIHelper.generateTitle(content);
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Not içeriği boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        // AI işlemesi için checkbox işaretli ama önizleme aktif değilse işle
        if (checkBoxAI.isChecked() && !isPreviewActive) {
            processedContent = AIHelper.processContent(content);
        }

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setProcessedContent(processedContent);
        note.setProcessedByAI(checkBoxAI.isChecked());

        if (isEditing) {
            // Not güncelleniyor
            int id = getIntent().getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Not güncellenemedi", Toast.LENGTH_SHORT).show();
                return;
            }

            note.setId(id);
            note.setModificationDate(new Date());

            int result = noteDatabase.updateNote(note);
            if (result > 0) {
                Toast.makeText(this, "Not güncellendi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not güncellenemedi", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Yeni not ekleniyor
            note.setCreationDate(new Date());

            long id = noteDatabase.addNote(note);
            if (id > 0) {
                Toast.makeText(this, "Not kaydedildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not kaydedilemedi", Toast.LENGTH_SHORT).show();
            }
        }

        setResult(RESULT_OK);
        finish();
    }
}