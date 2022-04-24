package com.example.geomessages.ui.maps;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.geomessages.data.AppExecutors;
import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.model.Message;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class MapsViewModel extends AndroidViewModel {

    private final LiveData<List<Message>> messages;
    private MessagesRoomDatabase mDb;

    public MapsViewModel(Application application) {
        super(application);
        mDb = MessagesRoomDatabase.getDatabase(application);
        messages = mDb.messageDao().getMessages();
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public String addMarker(Marker marker, String prenom, String nom) {
        String picture = "https://robohash.org/" +
                nom.substring(0, 1).toUpperCase() + nom.substring(1).toLowerCase() +
                prenom.substring(0, 1).toUpperCase() + prenom.substring(1).toLowerCase();
        Message message = new Message(prenom, nom, picture, marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle());

        AppExecutors.getInstance().diskIO().execute(() -> mDb.messageDao().insert(message));

        return picture;
    }
}