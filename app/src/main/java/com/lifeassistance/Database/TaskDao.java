package com.lifeassistance.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM tasks_table")
    List<Task> getAlphabetizedTasksSynced();

    @Query("SELECT * FROM tasks_table WHERE _id == :id")
    LiveData<Task> getTask(int id);

    @Query("SELECT * FROM tasks_table WHERE _id == :id")
    Task getTaskSynced(int id);

    @Query("UPDATE tasks_table SET isPlaying = :state WHERE _id == :id")
    void setTaskPlaying(int id, boolean state);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}