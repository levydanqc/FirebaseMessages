package com.example.firebasemessages.ui.maps;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.firebasemessages.model.Message;
import com.google.android.gms.maps.model.Marker;

public class MapsViewModel extends AndroidViewModel {

//    private final LiveData<List<Message>> messages;

    public MapsViewModel(Application application) {
        super(application);
        //TODO: Get from db
    }

//    public LiveData<List<Message>> getMessages() {
//        return messages;
//    }

    public String addMarker(Marker marker, String prenom, String nom) {
        String picture = "https://robohash.org/" + nom + prenom;
        Message message = new Message(prenom, nom, marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle());

        //TODO: Insert to db
        return picture;
    }
}