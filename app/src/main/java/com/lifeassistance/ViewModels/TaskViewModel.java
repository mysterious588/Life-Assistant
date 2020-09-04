package com.lifeassistance.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeassistance.Database.TaskRepository;
import com.lifeassistance.Models.Task;

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

    public void insert(Task task) {
        mRepository.insert(task);
    }

    public LiveData<Task> getTask(int id) {
        return mRepository.getTask(id);
    }

    public void updateTask(Task task) {
        mRepository.updateTask(task);
    }

    public void deleteTask(Task task) {
        mRepository.deleteTask(task);
    }
}