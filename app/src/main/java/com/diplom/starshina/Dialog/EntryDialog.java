package com.diplom.starshina.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Adapter.EntryAdapter;
import com.diplom.starshina.Model.ClotheEntry;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EntryDialog extends AppCompatDialogFragment {

    Context mContext;
    AlertDialog entryDialog;

    TextView emptyTV;
    ImageButton addEntryBtn, deleteEntryBtn;
    RecyclerView entriesRecyclerView;

    EntryAdapter entryAdapter;
    List<ClotheEntry> entrysList = new ArrayList<>();

    String id;

    // Конструктор
    public EntryDialog(Context mContext, String id) {
        this.mContext = mContext;
        this.id = id;
    }

    /**
     * ------------------------------ Метод создания диалога ------------------------------
     */
    public void startDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((mContext));
        View view = LayoutInflater.from(mContext).inflate(R.layout.entry_dialog_layout, null, false);
        dialogBuilder.setView(view);


        entryDialog = dialogBuilder.create();
        entryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        entryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        init(view);
        dataToRv();

        entryDialog.show();


    }

    /**
     * ------------------------------ Метод загрузки данных из БД ------------------------------
     */
    private void dataToRv() {
        DatabaseReference entryReference = FirebaseDatabase.getInstance().getReference("Clothes").child(id).child("Entries");

        entryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entrysList.clear();
                if (snapshot.getChildrenCount() > 0) {
                    emptyTV.setVisibility(View.GONE);
                    for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                        ClotheEntry clotheEntry = entrySnapshot.getValue(ClotheEntry.class);
                        entrysList.add(clotheEntry);
                    }
                    entryAdapter.notifyDataSetChanged();
                } else {
                    emptyTV.setVisibility(View.VISIBLE);
                    entryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Ошибка подключения к БД: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View view) {
        Log.e("CHECK_ENTRY", "Создали адаптеры и вью " + id);
        emptyTV = view.findViewById(R.id.emptyTV);
        addEntryBtn = view.findViewById(R.id.addEntryBtn);
        entriesRecyclerView = view.findViewById(R.id.entriesRecyclerView);
        deleteEntryBtn = view.findViewById(R.id.deleteEntryBtn);

        // Настройка RV
        entriesRecyclerView.setHasFixedSize(true);
        // Создаем LayoutManager в виде LinearLayoutов для нашего RV
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        // Применяем для нашего RV созданный LM
        entriesRecyclerView.setLayoutManager(layoutManager);
        // Создаем пустой массив
        entrysList = new ArrayList<>();
        // Создаем экземпляр адаптера с нужными исходными данными
        entryAdapter = new EntryAdapter(entrysList, mContext, id);
        // Применяем данный адаптер к нашему RV
        entriesRecyclerView.setAdapter(entryAdapter);

        addEntryBtn.setOnClickListener(v -> {
            EntryAddDialog entryAddDialog = new EntryAddDialog(mContext, id);
            entryAddDialog.startDialog();
        });

        deleteEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Билдер диалога удаления
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((mContext));
                view = LayoutInflater.from(mContext).inflate(R.layout.entry_delete_dialog, null, false);
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
                    DatabaseReference entryReference = FirebaseDatabase.getInstance().getReference("Clothes").child(id);
                    entryReference.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isComplete()) {
                                Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
                                deleteDialog.dismiss();
                                entryDialog.dismiss();
                                entryAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                });
            }
        });
    }
}




