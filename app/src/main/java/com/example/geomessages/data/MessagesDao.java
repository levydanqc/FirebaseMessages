package com.example.geomessages.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.geomessages.model.Message;

import java.util.List;


@Dao
public interface MessagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Query("delete from messages")
    void deleteAll();

    @Query("select * from messages order by lastname")
    LiveData<List<Message>> getMessages();
}
