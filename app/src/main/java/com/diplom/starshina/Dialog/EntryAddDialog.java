package com.diplom.starshina.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.diplom.starshina.Model.Clothe;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.util.HashMap;


public class EntryAddDialog extends AppCompatDialogFragment {

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    Context mContext;
    AlertDialog addEntryDialog;

    EditText entryName, entryNumber, income, outcome, count;
    LazyDatePicker entryPicker;

    ImageButton doneBtn;

    String id;


    public EntryAddDialog(Context mContext, String id) {
        this.mContext = mContext;
        this.id = id;
    }

    public void startDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((mContext));
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_entry_dialog, null, false);
        dialogBuilder.setView(view);

        addEntryDialog = dialogBuilder.create();
        addEntryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addEntryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addEntryDialog.show();


        init(view);

    }

    public void dissmisDialog(){
        addEntryDialog.dismiss();
    }

    private void init(View view) {
        entryName = view.findViewById(R.id.entryName);
        entryNumber = view.findViewById(R.id.entryNumber);
        income = view.findViewById(R.id.income);
        outcome = view.findViewById(R.id.outcome);
        entryPicker = view.findViewById(R.id.entryPicker);
        count = view.findViewById(R.id.count);
        doneBtn = view.findViewById(R.id.doneBtn);

        readCount();

        doneBtn.setOnClickListener(v -> {
            if (entryName.getText().length() < 2 || entryNumber.getText().length() < 1 || entryPicker.getDate().toString().length() < 1 || income.getText().toString().length() < 1 || outcome.getText().toString().length() < 1 || count.getText().toString().length()<1) {
                Toast.makeText(mContext, "Заполните все поля корректно", Toast.LENGTH_SHORT).show();
            } else {
                addEntry();
            }
        });
    }

    private void addEntry() {
        DatabaseReference newEntryReference = FirebaseDatabase.getInstance().getReference("Clothes").child(id).child("Entries");
        String entryId = newEntryReference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", entryName.getText().toString());
        hashMap.put("date", LazyDatePicker.dateToString(entryPicker.getDate(), DATE_FORMAT));
        hashMap.put("number", entryNumber.getText().toString());
        hashMap.put("id",entryId);
        hashMap.put("income", income.getText().toString());
        hashMap.put("outcome", outcome.getText().toString());
        hashMap.put("count", count.getText().toString());

        newEntryReference.child(entryId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    Toast.makeText(mContext, "Успешно", Toast.LENGTH_SHORT).show();
                    DatabaseReference clotheEntry = FirebaseDatabase.getInstance().getReference("Clothes").child(id);
                    clotheEntry.child("count").setValue(Integer.valueOf(count.getText().toString()));
                    clotheEntry.child("lastEntry").setValue(entryName.getText().toString() + " №" + entryNumber.getText().toString());
                    clotheEntry.child("lastEntryDate").setValue(LazyDatePicker.dateToString(entryPicker.getDate(), DATE_FORMAT));


                    dissmisDialog();
                }else{
                    Toast.makeText(mContext, "Не удалось " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    dissmisDialog();
                }
            }
        });


    }

    private void readCount() {

        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("Clothes").child(id);
        countReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Clothe clothe = snapshot.getValue(Clothe.class);
                count.setText(clothe.getCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}




