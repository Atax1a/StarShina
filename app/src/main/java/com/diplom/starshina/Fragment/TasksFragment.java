package com.diplom.starshina.Fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Adapter.DateTaskAdapter;
import com.diplom.starshina.Dialog.LoadingDialog;
import com.diplom.starshina.Model.DateTask;
import com.diplom.starshina.Model.Task;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    RecyclerView tasksListRecyclerView;
    Button addTaskBtn;

    List<DateTask> dateTaskList;
    DateTaskAdapter dateTaskAdapter;

    DatabaseReference dateTasksReference = FirebaseDatabase.getInstance().getReference("Tasks");

    LoadingDialog loadingDialog;
    AlertDialog taskAddDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_task, container, false);

        // Init
        init(root);

        // Кнопка добавления задачи
        addTaskBtn.setOnClickListener(v -> {
            addDialog();
        });

        // Загрузка данных о задачах
        getTasksData();

        return root;
    }

    /**
     * ------------------------------ Метод открытия диалога добавления задачи ------------------------------
     */
    private void addDialog() {
        // Диалог добавления имущества
        AlertDialog.Builder taskAddDialogBuilder = new AlertDialog.Builder(getContext());
        View taskAddView = LayoutInflater.from(getContext()).inflate(R.layout.add_task_dialog, null);
        taskAddDialogBuilder.setView(taskAddView);
        taskAddDialog = taskAddDialogBuilder.create();
        taskAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        taskAddDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        taskAddDialog.show();

        // View диалога
        ImageButton doneBtn = taskAddView.findViewById(R.id.doneBtn);
        EditText taskName = taskAddView.findViewById(R.id.taskName);
        EditText taskDescription = taskAddView.findViewById(R.id.taskDescription);
        LazyDatePicker datePicker = taskAddView.findViewById(R.id.datePicker);

        final String DATE_FORMAT = "dd.MM.yyyy";
        SimpleDateFormat formatter1 = new SimpleDateFormat(DATE_FORMAT);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskName.getText().length() > 1 && taskDescription.getText().length() > 1 && datePicker.getDate()!=null) {
                    addTask(taskName.getText().toString(), taskDescription.getText().toString(), formatter1.format(datePicker.getDate()));
                } else {
                    Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addTask(String name, String desc, String date) {
        Task task = new Task(name, desc, date, dateTasksReference.push().getKey(), false);


        dateTasksReference.child(date.replace(".", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DateTask dateTask = snapshot.getValue(DateTask.class);
                if (dateTask != null) {
                    List<Task> tasksList = dateTask.getTasksList();
                    tasksList.add(task);
                    dateTasksReference.child(date.replace(".", "")).child("tasksList").setValue(tasksList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> taskFB) {
                            if (taskFB.isComplete()) {
                                Toast.makeText(getContext(), "Успешно добавлено на " + date, Toast.LENGTH_SHORT).show();
                                dateTaskAdapter.notifyDataSetChanged();
                                taskAddDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), taskFB.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                taskAddDialog.dismiss();
                            }
                        }
                    });
                } else {
                    List<Task> tasksList = new ArrayList<>();
                    tasksList.add(task);
                    DateTask dateTaskNew = new DateTask(date, tasksList);
                    dateTasksReference.child(date.replace(".", "")).setValue(dateTaskNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> taskFB) {
                            if (taskFB.isComplete()) {
                                Toast.makeText(getContext(), "Успешно добавлено на " + date, Toast.LENGTH_SHORT).show();
                                dateTaskAdapter.notifyDataSetChanged();
                                taskAddDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), taskFB.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                taskAddDialog.dismiss();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    /**
     * ------------------------------ Метод получения данных о задачах ------------------------------
     */
    private void getTasksData() {
        loadingDialog.startLoadingDialog();
        dateTasksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateTaskList.clear();
                for (DataSnapshot dateTaskSnapshot : snapshot.getChildren()) {
                    DateTask dateTask = dateTaskSnapshot.getValue(DateTask.class);
                    dateTaskList.add(dateTask);
                }
                dateTaskAdapter.notifyDataSetChanged();
                loadingDialog.dismisDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismisDialog();
                Toast.makeText(getContext(), "Ошибка подключения к бд: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ------------------------------ INIT ------------------------------
     */
    private void init(View root) {
        // Присвоение переменным их View
        tasksListRecyclerView = root.findViewById(R.id.tasksListRecyclerView);
        addTaskBtn = root.findViewById(R.id.addTaskBtn);
        loadingDialog = new LoadingDialog(getContext());

        dateTaskList = new ArrayList<>();

        tasksListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tasksListRecyclerView.setLayoutManager(layoutManager);
        dateTaskAdapter = new DateTaskAdapter(getContext(), dateTaskList);
        tasksListRecyclerView.setAdapter(dateTaskAdapter);

    }
}