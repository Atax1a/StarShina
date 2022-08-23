package com.diplom.starshina.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Model.DateTask;
import com.diplom.starshina.Model.Task;
import com.diplom.starshina.R;

import java.util.Collections;
import java.util.List;

public class DateTaskAdapter extends RecyclerView.Adapter<DateTaskAdapter.ViewHolder> {
    Context mContext;
    List<DateTask> dateTaskList;

    public DateTaskAdapter() {
    }

    public DateTaskAdapter(Context mContext, List<DateTask> dateTaskList) {
        this.mContext = mContext;
        this.dateTaskList = dateTaskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.date_tasks_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateTask dateTask = dateTaskList.get(position);

        holder.tasksDateTitle.setText(dateTask.getDate());

        holder.tasksRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.tasksRecyclerView.setLayoutManager(layoutManager);
        List<Task> tasksList = dateTask.getTasksList();
        Collections.sort(tasksList);

        TaskAdapter taskAdapter = new TaskAdapter(mContext, tasksList);
        holder.tasksRecyclerView.setAdapter(taskAdapter);
    }

    @Override
    public int getItemCount() {
        return dateTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tasksDateTitle;
        RecyclerView tasksRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tasksDateTitle = itemView.findViewById(R.id.tasksDateTitle);
            tasksRecyclerView = itemView.findViewById(R.id.tasksRecyclerView);
        }
    }
}
