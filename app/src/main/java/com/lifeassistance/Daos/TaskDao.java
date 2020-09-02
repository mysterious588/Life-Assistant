package com.lifeassistance.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.lifeassistance.Models.Task;

import java.util.List;

@Dao
public interface TaskDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Task word);

    @Query("DELETE FROM tasks_table")
    void deleteAll();

    @Query("SELECT * from tasks_table ORDER BY title ASC")
    List<Task> getAlphabetizedtasks();
}