package com.diplom.starshina;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.diplom.starshina.Model.DateTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotifWorker extends Worker {
    public NotifWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v("MyLog", "Есть");
        final String DATE_FORMAT = "dd.MM.yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date currentDate = new Date();

        DatabaseReference taskReference = FirebaseDatabase.getInstance().getReference("Tasks");
        taskReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateTaskSnapshot : snapshot.getChildren()) {
                    DateTask dateTask = dateTaskSnapshot.getValue(DateTask.class);
                    if (dateTask.getDate().equals(dateFormat.format(currentDate))) {
                        notif(dateTask);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return null;
    }

    private void notif(DateTask dateTask) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_tasks)
                        .setContentTitle("У вас есть задачи на сегодня!")
                        .setContentText("Задачи [" + dateTask.getTasksList().size() + "] на сегодня")
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
