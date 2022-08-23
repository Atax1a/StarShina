package com.diplom.starshina.Model;

public class ClotheEntry {
    String name, date, number, id, income, outcome, count;

    public ClotheEntry(String name, String date, String number, String id, String income, String outcome, String count) {
        this.name = name;
        this.date = date;
        this.number = number;
        this.id = id;
        this.income = income;
        this.outcome = outcome;
        this.count = count;
    }

    public ClotheEntry() {
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public String getIncome() {
        return income;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getCount() {
        return count;
    }
}
