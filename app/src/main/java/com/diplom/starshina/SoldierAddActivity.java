package com.diplom.starshina;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.diplom.starshina.Dialog.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.util.Date;
import java.util.HashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

public class SoldierAddActivity extends AppCompatActivity {

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    Button addBtn;
    EditText a_name, a_surname, a_lastname;
    LazyDatePicker dmbPicker;
    SegmentedGroup vzvod_group, zvanie_group;
    String t_name, t_surname, t_lastname, t_dmb, t_vzvod;
    int t_zvanie;

    DatabaseReference soldierDatabaseReference;

    LoadingDialog loadingDialog = new LoadingDialog(SoldierAddActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldier_add);

        init();

        datePickerInit();


        // Слушатель кнопки добавления бойца
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkFields()) {
                    getData();
                    addSoldier();
                }

            }
        });
    }


    // Метод добавления бойца в БД
    private void addSoldier() {
        loadingDialog.startLoadingDialog();


        String soldierKey = soldierDatabaseReference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", t_name);
        hashMap.put("surname", t_surname);
        hashMap.put("lastname", t_lastname);
        hashMap.put("dmb", t_dmb);
        hashMap.put("vzvod", t_vzvod);
        hashMap.put("zvanie", t_zvanie);
        hashMap.put("gospital", "");
        hashMap.put("status", "free");
        hashMap.put("id", soldierKey);
        soldierDatabaseReference.child("Soldiers").child(soldierKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(SoldierAddActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismisDialog();
                    finish();
                }
            }
        });


    }


    // Получение данных из полей
    private void getData() {
        t_name = a_name.getText().toString();
        t_surname = a_surname.getText().toString();
        t_lastname = a_lastname.getText().toString();

        t_dmb = LazyDatePicker.dateToString(dmbPicker.getDate(), DATE_FORMAT);

        int vzvod_id = vzvod_group.getCheckedRadioButtonId();
        switch (vzvod_id) {
            case R.id.v_av:
                t_vzvod = "АВ";
                break;
            case R.id.v_aer:
                t_vzvod = "АЭР";
                break;
            case R.id.v_el:
                t_vzvod = "ЭЛ";
                break;
            case R.id.v_other:
                t_vzvod = "-";
                break;
            default:
                t_vzvod = "АВ";
        }

        int zvanie_id = zvanie_group.getCheckedRadioButtonId();
        switch (zvanie_id) {
            case R.id.z_ryad:
                t_zvanie = 1;
                break;
            case R.id.z_efr:
                t_zvanie = 2;
                break;
            case R.id.z_ml:
                t_zvanie = 3;
                break;
            default:
                t_zvanie = 1;
        }
    }


    // Настройка DatePicker'a
    private void datePickerInit() {
        Date minDate = LazyDatePicker.stringToDate("01.01.2021", DATE_FORMAT);
        dmbPicker.setMinDate(minDate);
    }


    // Проверка полей добавления бойца
    private boolean checkFields() {
        if (a_name.getText().length() < 2 || a_surname.getText().length() < 2
                || a_lastname.getText().length() < 2 || dmbPicker.getDate() == null) {

            if (a_name.getText().length() < 2) {
                a_name.setError("Заполните имя");
            }
            if (a_surname.getText().length() < 2) {
                a_surname.setError("Заполните фамилию");
            }
            if (a_lastname.getText().length() < 2) {
                a_lastname.setError("Заполните отчество");
            }
            if (dmbPicker.getDate() == null) {
                Toast.makeText(this, "Заполните корректно дату призыва", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private void init() {

        addBtn = findViewById(R.id.addBtn);
        a_name = findViewById(R.id.a_name);
        a_surname = findViewById(R.id.a_surname);
        a_lastname = findViewById(R.id.a_lastname);
        dmbPicker = findViewById(R.id.dmb_picker);
        vzvod_group = findViewById(R.id.vzvod_group);
        zvanie_group = findViewById(R.id.zvanie_group);

        soldierDatabaseReference = FirebaseDatabase.getInstance().getReference();


    }
}