package com.vl.tome.notes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tome on 17.2.2016..
 */
public class Note implements Serializable {
    private long id;
    private String ime;//trenutno ne koristim
    private String tekst;
    private Date datumCreated;
    private Calendar alarm; //prazan == null

    Note() {

    }
    Note(String ime, String tekst) {
        this.id=id;
        this.ime = ime;
        this.tekst = tekst;
    }
    Note(long id,String ime, String tekst) {
        this.id=id;
        this.ime = ime;
        this.tekst = tekst;
    }
    Note(long id,String ime, String tekst,Date datumcreated) {
        this.id=id;
        this.ime = ime;
        this.tekst = tekst;
        this.datumCreated=datumcreated;
    }
    Note(long id,String ime, String tekst,Date datumcreated,Calendar calendar) {
        this.id=id;
        this.ime = ime;
        this.tekst = tekst;
        this.datumCreated=datumcreated;
        this.alarm=calendar;
    }

    public Calendar getAlarm() {
        return alarm;
    }

    public void setAlarm(Calendar alarm) {
        this.alarm = alarm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public Date getDatumCreated() {
        return datumCreated;
    }

    public void setDatumCreated(Date datumCreated) {
        this.datumCreated = datumCreated;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

}
