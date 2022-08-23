package com.diplom.starshina.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Adapter.SoldiersAdapter;
import com.diplom.starshina.Dialog.LoadingDialog;
import com.diplom.starshina.Model.Soldier;
import com.diplom.starshina.R;
import com.diplom.starshina.SoldierAddActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SoldiersFragment extends Fragment {

    ImageButton addSoldierBtn;
    TextView na_lico, viezd, gospital, dejurstvo, naryad, other;
    RecyclerView soldiersRecyclerView;
    EditText soldierSearchEd;

    List<Soldier> soldiersList = new ArrayList<>();
    SoldiersAdapter soldiersAdapter;

    LoadingDialog loadingDialog;

    DatabaseReference soldiersDatabaseReference = FirebaseDatabase.getInstance().getReference("Soldiers");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_soldiers, container, false);

        // Init
        init(root);

        // Обновление списка бойцов в RV
        dataToRv();

        //Метод для добавления бойцов для примера
        //addDummies();

        // Метод подсчета и вывода расхода
        checkRashod();

        // Метод для поиска
        searchSoldiersInit();

        // Кнопка перехода на активити добавления нового бойца
        addSoldierBtn.setOnClickListener(view -> {
            Intent addActivityIntent = new Intent(getActivity(), SoldierAddActivity.class);
            startActivity(addActivityIntent);
        });

        return root;
    }


    /** ------------------------------ Слушатель для поиска ------------------------------ */
    private void searchSoldiersInit() {
        soldierSearchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (soldierSearchEd.getText().toString().length() > 0) {
                    search(editable.toString());
                }else{
                    soldiersAdapter.searchedList(soldiersList);
                }
            }
        });
    }

    /** ------------------------------ Поиск ------------------------------ */
    private void search(String s) {
        List<Soldier> searchedSoldiers = new ArrayList<>();

        for (Soldier soldier : soldiersList) {
            if (soldier.getName().toLowerCase().contains(s.toLowerCase()) ||
                    soldier.getSurname().toLowerCase().contains(s.toLowerCase()) ||
                    soldier.getLastname().toLowerCase().contains(s.toLowerCase())) {
                searchedSoldiers.add(soldier);
            }
        }
        soldiersAdapter.searchedList(searchedSoldiers);
    }


    /** ------------------------------ Расчет расхода ------------------------------ */
    private void checkRashod() {
        DatabaseReference rashodReference = FirebaseDatabase.getInstance().getReference("Soldiers");
        rashodReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
     * ------------------------------ Метод добавления бойцов для примера ------------------------------
     */
    private void addDummies() {
        DatabaseReference soldierDatabaseReference = FirebaseDatabase.getInstance().getReference("Soldiers");
        String soldierKey = soldierDatabaseReference.push().getKey();
        String soldierKey1 = soldierDatabaseReference.push().getKey();
        String soldierKey2 = soldierDatabaseReference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "Дамир");
        hashMap.put("surname", "Алтынбаев");
        hashMap.put("lastname", "Рустемович");
        hashMap.put("dmb", "05.07.2022");
        hashMap.put("vzvod", "АВ");
        hashMap.put("zvanie", 3);
        hashMap.put("gospital", "");
        hashMap.put("status", "free");
        hashMap.put("id", soldierKey);
        soldierDatabaseReference.child(soldierKey).setValue(hashMap);

    }


    /**
     * ------------------------------ Загрузка данных из FB в RV ------------------------------
     */
    private void dataToRv() {
        loadingDialog.startLoadingDialog();
        soldiersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot soldiersSnapshot) {
                soldiersList.clear();
                for (DataSnapshot soldierSnapshot : soldiersSnapshot.getChildren()) {

                    Soldier soldier = soldierSnapshot.getValue(Soldier.class);

                    soldiersList.add(soldier);
                }
                soldiersAdapter.notifyDataSetChanged();
                loadingDialog.dismisDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismisDialog();
                Toast.makeText(getContext(), "Ошибка загрузки данных из БД: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * ------------------------------ Init ------------------------------
     */
    private void init(View root) {
        // Присвоение переменным их View
        na_lico = root.findViewById(R.id.na_lico);
        naryad = root.findViewById(R.id.naryad);
        other = root.findViewById(R.id.other);
        gospital = root.findViewById(R.id.gospital);
        dejurstvo = root.findViewById(R.id.dejurstvo);
        viezd = root.findViewById(R.id.viezd);
        soldiersRecyclerView = root.findViewById(R.id.soldiersRecyclerView);
        addSoldierBtn = root.findViewById(R.id.addSoldierBtn);
        soldierSearchEd = root.findViewById(R.id.soldierSearchEd);

        loadingDialog = new LoadingDialog(getContext());


        // Настройка RV
        soldiersRecyclerView.setHasFixedSize(true);
        // Создаем LayoutManager в виде LinearLayoutов для нашего RV
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // Применяем для нашего RV созданный LM
        soldiersRecyclerView.setLayoutManager(layoutManager);
        // Создаем пустой массив
        soldiersList = new ArrayList<>();
        // Создаем экземпляр адаптера с нужными исходными данными
        soldiersAdapter = new SoldiersAdapter(getContext(), soldiersList);
        // Применяем данный адаптер к нашему RV
        soldiersRecyclerView.setAdapter(soldiersAdapter);
    }
}