package com.diplom.starshina.Model;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Temporary implements Comparable<Temporary> {
    private String id, soldierName, clotheName, date;

    public Temporary() {
    }

    public Temporary(String id, String soldierName, String clotheName, String date) {
        this.id = id;
        this.soldierName = soldierName;
        this.clotheName = clotheName;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getSoldierName() {
        return soldierName;
    }

    public String getClotheName() {
        return clotheName;
    }

    public String getDate() {
        return date;
    }



    @Override
    public int compareTo(Temporary temporary) {
        final String DATE_FORMAT = "dd.MM.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        return dateFormat.parse(getDate(), new ParsePosition(0)).compareTo(dateFormat.parse(temporary.getDate(), new ParsePosition(0)));
    }
}
