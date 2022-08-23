package com.diplom.starshina.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Adapter.TaskAdapter;
import com.diplom.starshina.Model.DateTask;
import com.diplom.starshina.Model.Soldier;
import com.diplom.starshina.Model.Task;
import com.diplom.starshina.R;
import com.diplom.starshina.SoldierAddActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    ImageButton addSoldierBtn;
    TextView na_lico, viezd, gospital, dejurstvo, naryad, other, soldiersCount, nextWash;
    TextView taskStatus;
    RecyclerView tasksRecyclerView;

    TextView tempCount;

    TaskAdapter taskAdapter;

    List<Task> taskList = new ArrayList<>();

    DatabaseReference soldiersDatabaseReference = FirebaseDatabase.getInstance().getReference("Soldiers");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        // Init
        init(root);

        // Метод подсчета и вывода расхода
        checkRashod();
        // Кнопка перехода на активити добавления нового бойца
        addSoldierBtn.setOnClickListener(view -> {
            Intent addActivityIntent = new Intent(getActivity(), SoldierAddActivity.class);
            startActivity(addActivityIntent);
        });

        // Метод проверки наличия задач на сегодня
        taskCheck();

        // Метод поиска следующей стирки
        findWashDay();

        // Статистика Temp
        checkTemp();


        return root;
    }

    /**
     * ------------------------------ Статистика Temp ------------------------------
     */
    private void checkTemp() {
        DatabaseReference tempReference = FirebaseDatabase.getInstance().getReference("Temporary");
        tempReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempCount.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    /**
     * ------------------------------ Проверка таскЛиста------------------------------
     */
    private void checkTaskList() {
        if (taskList.size() == 0) {
            taskStatus.setVisibility(View.VISIBLE);
        } else {
            taskStatus.setVisibility(View.GONE);
        }
    }

    /**
     * ------------------------------ Поиск следующей стирки ------------------------------
     */
    private void findWashDay() {
        final String DATE_FORMAT = "dd.MM.yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
            nextWash.setText("" + dateFormat.format(calendar.getTime()));
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            calendar.add(Calendar.DATE, 6);
            nextWash.setText("" + dateFormat.format(calendar.getTime()));
        } else {
            calendar.add(Calendar.DATE, 6 - calendar.get(Calendar.DAY_OF_WEEK));
            nextWash.setText("" + dateFormat.format(calendar.getTime()));
        }

    }


    /**
     * ------------------------------ Чек на наличие задач на сегодня ------------------------------
     */
    private void taskCheck() {
        final String DATE_FORMAT = "dd.MM.yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date currentDate = new Date();

        DatabaseReference taskReference = FirebaseDatabase.getInstance().getReference("Tasks");
        taskReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateTaskSnapshot : snapshot.getChildren()) {
                    DateTask dateTask = dateTaskSnapshot.getValue(DateTask.class);
                    if (dateTask.getDate().equals(dateFormat.format(currentDate))) {
                        // Task RV
                        taskList = dateTask.getTasksList();
                        break;
                    } else {
                        taskList.clear();
                    }
                }
                // Проверка taskList
                checkTaskList();
                taskRVInit(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * ------------------------------ Init task RV------------------------------
     */
    private void taskRVInit(List<Task> taskList) {
        taskAdapter = new TaskAdapter(getContext(), taskList);
        tasksRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tasksRecyclerView.setLayoutManager(layoutManager);
        Collections.sort(taskList);
        tasksRecyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();
    }

    /**
     * ------------------------------ Расчет расхода ------------------------------
     */
    private void checkRashod() {
        DatabaseReference rashodReference = FirebaseDatabase.getInstance().getReference("Soldiers");
        rashodReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soldiersCount.setText("" + snapshot.getChildrenCount());
                int na = 0, n = 0, g = 0, d = 0, v = 0, o = 0;
                for (DataSnapshot soldierSnapshot : snapshot.getChildren()) {
                    Soldier soldier = soldierSnapshot.getValue(Soldier.class);
                    switch (soldier.getStatus()) {
                        case "free":
                            na++;
                            break;
                        case "n":
                            n++;
                            break;
                        case "g":
                            g++;
                            break;
                        case "d":
                            d++;
                            break;
                        case "v":
                            v++;
                            break;
                        case "o":
                            o++;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + soldier.getStatus());
                    }
                }
                na_lico.setText(String.valueOf(na) + "/" + String.valueOf(na + n + g + d + v + o));
                naryad.setText(String.valueOf(n));
                gospital.setText(String.valueOf(g));
                dejurstvo.setText(String.valueOf(d));
                viezd.setText(String.valueOf(v));
                other.setText(String.valueOf(o));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * ------------------------------ INIT ------------------------------
     */
    private void init(View root) {
        // Присвоение переменным их View
        na_lico = root.findViewById(R.id.na_lico);
        naryad = root.findViewById(R.id.naryad);
        other = root.findViewById(R.id.other);
        gospital = root.findViewById(R.id.gospital);
        dejurstvo = root.findViewById(R.id.dejurstvo);
        viezd = root.findViewById(R.id.viezd);
        addSoldierBtn = root.findViewById(R.id.addSoldierBtn);
        soldiersCount = root.findViewById(R.id.soldiersCount);
        tasksRecyclerView = root.findViewById(R.id.tasksRecyclerView);
        taskStatus = root.findViewById(R.id.tasksStatus);
        nextWash = root.findViewById(R.id.nextWash);
        tempCount = root.findViewById(R.id.tempCount);

    }
}