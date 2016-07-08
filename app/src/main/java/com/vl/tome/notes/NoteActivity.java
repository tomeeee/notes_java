package com.vl.tome.notes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Tome on 18.2.2016..
 */
public class NoteActivity extends AppCompatActivity {
    private Bundle extras;
    private EditText tekst;
    private Note note;
    private boolean novaNote = false;
    private boolean deleteNote = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNote);
        setSupportActionBar(toolbar);
        tekst = (EditText) findViewById(R.id.noteEditText);
        extras = getIntent().getExtras();

        if (extras != null) {  //i za alarm
            note = DB.getInstance(this).getNote(extras.getLong("id"));
            //System.out.println(note.getIme());
            // System.out.println(note.getAlarm());
            tekst.setText(note.getTekst());
            novaNote = false;
            if (extras.getBoolean("alarm") == true) {
                note.setAlarm(null);
                DB.getInstance(this).updateNoteAlarm(note);
                Toast.makeText(NoteActivity.this, "ALARM!!!", Toast.LENGTH_LONG).show();
            }
        } else {
            note = new Note();
            novaNote = true;
        }

        //alarm

        final EditText timeEdit = (EditText) findViewById(R.id.timeEditText);
        // timeEdit.setShowSoftInputOnFocus(false);
        final SetTime time = new SetTime(timeEdit, this);
        final EditText dateEdit = (EditText) findViewById(R.id.dateEditText);
        //dateEdit.setShowSoftInputOnFocus(false);
        final SetDate date = new SetDate(dateEdit, this);

        if (note.getAlarm() != null) {
            Calendar notecalendar = note.getAlarm();
            timeEdit.setText(notecalendar.get(Calendar.HOUR_OF_DAY) + ":" + notecalendar.get(Calendar.MINUTE));
            dateEdit.setText(notecalendar.get(Calendar.DAY_OF_MONTH) + "." + notecalendar.get(Calendar.MONTH) + "." + notecalendar.get(Calendar.YEAR));
        }

        Button buttonAlarmOn = (Button) findViewById(R.id.action_alarm_add);
        buttonAlarmOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("butun pritisnut add alarm");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(date.mYear, date.mMonth, date.mDay, time.hour, time.minute);
                System.out.println(calendar.getTime().toString());
                timeEdit.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                dateEdit.setText(calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR));
                setAlarm(calendar);
            }
        });
        Button buttonAlarmOff = (Button) findViewById(R.id.action_alarm_cancel);
        buttonAlarmOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("NoteActivity destroy");

    }

    @Override
    protected void onPause() {//spremi tekst
        super.onPause();
        System.out.println("NoteActivity pause");
        // System.out.println(tekst.getText().toString());

        if (deleteNote == false && tekst.getText().toString().length() > 0) {
            if (novaNote == false && !note.getTekst().equals(tekst.getText().toString())) {
                note.setTekst(tekst.getText().toString());
                DB.getInstance(this).updateNote(note);
            } else if (novaNote == true) {
                note.setTekst(tekst.getText().toString());
                DB.getInstance(this).addNote(note);
            }
        } else if (deleteNote == true && novaNote == false) {
            cancelAlarm();
            DB.getInstance(this).deleteNote(note);
        }
    }

    private void setAlarm(Calendar calendar) {//dodat da ga spremi u bazu i kasnije izbrise
        System.out.println("Alarm postavljen");
        Toast.makeText(NoteActivity.this, "Alarm postavljen", Toast.LENGTH_LONG).show();
        note.setAlarm(calendar);
        if (novaNote == false) {
            DB.getInstance(this).updateNoteAlarm(note);
        } else if (novaNote == true) {
            note.setTekst(tekst.getText().toString());
            note.setId(DB.getInstance(this).addNote(note));
            novaNote = false;
        }
        Intent intent = new Intent(NoteActivity.this, NoteActivity.class);
        intent.putExtra("id", note.getId());
        intent.putExtra("alarm", true);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(NoteActivity.this, (int) note.getId(), intent, 0);

        int alarmType = AlarmManager.RTC_WAKEUP;//ELAPSED_REALTIME;
        //final int FIFTEEN_SEC_MILLIS = 15000;SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS

        AlarmManager alarmManager = (AlarmManager) NoteActivity.this.getSystemService(NoteActivity.this.ALARM_SERVICE);

        alarmManager.setExact(alarmType, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        if (note.getAlarm() != null && novaNote == false) {
            Toast.makeText(NoteActivity.this, "Alarm ugasen", Toast.LENGTH_LONG).show();
            note.setAlarm(null);
            DB.getInstance(this).updateNoteAlarm(note);
            Intent intent = new Intent(NoteActivity.this, NoteActivity.class);
            intent.putExtra("id", note.getId());
            intent.putExtra("alarm", true);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(NoteActivity.this, (int) note.getId(), intent, 0);
            AlarmManager alarmManager = (AlarmManager) NoteActivity.this.getSystemService(NoteActivity.this.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            System.out.println("Alarm cancel");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_delete) {
            System.out.println("action delete");
            deleteNote = true;
            this.finish();
            return true;
        } else if (id == R.id.action_share) {
            System.out.println("action share");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, note.getTekst());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "posalji :"));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}

class SetTime implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;
    public int hour;
    public int minute;

    public SetTime(EditText editText, Context ctx) {
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.ctx = ctx;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            this.hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            this.minute = myCalendar.get(Calendar.MINUTE);
            new TimePickerDialog(ctx, this, hour, minute, true).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        this.hour = hourOfDay;
        this.minute = minute;
        this.editText.setText(hourOfDay + ":" + minute);
    }

}

class SetDate implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;
    public int mYear;
    public int mMonth;
    public int mDay;

    public SetDate(EditText editText, Context ctx) {
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.ctx = ctx;

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            this.mYear = myCalendar.get(Calendar.YEAR);
            this.mMonth = myCalendar.get(Calendar.MONTH);
            this.mDay = myCalendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(ctx, this, mYear, mMonth, mDay).show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {

        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
        this.editText.setText(mDay + "." + mMonth + "." + mYear);
    }

}
