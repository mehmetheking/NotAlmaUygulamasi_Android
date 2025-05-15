package com.mehmetbirolgolge.notuygulamasi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mehmetbirolgolge.notuygulamasi.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_PROCESSED_CONTENT = "processed_content";
    private static final String COLUMN_CREATION_DATE = "creation_date";
    private static final String COLUMN_MODIFICATION_DATE = "modification_date";
    private static final String COLUMN_PROCESSED_BY_AI = "processed_by_ai";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public NoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_PROCESSED_CONTENT + " TEXT,"
                + COLUMN_CREATION_DATE + " TEXT,"
                + COLUMN_MODIFICATION_DATE + " TEXT,"
                + COLUMN_PROCESSED_BY_AI + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Tüm notları getir
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_CREATION_DATE + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                note.setProcessedContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROCESSED_CONTENT)));

                try {
                    String creationDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
                    Date creationDate = dateFormat.parse(creationDateStr);
                    note.setCreationDate(creationDate);

                    String modificationDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODIFICATION_DATE));
                    if (modificationDateStr != null) {
                        Date modificationDate = dateFormat.parse(modificationDateStr);
                        note.setModificationDate(modificationDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                note.setProcessedByAI(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROCESSED_BY_AI)) == 1);

                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

    // Not ekle
    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_PROCESSED_CONTENT, note.getProcessedContent());
        values.put(COLUMN_CREATION_DATE, dateFormat.format(note.getCreationDate()));

        if (note.getModificationDate() != null) {
            values.put(COLUMN_MODIFICATION_DATE, dateFormat.format(note.getModificationDate()));
        }

        values.put(COLUMN_PROCESSED_BY_AI, note.isProcessedByAI() ? 1 : 0);

        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    // Not güncelle
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_PROCESSED_CONTENT, note.getProcessedContent());

        if (note.getModificationDate() != null) {
            values.put(COLUMN_MODIFICATION_DATE, dateFormat.format(note.getModificationDate()));
        }

        values.put(COLUMN_PROCESSED_BY_AI, note.isProcessedByAI() ? 1 : 0);

        int rowsAffected = db.update(TABLE_NOTES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
        return rowsAffected;
    }

    // Not sil
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ID'ye göre not getir
    public Note getNoteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_PROCESSED_CONTENT,
                        COLUMN_CREATION_DATE, COLUMN_MODIFICATION_DATE, COLUMN_PROCESSED_BY_AI},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        Note note = null;
        if (cursor != null && cursor.moveToFirst()) {
            note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            note.setProcessedContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROCESSED_CONTENT)));

            try {
                String creationDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
                Date creationDate = dateFormat.parse(creationDateStr);
                note.setCreationDate(creationDate);

                String modificationDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODIFICATION_DATE));
                if (modificationDateStr != null) {
                    Date modificationDate = dateFormat.parse(modificationDateStr);
                    note.setModificationDate(modificationDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            note.setProcessedByAI(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROCESSED_BY_AI)) == 1);

            cursor.close();
        }

        db.close();
        return note;
    }

    // Notları ara
    public List<Note> searchNotes(String query) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NOTES +
                " WHERE " + COLUMN_TITLE + " LIKE '%" + query + "%'" +
                " OR " + COLUMN_CONTENT + " LIKE '%" + query + "%'" +
                " ORDER BY " + COLUMN_CREATION_DATE + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                note.setProcessedContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROCESSED_CONTENT)));

                try {
                    String creationDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
                    Date creationDate = dateFormat.parse(creationDateStr);
                    note.setCreationDate(creationDate);

                    String modificationDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODIFICATION_DATE));
                    if (modificationDateStr != null) {
                        Date modificationDate = dateFormat.parse(modificationDateStr);
                        note.setModificationDate(modificationDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                note.setProcessedByAI(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROCESSED_BY_AI)) == 1);

                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }
}