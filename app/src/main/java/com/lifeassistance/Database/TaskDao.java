package com.lifeassistance.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeassistance.Models.Task;

import java.util.List;

@Dao
public interface TaskDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Query("DELETE FROM tasks_table")
    void deleteAll();

    @Query("SELECT * FROM tasks_table")
    LiveData<List<Task>> getAlphabetizedTasks();

    @Query("SELECT * FROM tasks_table WHERE _id == :id")
    LiveData<Task> getTask(int id);

    @Update
    void updateTask(Task task);

}