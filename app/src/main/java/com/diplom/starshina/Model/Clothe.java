package com.diplom.starshina.Model;

public class Clothe {
    String id;
    String name;
    String lastEntry;
    String lastEntryDate;
    int count;

    public Clothe() {
    }

    public Clothe(String id, String name, String lastEntry, String lastEntryDate, int count) {
        this.id = id;
        this.name = name;
        this.lastEntry = lastEntry;
        this.lastEntryDate = lastEntryDate;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastEntry() {
        return lastEntry;
    }

    public String getLastEntryDate() {
        return lastEntryDate;
    }

    public int getCount() {
        return count;
    }
}
