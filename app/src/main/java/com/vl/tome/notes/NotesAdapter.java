package com.vl.tome.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<Note> {
    public NotesAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
        }

        TextView ime = (TextView) convertView.findViewById(R.id.noteIme);
        TextView tekst = (TextView) convertView.findViewById(R.id.noteTekst);
        ImageView slikaAlarm=(ImageView) convertView.findViewById(R.id.noteImageAlarm);
        if(note.getAlarm()==null){
            slikaAlarm.setVisibility(View.INVISIBLE);
            //System.out.println("invisible");
        }
        else {
           // System.out.println("visible");
            slikaAlarm.setVisibility(View.VISIBLE);
        }
        SimpleDateFormat ft = new SimpleDateFormat("MM.dd.yyyy - HH:mm ");
        ime.setText("| ID: " + note.getId() + " | Datum:" + ft.format(note.getDatumCreated()));
        tekst.setText(note.getTekst());

        return convertView;
    }
}
