package com.example.firebasemessages.ui.liste;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.firebasemessages.data.MessagesRoomDatabase;
import com.example.firebasemessages.model.Message;

import java.util.List;

public class ListeViewModel extends AndroidViewModel {

    private final LiveData<List<Message>> messages;

    public ListeViewModel(Application application) {
        super(application);
        MessagesRoomDatabase mDb = MessagesRoomDatabase.getDatabase(application);
        messages = mDb.messageDao().getMessages();
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }
}