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
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.firebasemessages.AuthenticationActivity;
import com.example.firebasemessages.R;
import com.example.firebasemessages.ui.maps.MapsFragment;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class BroadCastReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            SmsMessage[] sms = getMessagesFromIntent(intent);
            String messageSms = sms[0].getDisplayMessageBody();

            String[] parts = messageSms.split(", ");
            if (parts.length == 5) {
                try {
                    String strLatitude = parts[parts.length - 2].replace("+", "").replace("-", "");
                    String strLongitude = parts[parts.length - 1].replace("+", "").replace("-", "");
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

                    PendingIntent contentPendingIntent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        contentPendingIntent = PendingIntent.getActivity
                                (context, 0, i,
                                        PendingIntent.FLAG_MUTABLE);
                    } else {
                        contentPendingIntent = PendingIntent.getActivity
                                (context, 0, i,
                                        PendingIntent.FLAG_IMMUTABLE);
                    }
                    Notification notification = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_baseline_map_24)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setChannelId(CHANNEL_1_ID)
                            .setContentIntent(contentPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(165543, notification);

                    MapsFragment.getInstance().playSound();
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }

        }
    }
}
