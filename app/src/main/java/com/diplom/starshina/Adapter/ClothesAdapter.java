package com.diplom.starshina.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.starshina.Dialog.EntryDialog;
import com.diplom.starshina.Model.Clothe;
import com.diplom.starshina.R;

import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {

    Context mContext;
    List<Clothe> clothesList;

    // Конструктор адаптера
    public ClothesAdapter(Context mContext, List<Clothe> clothesList) {
        this.mContext = mContext;
        this.clothesList = clothesList;
    }


    // Создание View для адаптера
    @NonNull
    @Override
    public ClothesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.clothe_item_layout, parent, false);
        return new ClothesAdapter.ViewHolder(view);
    }

    /**
     * ------------------------------ Метод для поиска (применение нового листа)------------------------------
     */
    public void searchedList(List<Clothe> searchedList) {
        clothesList = searchedList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull ClothesAdapter.ViewHolder holder, int position) {
        Clothe clothe = clothesList.get(position);

        holder.clotheName.setText(clothe.getName());
        holder.clotheCount.setText(String.valueOf(clothe.getCount()));

        // Слушатель долгого нажатия для открытия диалога записей
        holder.clotheItemLayout.setOnLongClickListener(view -> {
            EntryDialog entryDialog = new EntryDialog(mContext, clothe.getId());
            entryDialog.startDialog();
            return false;
        });

        // Загрузка последней записи
        getLastEntry(clothe, holder.lastEntry, holder.lastEntryDate);


    }

    /**
     * ------------------------------ Загрузка последней записи ------------------------------
     */
    private void getLastEntry(Clothe clothe, TextView lastEntry, TextView lastEntryDate) {
        if (clothe.getLastEntry() != null || clothe.getLastEntry().equals("")) {
            lastEntry.setText("Последняя запись: " + clothe.getLastEntry());
        } else {
            lastEntry.setText("Последняя запись: пусто");
        }

        if (clothe.getLastEntryDate() != null || clothe.getLastEntryDate().equals("")) {
            lastEntryDate.setText("Дата записи: " + clothe.getLastEntryDate());
        } else {
            lastEntryDate.setText("Дата записи: пусто");
        }
    }

    @Override
    public int getItemCount() {
        return clothesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clotheName, lastEntry, lastEntryDate, clotheCount;
        LinearLayout clotheItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            clotheName = itemView.findViewById(R.id.clotheName);
            lastEntry = itemView.findViewById(R.id.lastEntry);
            lastEntryDate = itemView.findViewById(R.id.lastEntryDate);
            clotheCount = itemView.findViewById(R.id.clotheCount);
            clotheItemLayout = itemView.findViewById(R.id.clotheItemLayout);
        }
    }
}
