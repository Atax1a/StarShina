package com.diplom.starshina.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Model.ClotheEntry;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

    List<ClotheEntry> entrysList;
    Context mContext;
    String clotheId;

    public EntryAdapter(List<ClotheEntry> entrysList, Context mContext,String clotheId) {
        this.entrysList = entrysList;
        this.mContext = mContext;
        this.clotheId = clotheId;
    }

    @NonNull
    @Override
    public EntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.entry_item_layout, parent, false);
        return new EntryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryAdapter.ViewHolder holder, int position) {

        ClotheEntry clotheEntry = entrysList.get(position);

        holder.entryName.setText(clotheEntry.getName());
        holder.entryDate.setText(clotheEntry.getDate());
        holder.entryNumber.setText(clotheEntry.getNumber());
        holder.entryIncome.setText(clotheEntry.getIncome());
        holder.entryOutcome.setText(clotheEntry.getOutcome());
        holder.entryCount.setText(clotheEntry.getCount());

        holder.entryItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((mContext));
                view = LayoutInflater.from(mContext).inflate(R.layout.entry_delete_dialog, null, false);
                dialogBuilder.setView(view);

                AlertDialog deleteDialog = dialogBuilder.create();
                deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                deleteDialog.show();

                ImageButton noBtn, yesBtn;
                noBtn = view.findViewById(R.id.noBtn);
                yesBtn = view.findViewById(R.id.yesBtn);

                noBtn.setOnClickListener(v->{
                    deleteDialog.dismiss();
                });
                yesBtn.setOnClickListener(v->{
                    DatabaseReference entryReference = FirebaseDatabase.getInstance().getReference("Clothes").child(clotheId).child("Entries").child(clotheEntry.getId());
                    entryReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                Toast.makeText(mContext, "Успешно", Toast.LENGTH_SHORT).show();
                                deleteDialog.dismiss();
                            }else{
                                Toast.makeText(mContext, "Не удалось удалить запись " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                deleteDialog.dismiss();
                            }
                        }
                    });
                });
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return entrysList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView entryName, entryDate, entryNumber, entryIncome, entryOutcome, entryCount;
        RelativeLayout entryItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            entryName = itemView.findViewById(R.id.entryName);
            entryDate = itemView.findViewById(R.id.entryDate);
            entryNumber = itemView.findViewById(R.id.entryNumber);
            entryIncome = itemView.findViewById(R.id.entryIncome);
            entryOutcome = itemView.findViewById(R.id.entryOutcome);
            entryCount = itemView.findViewById(R.id.entryCount);
            entryItemLayout = itemView.findViewById(R.id.entryItemLayout);
        }
    }
}
