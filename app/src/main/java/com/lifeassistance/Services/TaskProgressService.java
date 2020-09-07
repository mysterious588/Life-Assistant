package com.lifeassistance.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.lifeassistance.Database.Task;
import com.lifeassistance.ViewModels.TaskViewModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskProgressService extends LifecycleService {

    private static final String TAG = "Task Progress Service";

    TaskViewModel mTaskViewModel;

    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.d(TAG, "running timer");
            List<Task> tasks = mTaskViewModel.getAllTasksSynced();
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (task.isPlaying() && !task.isCompleted()) {
                    task.setProgress(task.getProgress() + 1);
                    if (task.getProgress() >= task.getDuration()) {
                        task.setCompleted(true);
                    }
                    mTaskViewModel.updateTask(task);
                }
            }
        }
    };


    public TaskProgressService() {
    }

    public void start() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 60000);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onCreate() {
        mTaskViewModel = new TaskViewModel(getApplication());
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "CHANNEL_LIFE_ASSISTANT";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).
                    setContentTitle("Working").
                    setContentText("Here").build();
            Log.d(TAG, "binding service");
            startForeground(1, notification);
            start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartService = new Intent("CallTaskProgressService");
        sendBroadcast(restartService);
    }
}
