package com.diplom.starshina.Fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Adapter.ClothesAdapter;
import com.diplom.starshina.Dialog.LoadingDialog;
import com.diplom.starshina.Model.Clothe;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClothesFragment extends Fragment {

    EditText clothesSearchEd;
    ImageButton addClotheBtn;
    RecyclerView clothesRecyclerView;

    ClothesAdapter clothesAdapter;
    List<Clothe> clothesList;

    LoadingDialog loadingDialog;

    DatabaseReference clothesReference = FirebaseDatabase.getInstance().getReference("Clothes");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clothes, container, false);

        // Init
        init(root);

        // Подгружаем данные в RV
        getDataToRv();

        // Кнопка создания вещи
        addClotheBtn.setOnClickListener(v -> {
            addClothe();
        });

        // Метод для поиска имущества
        searchClotheInit();

        return root;
    }


    /**
     * ------------------------------ Слушатель Edit'a для поиска------------------------------
     */
    private void searchClotheInit() {
        clothesSearchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (clothesSearchEd.getText().toString().length() > 0) {
                    search(editable.toString());
                } else {
                    clothesAdapter.searchedList(clothesList);
                }
            }
        });
    }

    /**
     * ------------------------------ Поиск ------------------------------
     */
    private void search(String s) {
        List<Clothe> searchedClothes = new ArrayList<>();

        for (Clothe clothe : clothesList) {
            if (clothe.getName().toLowerCase().contains(s.toLowerCase())) {
                searchedClothes.add(clothe);
            }
        }
        clothesAdapter.searchedList(searchedClothes);
    }


    /**
     * ------------------------------ Загрузка данных из БД в RV------------------------------
     */
    private void getDataToRv() {
        loadingDialog.startLoadingDialog();
        clothesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothesList.clear();
                for (DataSnapshot clotheSnapshot : snapshot.getChildren()) {
                    Clothe clothe = clotheSnapshot.getValue(Clothe.class);

                    clothesList.add(clothe);
                }
                loadingDialog.dismisDialog();
                clothesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismisDialog();
                Toast.makeText(getContext(), "Ошибка подключения к бд " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * ------------------------------ Диалог добавления имущества ------------------------------
     */
    private void addClothe() {
        // Диалог добавления имущества
        AlertDialog.Builder clotheAddDialogBuilder = new AlertDialog.Builder(getContext());
        View clotheAddView = LayoutInflater.from(getContext()).inflate(R.layout.add_clothe_dialog, null);
        clotheAddDialogBuilder.setView(clotheAddView);
        AlertDialog clotheAddDialog = clotheAddDialogBuilder.create();
        clotheAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        clotheAddDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        clotheAddDialog.show();

        // View диалога
        ImageButton doneBtn = clotheAddView.findViewById(R.id.doneBtn);
        EditText clotheName = clotheAddView.findViewById(R.id.clothe_name);
        EditText clotheCount = clotheAddView.findViewById(R.id.clothe_count);

        // Слушатель кнопки "Готово"
        doneBtn.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();

            // Проверка заполненности полей
            if (clotheName.getText().toString().length() > 1 && clotheCount.getText().toString().length() > 0) {

                String clotheId = clothesReference.push().getKey();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", clotheName.getText().toString());
                hashMap.put("count", Integer.valueOf(clotheCount.getText().toString()));
                hashMap.put("id", clotheId);
                hashMap.put("lastEntry", "");
                hashMap.put("lastEntryDate", "");

                clothesReference.child(clotheId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            loadingDialog.dismisDialog();
                            Toast.makeText(getContext(), "Имущество успешно добавлено", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingDialog.dismisDialog();
                            Toast.makeText(getContext(), "Ошибка БД, не удалось добавить имущество " + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                loadingDialog.dismisDialog();
                Toast.makeText(getContext(), "Заполните все поля верно!", Toast.LENGTH_SHORT).show();
                clotheName.setError("");
                clotheCount.setError("");
            }
        });
    }


    /**
     * ------------------------------ Init ------------------------------
     */
    private void init(View root) {
        // Присвоение переменным их View
        clothesSearchEd = root.findViewById(R.id.clothesSearchEd);
        addClotheBtn = root.findViewById(R.id.addClotheBtn);
        clothesRecyclerView = root.findViewById(R.id.clothesRecyclerView);

        loadingDialog = new LoadingDialog(getContext());


        // Настройка RV
        clothesRecyclerView.setHasFixedSize(true);
        // Создаем LayoutManager в виде LinearLayoutов для нашего RV
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // Применяем для нашего RV созданный LM
        clothesRecyclerView.setLayoutManager(layoutManager);
        // Создаем пустой массив
        clothesList = new ArrayList<>();
        // Создаем экземпляр адаптера с нужными исходными данными
        clothesAdapter = new ClothesAdapter(getContext(), clothesList);
        // Применяем данный адаптер к нашему RV
        clothesRecyclerView.setAdapter(clothesAdapter);
    }
}

