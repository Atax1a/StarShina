package com.diplom.starshina.Model;

import java.util.List;

public class DateTask {

    String date;
    List<Task> tasksList;

    public DateTask() {
    }


    public DateTask(String date, List<Task> tasksList) {
        this.date = date;
        this.tasksList = tasksList;
    }

    public String getDate() {
        return date;
    }

    public List<Task> getTasksList() {
        return tasksList;
    }
}
