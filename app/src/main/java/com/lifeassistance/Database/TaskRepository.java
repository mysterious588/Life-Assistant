package com.lifeassistance.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        mTaskDao = db.mTaskDao();
        mAllTasks = mTaskDao.getAlphabetizedTasks();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.insert(task);
        });
    }

    public LiveData<Task> getTask(int id) {
        return mTaskDao.getTask(id);
    }

    public List<Task> getAllTasksSynced() {
        return mTaskDao.getAlphabetizedTasksSynced();
    }

    public Task getTaskSynced(int id) {
        return mTaskDao.getTaskSynced(id);
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return mTaskDao.getCompletedTasks();
    }

    public LiveData<List<Task>> getUnCompletedTasks() {
        return mTaskDao.getUnCompletedTasks();
    }

    public LiveData<List<Task>> getArchivedTasks() {
        return mTaskDao.getArchivedTasks();
    }

    public void updateTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.updateTask(task);
        });
    }

    public void deleteTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteTask(task);
        });
    }

    public void setTaskPlaying(int id, boolean state) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.setTaskPlaying(id, state);
        });
    }
}