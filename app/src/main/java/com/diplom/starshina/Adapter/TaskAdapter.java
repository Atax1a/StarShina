package com.diplom.starshina.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Model.DateTask;
import com.diplom.starshina.Model.Task;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    Context mContext;
    List<Task> tasksList;
    DatabaseReference taskReference = FirebaseDatabase.getInstance().getReference("Tasks");

    public TaskAdapter() {
    }

    public TaskAdapter(Context mContext, List<Task> tasksList) {
        this.mContext = mContext;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasksList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskDescription.setText(task.getTaskDescription());
        if(task.isTaskStatus() != holder.taskStatusSwitch.isChecked()){
            holder.taskStatusSwitch.setChecked(task.isTaskStatus());
        }



        holder.tempItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteDialog(view, task, mContext);
                return false;
            }
        });

        // Слушатель для ТВ выполнения
        if (task.isTaskStatus()) {
            holder.taskStatus.setText("Выполнено");
            holder.taskStatus.setTextColor(mContext.getResources().getColor(R.color.accent_color));
            holder.tempItemLayout.setBackgroundResource(R.drawable.panel_task_done);
        } else {
            holder.taskStatus.setText("Не выполнено");
            holder.taskStatus.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.tempItemLayout.setBackgroundResource(R.drawable.panel_task_notdone);
        }

        // Кнопка выполнения
        holder.taskStatusSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(task.isTaskStatus()!=isChecked){
                    changeFBTaskStatus(isChecked, task, holder);
                }

                changeTaskStatus(isChecked, holder);

            }
        });

    }

    private void changeFBTaskStatus(boolean isChecked, Task task, ViewHolder holder) {
        taskReference.child(task.getTaskDate().replace(".", "")).child("tasksList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;

                for (DataSnapshot tasksSnapshot : snapshot.getChildren()) {
                    Task taskFromSnapshot = tasksSnapshot.getValue(Task.class);
                    if (task.getId() == (taskFromSnapshot.getId())) {
                        taskReference.child(task.getTaskDate().replace(".", "")).child("tasksList").child(String.valueOf(i)).child("taskStatus").setValue(isChecked);
                        notifyDataSetChanged();
                        break;
                    }
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    /**
     * ------------------------------ Изменения статуса таска ------------------------------
     */
    private void changeTaskStatus(boolean isChecked, ViewHolder holder) {
        if (isChecked) {
            holder.taskStatus.setText("Выполнено");
            holder.taskStatus.setTextColor(mContext.getResources().getColor(R.color.accent_color));
            holder.tempItemLayout.setBackgroundResource(R.drawable.panel_task_done);
        } else {
            holder.taskStatus.setText("Не выполнено");
            holder.taskStatus.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.tempItemLayout.setBackgroundResource(R.drawable.panel_task_notdone);
        }
    }

    /**
     * ------------------------------ Диалог удаления задачи ------------------------------
     */
    private void deleteDialog(View view, Task task, Context mContext) {
        // Билдер диалога удаления
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((this.mContext));
        view = LayoutInflater.from(this.mContext).inflate(R.layout.task_delete_dialog, null, false);
        dialogBuilder.setView(view);
        AlertDialog deleteDialog = dialogBuilder.create();
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

        // View диалога
        ImageButton noBtn, yesBtn;
        noBtn = view.findViewById(R.id.noBtn);
        yesBtn = view.findViewById(R.id.yesBtn);

        // Слушатель кнопки Нет
        noBtn.setOnClickListener(v -> {
            deleteDialog.dismiss();
        });

        // Слушатель кнопки Да
        yesBtn.setOnClickListener(v -> {
            DatabaseReference taskReference = FirebaseDatabase.getInstance().getReference("Tasks")
                    .child(task.getTaskDate().replace(".", ""));

            // Одиночный слушатель изменения таблицы Тасков
            taskReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    List<Task> taskListFromSnapshot = new ArrayList<>();

                    DateTask dateTaskFromSnapshot = snapshot.getValue(DateTask.class);

                    taskListFromSnapshot = dateTaskFromSnapshot.getTasksList();
                    // Если задача на дату последняя - удалить всю дату
                    if (taskListFromSnapshot.size() < 2) {
                        taskReference.getRef().removeValue();
                        Toast.makeText(TaskAdapter.this.mContext, "Успешно!", Toast.LENGTH_SHORT).show();
                        deleteDialog.dismiss();
                        notifyDataSetChanged();
                        // Либо найти ее в выгруженном листе, удалить, и новый лист загрузить
                    } else {

                        int i = 0;

                        for (Task taskFromList : taskListFromSnapshot) {

                            if (taskFromList.getId().equals(task.getId())) {
                                taskListFromSnapshot.remove(i);
                                break;
                            }
                            i++;
                        }

                        taskReference.child("tasksList").setValue(taskListFromSnapshot).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                if (task.isComplete()) {
                                    Toast.makeText(TaskAdapter.this.mContext, "Успешно!", Toast.LENGTH_SHORT).show();
                                    deleteDialog.dismiss();
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDescription, taskStatus;
        SwitchButton taskStatusSwitch;
        RelativeLayout tempItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            taskStatusSwitch = itemView.findViewById(R.id.taskStatusSwitch);
            tempItemLayout = itemView.findViewById(R.id.tempItemLayout);
        }
    }
}
