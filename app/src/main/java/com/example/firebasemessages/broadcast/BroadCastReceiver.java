package com.example.firebasemessages.broadcast;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;
import static com.example.firebasemessages.App.CHANNEL_1_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.firebasemessages.AuthenticationActivity;
import com.example.firebasemessages.R;
import com.example.firebasemessages.ui.maps.MapsFragment;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BroadCastReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            SmsMessage[] sms = getMessagesFromIntent(intent);
            String messageSms = sms[0].getDisplayMessageBody();

            String[] parts = messageSms.split(", ");
            String strLatitude = parts[parts.length - 2].replace("+", "").replace("-", "");
            String strLongitude = parts[parts.length - 1].replace("+", "").replace("-", "");
            if (parts.length == 5 && Pattern.matches("([0-9]+)[.]([0-9]+)", strLatitude)
                    && Pattern.matches("([0-9]+)[.]([0-9]+)", strLongitude)) {
                Map<String, Object> message = new HashMap<>();
                message.put("firstname", parts[0]);
                message.put("lastname", parts[1]);
                message.put("message", parts[2]);
                message.put("position", new GeoPoint(Double.parseDouble(parts[3]), Double.parseDouble(parts[4])));

                MapsFragment.getInstance().addToFirebase(message);
                Toast.makeText(context, "SMS reçu et ajouté !", Toast.LENGTH_SHORT).show();

                notificationManager = NotificationManagerCompat.from(context);
                String title = "FirebaseMessages";
                String content = "Ajout d'un SMS à la map";

                Intent i = new Intent(context, AuthenticationActivity.class);
                PendingIntent contentPendingIntent = PendingIntent.getActivity
                        (context, 0, i,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_baseline_map_24)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(contentPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(1, notification);

            }
        }
    }
}
