package com.diplom.starshina.Model;

public class Soldier {
    String name, surname, lastname, status, vzvod, dmb, id, gospital;
    int zvanie;

    public Soldier() {

    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getStatus() {
        return status;
    }

    public String getVzvod() {
        return vzvod;
    }

    public String getDmb() {
        return dmb;
    }

    public String getId() {
        return id;
    }

    public String getGospital() {
        return gospital;
    }

    public int getZvanie() {
        return zvanie;
    }

    public Soldier(String name, String surname, String lastname, String status, String vzvod, String dmb, String id, String gospital, int zvanie) {
        this.name = name;
        this.surname = surname;
        this.lastname = lastname;
        this.status = status;
        this.vzvod = vzvod;
        this.dmb = dmb;
        this.id = id;
        this.gospital = gospital;
        this.zvanie = zvanie;
    }
}
