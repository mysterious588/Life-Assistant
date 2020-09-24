package com.lifeassistance.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeassistance.Database.Task;
import com.lifeassistance.Database.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository mRepository;

    private LiveData<List<Task>> mAllTasks;

    public TaskViewModel(Application application) {
        super(application);
        mRepository = new TaskRepository(application);
        mAllTasks = mRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public List<Task> getAllTasksSynced() {
        return mRepository.getAllTasksSynced();
    }

    public void insert(Task task) {
        mRepository.insert(task);
    }

    public LiveData<Task> getTask(int id) {
        return mRepository.getTask(id);
    }

    public Task getTaskSynced(int id) {
        return mRepository.getTaskSynced(id);
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return mRepository.getCompletedTasks();
    }

    public LiveData<List<Task>> getUnCompletedTasks() {
        return mRepository.getUnCompletedTasks();
    }

    public LiveData<List<Task>> getArchivedTasks() {
        return mRepository.getArchivedTasks();
    }

    public void updateTask(Task task) {
        mRepository.updateTask(task);
    }

    public void deleteTask(Task task) {
        mRepository.deleteTask(task);
    }

    public void setTaskPlaying(int id, boolean state) {
        mRepository.setTaskPlaying(id, state);
    }
}