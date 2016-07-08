package com.vl.tome.notes;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import layout.NoteWidget;


public class MainActivity extends AppCompatActivity {
    //notes s alarmom/podsjetnikom i widget,omogućite dijeljenje/share informacija iz notesa

    private NotesAdapter adapter;
    private List<Note> listNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Bundle extras = getIntent().getExtras();

        ////////napravit listu note, i na klik se otvara svaka zaa edit. tipka nazad je sprema u bazu.

        runOnUiThread(new Runnable() {
            public void run() {
                DB db = DB.getInstance(MainActivity.this);
                //db.test();
                listNote = db.getAllNotes();
                final ListView lv = (ListView) findViewById(R.id.listView);//preko baze
                adapter = new NotesAdapter(MainActivity.this, (ArrayList<Note>) listNote);
                lv.setAdapter(adapter);
                System.out.println("MainActivity create");
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        Object o = lv.getItemAtPosition(position);
                        Note n = (Note) o;
                        //System.out.println(n.getIme());
                        if (extras != null && extras.getBoolean("widget") == true) {//Widget
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                            int mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                            System.out.println("main activiti widget id: " + mAppWidgetId);
                            Toast.makeText(MainActivity.this, "widget!!!", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = getSharedPreferences("note", MODE_PRIVATE).edit();
                            editor.putString("noteTekst", n.getTekst());
                            editor.commit();

                            NoteWidget.updateAppWidget(MainActivity.this, appWidgetManager, mAppWidgetId);
                            MainActivity.this.finish();

                        } else {
                            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                            Bundle b = new Bundle();
                            //b.putSerializable("note", n); //rjesit preko int ID i baze, tako brze radi
                            b.putLong("id", n.getId());
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity resume");
        runOnUiThread(new Runnable() {
            public void run() {
                DB db = DB.getInstance(MainActivity.this);
                //db.test();
                listNote = db.getAllNotes();
                final ListView lv = (ListView) findViewById(R.id.listView);
               adapter =new NotesAdapter(MainActivity.this, (ArrayList<Note>) listNote);
                lv.setAdapter(adapter);
               //adapter.notifyDataSetChanged();
            }
        });
    }

    ///////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            System.out.println("action settings");
            return true;
        } else if (id == R.id.action_add) {
            System.out.println("action add");
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
