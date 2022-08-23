package com.diplom.starshina.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Model.Soldier;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import info.hoang8f.android.segmented.SegmentedGroup;

public class SoldiersAdapter extends RecyclerView.Adapter<SoldiersAdapter.ViewHolder> {
    Context mContext;
    List<Soldier> soldiersList;
    String status = "free";

    // Конструктор адаптера
    public SoldiersAdapter(Context mContext, List<Soldier> soldiersList) {
        this.mContext = mContext;
        this.soldiersList = soldiersList;
    }

    // Создание view для адаптера
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.soldier_item_layout, parent, false);
        return new SoldiersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Soldier soldier = soldiersList.get(position);

        // Прямые выводы
        holder.s_name.setText(soldier.getName());
        holder.s_surname.setText(soldier.getSurname());
        holder.s_lastname.setText(soldier.getLastname());
        holder.s_vzvod.setText(soldier.getVzvod());


        // Звание
        if (soldier.getZvanie() == 1) {
            holder.zvanie_1.setVisibility(View.INVISIBLE);
            holder.zvanie_2.setVisibility(View.INVISIBLE);
        }
        if (soldier.getZvanie() == 2) {
            holder.zvanie_1.setVisibility(View.VISIBLE);
            holder.zvanie_2.setVisibility(View.INVISIBLE);
        }
        if (soldier.getZvanie() == 3) {
            holder.zvanie_1.setVisibility(View.VISIBLE);
            holder.zvanie_2.setVisibility(View.VISIBLE);
        }

        // Дата дмб
        holder.dmb_date.setText(soldier.getDmb());
        // Метод расчета остатка ДДД
        checkDmbLeft(holder.dmb_left, soldier.getDmb());

        // Слушатель долгого нажатия на бойца для редактирования
        holder.soldierItemLayout.setOnLongClickListener(view -> {
            showDialog(mContext, soldier.getId());
            return false;
        });

        // Слушатель статуса бойца
        switch (soldier.getStatus()) {
            case "free":
                holder.status_icon.setImageResource(R.drawable.status_green_round);
                holder.s_status.setText("Свободен");
                break;
            case "n":
                holder.status_icon.setImageResource(R.drawable.status_red_round);
                holder.s_status.setText("В наряде");
                break;
            case "g":
                holder.status_icon.setImageResource(R.drawable.status_red_round);
                // Вывод, если госпиталь
                if (soldier.getGospital().length() > 1) {

                    final String DATE_FORMAT = "dd.MM.yyyy";

                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    Date gospitalDate = new Date();
                    Date currentDate = new Date();
                    try {
                        gospitalDate = dateFormat.parse(soldier.getGospital());
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    long diffInMillies = Math.abs((currentDate.getTime()) - gospitalDate.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    holder.s_status.setText("В госпитале " + diff + "д, " + "c " + soldier.getGospital());
                    holder.status_icon.setImageResource(R.drawable.status_red_round);
                } else {
                    holder.s_status.setText("В госпитале, дата не указана");
                }
                break;
            case "d":
                holder.status_icon.setImageResource(R.drawable.status_yellow_round);
                holder.s_status.setText("Дежурство");
                break;
            case "v":
                holder.status_icon.setImageResource(R.drawable.status_yellow_round);
                holder.s_status.setText("Выезд");
                break;
            case "o":
                holder.status_icon.setImageResource(R.drawable.status_yellow_round);
                holder.s_status.setText("Неизвестно");
                break;
            default:

        }
    }

    /**
     * ------------------------------ Метод вывода диалога настройки бойца ------------------------------
     */
    private void showDialog(Context mContext, String id) {
        // Создание диалога
        AlertDialog.Builder soldierSettingsDialogBuilder = new AlertDialog.Builder(mContext);
        View settingsDialogView = LayoutInflater.from(mContext).inflate(R.layout.soldier_settings_dialog_layout, null);
        soldierSettingsDialogBuilder.setView(settingsDialogView);
        AlertDialog soldiersSettingsDialog = soldierSettingsDialogBuilder.create();
        soldiersSettingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        soldiersSettingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        soldiersSettingsDialog.show();

        // View Диалога
        Button naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn;
        ImageButton editBtn, deleteBtn, doneBtn;

        naLicoBtn = settingsDialogView.findViewById(R.id.naLicoBtn);
        naryadBtn = settingsDialogView.findViewById(R.id.naryadBtn);
        gospitalBtn = settingsDialogView.findViewById(R.id.gospitalBtn);
        dejurstvoBtn = settingsDialogView.findViewById(R.id.dejurstvoBtn);
        viezdBtn = settingsDialogView.findViewById(R.id.viezdBtn);
        otherBtn = settingsDialogView.findViewById(R.id.otherBtn);
        editBtn = settingsDialogView.findViewById(R.id.editBtn);
        deleteBtn = settingsDialogView.findViewById(R.id.deleteBtn);
        doneBtn = settingsDialogView.findViewById(R.id.doneBtn);

        // Слушатели нажатия для смены цвета выбранной кнопки
        naLicoBtn.setOnClickListener(view -> {
            status = "free";
            setAllGray(naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn);
            naLicoBtn.setBackgroundResource(R.drawable.button_ripple_back_round_green_10);
        });
        naryadBtn.setOnClickListener(view -> {
            status = "n";
            setAllGray(naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn);
            naryadBtn.setBackgroundResource(R.drawable.button_ripple_back_round_green_10);
        });
        gospitalBtn.setOnClickListener(view -> {
            status = "g";
            showGospitalDialog(id);
            setAllGray(naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn);
            gospitalBtn.setBackgroundResource(R.drawable.button_ripple_back_round_green_10);
        });
        dejurstvoBtn.setOnClickListener(view -> {
            status = "d";
            setAllGray(naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn);
            dejurstvoBtn.setBackgroundResource(R.drawable.button_ripple_back_round_green_10);
        });
        viezdBtn.setOnClickListener(view -> {
            status = "v";
            setAllGray(naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn);
            viezdBtn.setBackgroundResource(R.drawable.button_ripple_back_round_green_10);
        });
        otherBtn.setOnClickListener(view -> {
            status = "o";
            setAllGray(naLicoBtn, naryadBtn, gospitalBtn, dejurstvoBtn, viezdBtn, otherBtn);
            otherBtn.setBackgroundResource(R.drawable.button_ripple_back_round_green_10);
        });

        // Слушатель кнопки 'Готово'
        doneBtn.setOnClickListener(v -> {
            DatabaseReference soldierStatusReference = FirebaseDatabase.getInstance().getReference("Soldiers").child(id).child("status");
            soldierStatusReference.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        Toast.makeText(mContext, "Успешно", Toast.LENGTH_SHORT).show();
                        soldiersSettingsDialog.dismiss();
                    }
                }
            });
        });

        // Слушатель кнопки удаления бойца
        deleteBtn.setOnClickListener(view -> {
            DatabaseReference soldierReference = FirebaseDatabase.getInstance().getReference("Soldiers").child(id);
            soldierReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Ошибка удаления бойца: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            soldiersSettingsDialog.dismiss();
        });

        editBtn.setOnClickListener(view -> {

            showEditDialog(id);

        });


    }


    /**
     * ------------------------------ Метод диалога для госпиталя------------------------------
     */
    private void showGospitalDialog(String id) {
        // Создание диалога
        AlertDialog.Builder gospitalDialogBuilder = new AlertDialog.Builder(mContext);
        View gospitalDialogView = LayoutInflater.from(mContext).inflate(R.layout.soldier_gospital_dialog, null);
        gospitalDialogBuilder.setView(gospitalDialogView);
        AlertDialog gospitalDialog = gospitalDialogBuilder.create();
        gospitalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gospitalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gospitalDialog.show();

        ImageButton doneBtn;
        LazyDatePicker gospital_picker;

        doneBtn = gospitalDialogView.findViewById(R.id.doneBtn);
        gospital_picker = gospitalDialogView.findViewById(R.id.gospital_picker);

        final String DATE_FORMAT = "dd.MM.yyyy";
        SimpleDateFormat formatter1 = new SimpleDateFormat(DATE_FORMAT);

        DatabaseReference gospitalSoldierReference = FirebaseDatabase.getInstance().getReference("Soldiers").child(id);

        doneBtn.setOnClickListener(view -> {
            if (gospital_picker.getDate() != null) {
                gospitalSoldierReference.child("gospital").setValue(LazyDatePicker.dateToString(gospital_picker.getDate(), DATE_FORMAT));
                gospitalDialog.dismiss();
            }
        });
    }


    /**
     * ------------------------------ Диалог редактирования бойца ------------------------------
     */
    private void showEditDialog(String id) {
        AlertDialog.Builder editBuilder = new AlertDialog.Builder(mContext);
        View editDialogView = LayoutInflater.from(mContext).inflate(R.layout.soldier_edit_dialog, null);
        editBuilder.setView(editDialogView);
        AlertDialog editDialog = editBuilder.create();
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.show();

        // Переменные
        Button addBtn;
        EditText a_name, a_surname, a_lastname;
        LazyDatePicker dmbPicker;
        SegmentedGroup vzvod_group, zvanie_group;
        RadioButton v_av, v_aer, v_el, v_other;
        RadioButton z_ryad, z_efr, z_ml;

        final String DATE_FORMAT = "dd.MM.yyyy";
        SimpleDateFormat formatter1 = new SimpleDateFormat(DATE_FORMAT);

        addBtn = editDialogView.findViewById(R.id.addBtn);
        a_name = editDialogView.findViewById(R.id.a_name);
        a_surname = editDialogView.findViewById(R.id.a_surname);
        a_lastname = editDialogView.findViewById(R.id.a_lastname);
        dmbPicker = editDialogView.findViewById(R.id.dmb_picker);
        vzvod_group = editDialogView.findViewById(R.id.vzvod_group);
        zvanie_group = editDialogView.findViewById(R.id.zvanie_group);

        v_av = editDialogView.findViewById(R.id.v_av);
        v_aer = editDialogView.findViewById(R.id.v_aer);
        v_el = editDialogView.findViewById(R.id.v_el);
        v_other = editDialogView.findViewById(R.id.v_other);

        z_ryad = editDialogView.findViewById(R.id.z_ryad);
        z_efr = editDialogView.findViewById(R.id.z_efr);
        z_ml = editDialogView.findViewById(R.id.z_ml);

        setOldData(a_name, a_surname, a_lastname, dmbPicker, formatter1, v_aer, v_el, v_av, v_other, z_efr, z_ml, z_ryad, id);

        addBtn.setOnClickListener(v -> {
            if (checkFields(a_name, a_surname, a_lastname, dmbPicker, formatter1, v_aer, v_el, v_av, z_efr, z_ml, z_ryad)) {
                addSoldier(id, editDialog, zvanie_group, vzvod_group, a_name, a_lastname, a_surname, dmbPicker, DATE_FORMAT);
            }

        });

    }

    /**
     * ------------------------------ Добавление бойца ------------------------------
     */
    private void addSoldier(String id, AlertDialog editDialog, SegmentedGroup zvanie_group, SegmentedGroup vzvod_group, EditText a_name, EditText a_lastname, EditText a_surname, LazyDatePicker dmbPicker, String date_format) {
        String t_name, t_surname, t_lastname, t_dmb, t_vzvod;
        int t_zvanie;

        t_name = a_name.getText().toString();
        t_surname = a_surname.getText().toString();
        t_lastname = a_lastname.getText().toString();

        t_dmb = LazyDatePicker.dateToString(dmbPicker.getDate(), date_format);

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
        DatabaseReference soldierDatabaseReference = FirebaseDatabase.getInstance().getReference("Soldiers").child(id);

        soldierDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Soldier soldier = snapshot.getValue(Soldier.class);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", t_name);
                hashMap.put("surname", t_surname);
                hashMap.put("lastname", t_lastname);
                hashMap.put("dmb", t_dmb);
                hashMap.put("vzvod", t_vzvod);
                hashMap.put("zvanie", t_zvanie);
                hashMap.put("gospital", soldier.getGospital());
                hashMap.put("status", soldier.getStatus());
                hashMap.put("id", id);
                soldierDatabaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Toast.makeText(mContext, "Успешно", Toast.LENGTH_SHORT).show();
                            editDialog.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Ошибка подключения к базе данных: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * ------------------------------ Проверка полей ------------------------------
     */
    private boolean checkFields(EditText a_name, EditText a_surname, EditText a_lastname, LazyDatePicker dmbPicker, SimpleDateFormat formatter1, RadioButton v_aer, RadioButton v_el, RadioButton v_av, RadioButton z_efr, RadioButton z_ml, RadioButton z_ryad) {
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
                Toast.makeText(mContext, "Заполните корректно дату призыва", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }


    /**
     * ------------------------------ Метод вывода старых данных бойца для редактирования ------------------------------
     */
    private void setOldData(EditText a_name, EditText a_surname, EditText a_lastname, LazyDatePicker dmbPicker, SimpleDateFormat formatter1, RadioButton v_aer, RadioButton v_el, RadioButton v_other, RadioButton v_av, RadioButton z_efr, RadioButton z_ml, RadioButton z_ryad, String id) {
        DatabaseReference editSoldierReference = FirebaseDatabase.getInstance().getReference("Soldiers").child(id);
        editSoldierReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Soldier soldier = snapshot.getValue(Soldier.class);

                a_name.setText(soldier.getName());
                a_surname.setText(soldier.getSurname());
                a_lastname.setText(soldier.getLastname());

                try {
                    dmbPicker.setDate(formatter1.parse(soldier.getDmb()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                switch (soldier.getVzvod()) {
                    case "АЭР":
                        v_aer.setChecked(true);
                        break;
                    case "ЭЛ":
                        v_el.setChecked(true);
                        break;
                    case "-":
                        v_other.setChecked(true);
                        break;
                    case "АВ":
                    default:
                        v_av.setChecked(true);

                }

                switch (soldier.getZvanie()) {
                    case 2:
                        z_efr.setChecked(true);
                        break;
                    case 3:
                        z_ml.setChecked(true);
                        break;
                    case 1:
                    default:
                        z_ryad.setChecked(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Ошибка подключения к БД для редактирования бойца: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * ------------------------------ Метод переключения цвета кнопок диалога в серый ------------------------------
     */
    private void setAllGray(Button naLicoBtn, Button naryadBtn, Button gospitalBtn, Button dejurstvoBtn, Button viezdBtn, Button otherBtn) {
        naLicoBtn.setBackgroundResource(R.drawable.button_ripple_back_round_gray_10);
        naryadBtn.setBackgroundResource(R.drawable.button_ripple_back_round_gray_10);
        gospitalBtn.setBackgroundResource(R.drawable.button_ripple_back_round_gray_10);
        dejurstvoBtn.setBackgroundResource(R.drawable.button_ripple_back_round_gray_10);
        viezdBtn.setBackgroundResource(R.drawable.button_ripple_back_round_gray_10);
        otherBtn.setBackgroundResource(R.drawable.button_ripple_back_round_gray_10);
    }


    /**
     * ------------------------------ Метод расчета ДДД ------------------------------
     */
    private void checkDmbLeft(TextView dmb_left, String dmb) {

        final String DATE_FORMAT = "dd.MM.yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date dmbDate = new Date();
        Date currentDate = new Date();
        try {
            dmbDate = dateFormat.parse(dmb);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        long diffInMillies = Math.abs((dmbDate.getTime() + 31556952000L) - currentDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        dmb_left.setText(String.valueOf(diff) + " ддд");
    }

    @Override
    public int getItemCount() {
        return soldiersList.size();
    }

    /**
     * ------------------------------ Применение листа поиска ------------------------------
     */
    public void searchedList(List<Soldier> searchedList) {
        soldiersList = searchedList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView s_surname, s_name, s_lastname, s_vzvod, s_status, dmb_date, dmb_left;
        RelativeLayout zvanie_1, zvanie_2, soldierItemLayout;
        ImageView status_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            s_surname = itemView.findViewById(R.id.s_surname);
            s_name = itemView.findViewById(R.id.s_name);
            s_lastname = itemView.findViewById(R.id.s_lastname);
            s_vzvod = itemView.findViewById(R.id.s_vzvod);
            s_status = itemView.findViewById(R.id.s_status);
            dmb_date = itemView.findViewById(R.id.dmb_date);
            dmb_left = itemView.findViewById(R.id.dmb_left);
            zvanie_1 = itemView.findViewById(R.id.zvanie_1);
            zvanie_2 = itemView.findViewById(R.id.zvanie_2);
            status_icon = itemView.findViewById(R.id.status_icon);
            soldierItemLayout = itemView.findViewById(R.id.soldierItemLayout);

            zvanie_1.setVisibility(View.INVISIBLE);
            zvanie_2.setVisibility(View.INVISIBLE);
        }
    }

}
