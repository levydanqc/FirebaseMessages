package com.example.firebasemessages.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.firebasemessages.R;

public class SoundEffectService extends Service {
    MediaPlayer mediaPlayer;

    public SoundEffectService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int audio = intent.getIntExtra("audio", R.raw.gotitem);

        mediaPlayer = MediaPlayer.create(this, audio);

        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}