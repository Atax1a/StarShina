package com.diplom.starshina.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Adapter.TempAdapter;
import com.diplom.starshina.Dialog.LoadingDialog;
import com.diplom.starshina.Model.Clothe;
import com.diplom.starshina.Model.Soldier;
import com.diplom.starshina.Model.Temporary;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TemporaryFragment extends Fragment {

    AutoCompleteTextView clotheName, soldierName;
    ImageButton addBtn;
    RecyclerView tempRecyclerView;
    Button addAllBtn;

    String date;

    ProgressBar loadingRv;

    TempAdapter tempAdapter;

    DatabaseReference clotheReference = FirebaseDatabase.getInstance().getReference("Clothes");
    DatabaseReference soldiersReference = FirebaseDatabase.getInstance().getReference("Soldiers");
    DatabaseReference tempReference = FirebaseDatabase.getInstance().getReference("Temporary");

    List<String> soldiersNameList = new ArrayList<>();
    List<String> clothesNameList = new ArrayList<>();

    Boolean sLoaded = false, cLoaded = false;

    LoadingDialog loadingDialog;

    List<Temporary> tempList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_temporary, container, false);

        init(root);

        getClotheNames();

        getSoldiersName();

        autoCompleteInit();

        addBtn.setOnClickListener(v -> {
            addTemp();
        });

        getTempData();

        addAllBtn.setOnClickListener(view -> {
            if (clotheName.getText().toString().length() > 1){
                checkDialog();
            }else{
                Toast.makeText(getContext(), "?????????????????? ???????? ??????????????????", Toast.LENGTH_SHORT).show();
                clotheName.setError("");
            }
        });

        return root;
    }

    /**
     * ------------------------------ ???????????? ?????????????????????????? ------------------------------
     */
    private void checkDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((getContext()));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.all_add_check, null, false);
        dialogBuilder.setView(view);

        AlertDialog deleteDialog = dialogBuilder.create();
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        deleteDialog.show();

        ImageButton noBtn, yesBtn;
        noBtn = view.findViewById(R.id.noBtn);
        yesBtn = view.findViewById(R.id.yesBtn);

        noBtn.setOnClickListener(v -> {
            deleteDialog.dismiss();
        });
        yesBtn.setOnClickListener(v -> {
            addAll();
            deleteDialog.dismiss();
            loadingDialog.startLoadingDialog();
        });
    }

    /**
     * ------------------------------ ?????????? ???????????????????? ?????????????????? ???????? ???????????? ------------------------------
     */
    private void addAll() {
        soldiersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String soldierNameTxt;
                String tempId;
                for (DataSnapshot soldierSnapshot : snapshot.getChildren()) {
                    Soldier soldier = soldierSnapshot.getValue(Soldier.class);
                    soldierNameTxt = soldier.getSurname() + " " + soldier.getName();
                    tempId = tempReference.push().getKey();
                    Temporary temporary = new Temporary(tempId, soldierNameTxt, clotheName.getText().toString(), date);
                    tempReference.child(tempId).setValue(temporary);
                }
                loadingDialog.dismisDialog();
                tempAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * ------------------------------ ?????????? ?????????????????? ???????????? ?? ?????????????????? ?????????????? ------------------------------
     */
    private void getTempData() {
        tempReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempList.clear();
                for (DataSnapshot tempSnapshot : snapshot.getChildren()) {
                    Temporary temporary = tempSnapshot.getValue(Temporary.class);
                    tempList.add(temporary);
                }
                Collections.sort(tempList);
                tempAdapter.notifyDataSetChanged();
                loadingRv.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * ------------------------------ ?????????? ???????????????????? ?????????????????? ???????????? ?? ???? ------------------------------
     */
    private void addTemp() {
        String clothe, soldier, tempId;

        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        clothe = clotheName.getText().toString();
        soldier = soldierName.getText().toString();

        tempId = tempReference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", date);
        hashMap.put("clotheName", clothe);
        hashMap.put("soldierName", soldier);
        hashMap.put("id", tempId);
        if (clothe.length() < 2 || soldier.length() < 2) {
            Toast.makeText(getContext(), "?????????????????? ?????? ????????", Toast.LENGTH_SHORT).show();
            clotheName.setText("");
            soldierName.setText("");
            clotheName.setError("");
            soldierName.setError("");
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            tempReference.child(tempId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        Toast.makeText(getContext(), "??????????????!", Toast.LENGTH_SHORT).show();
                        clotheName.setText("");
                        soldierName.setText("");
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } else {
                        Toast.makeText(getContext(), "???? ?????????????? ???????????????????????? ?? ????: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }


    /**
     * ------------------------------ ?????????????????? AutoComplete ------------------------------
     */
    private void autoCompleteInit() {
        ArrayAdapter<String> soldiersAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, soldiersNameList);
        soldierName.setAdapter(soldiersAdapter);
        ArrayAdapter<String> clothesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, clothesNameList);
        clotheName.setAdapter(clothesAdapter);

        checkLoaded();
    }

    /**
     * ------------------------------ ???????????????? ?????????????????? ???????????? ------------------------------
     */
    private void checkLoaded() {
        if (sLoaded && cLoaded) {
            loadingDialog.dismisDialog();
        } else {
            getClotheNames();
            getSoldiersName();

            if (sLoaded && cLoaded) {
                loadingDialog.dismisDialog();
            } else {
                loadingDialog.dismisDialog();
            }
        }
    }

    /**
     * ------------------------------ ?????????? ?????????????????? ???????????? ?????? ???????????? ???????????????? ??????????????????------------------------------
     */
    private void getClotheNames() {
        clotheReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothesNameList.clear();
                for (DataSnapshot clotheSnapshot : snapshot.getChildren()) {
                    Clothe clothe = clotheSnapshot.getValue(Clothe.class);
                    clothesNameList.add(clothe.getName());
                }
                if (clothesNameList.size() > 0) {
                    cLoaded = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cLoaded = false;
                Toast.makeText(getContext(), "???????????? ?????????????????????? ?? ????: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ------------------------------ ?????????? ?????????????????? ???????????? ?????? ???????????? ???????? ????????????------------------------------
     */
    private void getSoldiersName() {
        soldiersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soldiersNameList.clear();
                for (DataSnapshot soldierSnapshot : snapshot.getChildren()) {
                    Soldier soldier = soldierSnapshot.getValue(Soldier.class);
                    soldiersNameList.add(soldier.getSurname() + " " + soldier.getName());
                }
                if (soldiersNameList.size() > 0) {
                    sLoaded = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "???????????? ?????????????????????? ?? ????: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View root) {
        clotheName = root.findViewById(R.id.clotheName);
        soldierName = root.findViewById(R.id.soldierName);
        addBtn = root.findViewById(R.id.addBtn);
        addAllBtn = root.findViewById(R.id.addAllBtn);
        loadingRv = root.findViewById(R.id.loadingRv);
        tempRecyclerView = root.findViewById(R.id.tempRecyclerView);

        loadingRv.setVisibility(View.VISIBLE);

        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.startLoadingDialog();

        final String DATE_FORMAT = "dd.MM.yyyy";
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        date = dateFormat.format(currentDate).toString();

        // ?????????????????? RV
        tempRecyclerView.setHasFixedSize(true);
        // ?????????????? LayoutManager ?? ???????? LinearLayout???? ?????? ???????????? RV
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // ?????????????????? ?????? ???????????? RV ?????????????????? LM
        tempRecyclerView.setLayoutManager(layoutManager);
        // ?????????????? ???????????? ????????????
        tempList = new ArrayList<>();
        // ?????????????? ?????????????????? ???????????????? ?? ?????????????? ?????????????????? ??????????????
        tempAdapter = new TempAdapter(tempList, getContext());
        // ?????????????????? ???????????? ?????????????? ?? ???????????? RV
        tempRecyclerView.setAdapter(tempAdapter);

    }
}