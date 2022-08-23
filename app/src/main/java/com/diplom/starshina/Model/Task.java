package com.diplom.starshina.Model;

public class Task implements Comparable<Task>{
    String taskName;
    String taskDescription;
    String taskDate;
    String id;
    boolean taskStatus;

    public Task(String taskName, String taskDescription, String taskDate, String id, boolean taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
        this.id = id;
        this.taskStatus = taskStatus;
    }

    public Task() {
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getId() {
        return id;
    }

    public boolean isTaskStatus() {
        return taskStatus;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public int compareTo(Task task) {
        if(task!=null){
            return String.valueOf(isTaskStatus()).compareTo(String.valueOf(task.isTaskStatus()));
        }else{
            return 1;
        }

    }
}
