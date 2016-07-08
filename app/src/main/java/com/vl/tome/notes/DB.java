package com.vl.tome.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tome on 18.2.2016..
 */
public class DB extends SQLiteOpenHelper {

    private static DB Instance;

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "notes";

    private static final String TABLE_NAME = "note";

    private static final String KEY_ID = "id";
    private static final String KEY_IME = "ime";
    private static final String KEY_TEKST = "tekst";
    private static final String KEY_DATUM_CREATED = "datumCreated";
    private static final String KEY_DATUM_ALARM = "datumAlarm";
    public static synchronized DB getInstance(Context context) {
        if (Instance == null) {
            Instance = new DB(context.getApplicationContext());
        }
        return Instance;
    }

    private DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void test() {

        Note note;
        for (int i = 0; i < 5; i++) {
            note = new Note("nota " + i, "tekst blablabablalblabl");
            this.addNote(note);
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IME + " TEXT,"
                + KEY_TEKST + " TEXT," + KEY_DATUM_CREATED + " INTEGER," + KEY_DATUM_ALARM + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IME, note.getIme());
        values.put(KEY_TEKST, note.getTekst());
        values.put(KEY_DATUM_CREATED, System.currentTimeMillis());
        if (note.getAlarm() != null) {
            values.put(KEY_DATUM_ALARM, note.getAlarm().getTimeInMillis());
        }
       return db.insert(TABLE_NAME, null, values);
        //db.close();
    }

    public Note getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID,
                KEY_IME, KEY_TEKST, KEY_DATUM_CREATED, KEY_DATUM_ALARM}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2), new Date(Long.parseLong(cursor.getString(3))));
        if (cursor.getString(4) != null) {
            Calendar novicalendar = Calendar.getInstance();
            novicalendar.setTimeInMillis(Long.parseLong(cursor.getString(4)));
            note.setAlarm(novicalendar);
        }
        return note;
    }


    public List<Note> getAllNotes() {
        List<Note> listNotes = new ArrayList<Note>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID,
                KEY_IME, KEY_TEKST, KEY_DATUM_CREATED,KEY_DATUM_ALARM}, null, null, null, null, KEY_DATUM_CREATED+" DESC",null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setIme(cursor.getString(1));
                note.setTekst(cursor.getString(2));
                note.setDatumCreated(new Date(Long.parseLong(cursor.getString(3))));
                if (cursor.getString(4) != null) {
                    Calendar novicalendar = Calendar.getInstance();
                    novicalendar.setTimeInMillis(Long.parseLong(cursor.getString(4)));
                    note.setAlarm(novicalendar);
                }
                listNotes.add(note);
            } while (cursor.moveToNext());
        }
        return listNotes;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IME, note.getIme());
        values.put(KEY_TEKST, note.getTekst());
        values.put(KEY_DATUM_CREATED,System.currentTimeMillis());
        return db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(note.getId())});
    }

    public int updateNoteAlarm(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(note.getAlarm()==null){
            values.putNull(KEY_DATUM_ALARM);
        }else{
            values.put(KEY_DATUM_ALARM,note.getAlarm().getTimeInMillis());
        }
        return db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();
    }
}
