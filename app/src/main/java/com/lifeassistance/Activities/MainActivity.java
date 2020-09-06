package com.lifeassistance.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khaledz.lifeassistance.R;
import com.lifeassistance.Adapters.RecyclerViewAdapter;
import com.lifeassistance.Database.Task;
import com.lifeassistance.ViewModels.TaskViewModel;
import com.ohoussein.playpause.PlayPauseView;
import com.shinelw.library.ColorArcProgressBar;

public class MainActivity extends AppCompatActivity {

    private static int selectedTask = -1;

    private static final String TAG = "MAIN ACTIVITY";

    private static TaskViewModel mTaskViewModel;
    private static ColorArcProgressBar colorArcProgressBar;

    private FloatingActionButton fab;

    public static void viewTaskDialog(Context context, Task task, View v) {
        selectedTask = task.get_id();
        Log.d(TAG, "showing dialog");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.view_task_dialog);
        colorArcProgressBar = dialog.findViewById(R.id.circularProgressBar);
        Log.d(TAG + " progress", Float.toString(task.getProgress()));
        colorArcProgressBar.setMaxValues(task.getDuration());
        colorArcProgressBar.setCurrentValues(task.getProgress());
        dialog.show();

        if (task.getType() == Task.TIMED && !task.isIcCompleted()) {
            PlayPauseView playPauseView = dialog.findViewById(R.id.play_pause_view);
            if (task.isPlaying()) playPauseView.change(false, false);
            playPauseView.setVisibility(View.VISIBLE);
            playPauseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPauseView.toggle();
                    if (!playPauseView.isPlay()) {
                        Log.d(TAG, "task started");
                        task.setPlaying(true);
                        mTaskViewModel.setTaskPlaying(selectedTask, true);
                    } else {
                        task.setPlaying(false);
                        mTaskViewModel.setTaskPlaying(selectedTask, false);
                    }
                }
            });
        }
        if (task.isIcCompleted()) {
            TextView wellDoneTextView = dialog.findViewById(R.id.wellDoneTextView);
            wellDoneTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setEnabled(true);
    }

    public static void deleteTask(Context context, Task task) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        mTaskViewModel.deleteTask(task);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to remove this?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.tasksRecyclerView);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        mTaskViewModel.getAllTasks().observe(this, task -> {
            // Update the cached copy of the words in the adapter.
            for (int i = 0; i < task.size(); i++) {
                Log.d(TAG, "New task found: " + task.get(i).getTitle());
                if (colorArcProgressBar != null && task.get(i).get_id() == selectedTask) {
                    Log.d(TAG, "updating progress bard: " + task.get(i).getProgress());
                    colorArcProgressBar.setCurrentValues(task.get(i).getProgress());
                }
                adapter.setDataSet(task);
            }
        });
        Intent serviceIntent = new Intent(this, TaskProgressService.class);
        serviceIntent.putExtra("task", 6);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            fab.setEnabled(false); // avoid multiple clicks
            startActivity(new Intent(this, StepActivity.class));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}