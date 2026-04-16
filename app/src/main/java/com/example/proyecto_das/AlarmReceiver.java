package com.example.proyecto_das;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tituloPeli = intent.getStringExtra("TITULO_PELI");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Canal03");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel("Canal03", "CanalRecordatorio",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(canal);
        }

        builder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle("Recordatorio de Película")
                .setContentText("¡Hora de ver " + tituloPeli + "!") // Aquí usamos el dato dinámico
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}