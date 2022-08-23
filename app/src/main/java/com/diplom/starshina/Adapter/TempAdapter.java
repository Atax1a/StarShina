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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Model.Temporary;
import com.diplom.starshina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TempAdapter extends RecyclerView.Adapter<TempAdapter.ViewHolder> {
    List<Temporary> temporaryList;
    Context mContext;

    public TempAdapter(List<Temporary> temporaryList, Context mContext) {
        this.temporaryList = temporaryList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TempAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.temp_item_layout, parent, false);
        return new TempAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempAdapter.ViewHolder holder, int position) {
        Temporary temporary = temporaryList.get(position);

        holder.clotheName.setText(temporary.getClotheName());
        holder.soldierName.setText(temporary.getSoldierName());
        holder.oldDate.setText("Выдано: " +  temporary.getDate());

        final String DATE_FORMAT = "dd.MM.yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date oldDate = new Date();
        Date currentDate = new Date();
        try {
            oldDate = dateFormat.parse(temporary.getDate());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        long diffInMillies = Math.abs(currentDate.getTime() - (oldDate.getTime()));
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        holder.daysAgo.setText(String.valueOf(diff) + "д \nназад");

        holder.tempItemLayout.setOnLongClickListener(view -> {
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
                DatabaseReference tempReference = FirebaseDatabase.getInstance().getReference("Temporary").child(temporary.getId());
                tempReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        });
    }

    @Override
    public int getItemCount() {
        return temporaryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clotheName, soldierName, oldDate, daysAgo;
        RelativeLayout tempItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clotheName = itemView.findViewById(R.id.clotheName);
            soldierName = itemView.findViewById(R.id.soldierName);
            oldDate = itemView.findViewById(R.id.oldDate);
            daysAgo = itemView.findViewById(R.id.daysAgo);
            tempItemLayout = itemView.findViewById(R.id.tempItemLayout);
        }
    }
}
