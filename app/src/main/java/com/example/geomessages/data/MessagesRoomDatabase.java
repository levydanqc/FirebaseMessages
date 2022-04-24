package com.example.geomessages.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.geomessages.model.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = Message.class, version = 1, exportSchema = false)
public abstract class MessagesRoomDatabase extends RoomDatabase {
    public static volatile MessagesRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract MessagesDao messageDao();

    public static MessagesRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MessagesRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MessagesRoomDatabase.class, "messages")
                        .build();
            }
        }
        return INSTANCE;
    }
}
