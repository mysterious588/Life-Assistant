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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.khaledz.lifeassistance.R;
import com.lifeassistance.Adapters.RecyclerViewAdapter;
import com.lifeassistance.Database.Task;
import com.lifeassistance.Services.TaskProgressService;
import com.lifeassistance.ViewModels.TaskViewModel;
import com.ohoussein.playpause.PlayPauseView;
import com.shinelw.library.ColorArcProgressBar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int selectedTask = -1;

    private static final String TAG = "MAIN ACTIVITY";

    private static TaskViewModel mTaskViewModel;
    private static ColorArcProgressBar colorArcProgressBar;

    private static TextView progressTextViewItemView;

    private FloatingActionButton fab;

    private static RecyclerViewAdapter adapter;

    private static Observer<List<Task>> allTasksObserver, incompleteTasksObserver, completedTasksObserver;
    private static Observer<List<Task>> currentObserver;

    public static void viewTaskDialog(Context context, Task task, View v) {

        selectedTask = task.get_id();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.view_task_dialog);

        TextView dateTextView = dialog.findViewById(R.id.dateViewItemTextView);
        progressTextViewItemView = dialog.findViewById(R.id.progressViewItemTextView);

        String days = ChronoUnit.DAYS.between(task.getDate(), LocalDateTime.now()) + "";
        DateTimeFormatter hourMinuteFormatter = DateTimeFormatter.ofPattern("HH:mm");
        dateTextView.setText(String.format("%s days ago %s", days, task.getDate().format(hourMinuteFormatter)));

        colorArcProgressBar = dialog.findViewById(R.id.circularProgressBar);
        Log.d(TAG + " progress", Float.toString(task.getProgress()));
        colorArcProgressBar.setMaxValues(task.getDuration());
        colorArcProgressBar.setCurrentValues(task.getProgress());

        updateDialogUi(task);
        dialog.show();

        if (task.getType() == Task.TIMED && !task.isCompleted()) {
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
                        Log.d(TAG, "task paused");
                        task.setPlaying(false);
                        mTaskViewModel.setTaskPlaying(selectedTask, false);
                    }
                }
            });
        }
        if (task.isCompleted()) {
//            TextView wellDoneTextView = dialog.findViewById(R.id.wellDoneTextView);
//            wellDoneTextView.setVisibility(View.VISIBLE);
        }
    }

    public static void deleteTask(Context context, Task task, int position) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (mTaskViewModel.getAllTasks().getValue().size() == 1) {
                            Log.d(TAG, "list empty");
                            mTaskViewModel.deleteTask(task);
                            adapter.setDataSet(null);
                            adapter.notifyItemRemoved(position);

                        } else {
                            mTaskViewModel.deleteTask(task);
                            Log.d(TAG, "list not empty, size: " + mTaskViewModel.getAllTasks().getValue().size());
                            adapter.notifyItemRemoved(position);
                        }
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

    private static void removeAllObservers(Observer keep) {
        if (keep.equals(allTasksObserver)) {
            currentObserver = allTasksObserver;
            mTaskViewModel.getUnCompletedTasks().removeObserver(completedTasksObserver);
            mTaskViewModel.getCompletedTasks().removeObserver(incompleteTasksObserver);
        } else if (keep.equals(completedTasksObserver)) {
            currentObserver = completedTasksObserver;
            mTaskViewModel.getAllTasks().removeObserver(allTasksObserver);
            mTaskViewModel.getCompletedTasks().removeObserver(incompleteTasksObserver);
        } else {
            currentObserver = incompleteTasksObserver;
            mTaskViewModel.getUnCompletedTasks().removeObserver(completedTasksObserver);
            mTaskViewModel.getAllTasks().removeObserver(allTasksObserver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setEnabled(true);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentObserver = tasks -> {
        }; // set to a non null value
        initObservers();
        initRecyclerView();
        initService();
        initViewModel();
        removeAllObservers(allTasksObserver);
        initFab();
        initChipNavigationBar();
    }

    private static void updateDialogUi(Task task) {
        colorArcProgressBar.setCurrentValues(task.getProgress());

        if (task.isCompleted()) {
            progressTextViewItemView.setTextSize(24);
            progressTextViewItemView.setText(R.string.Completed);
            colorArcProgressBar.setUnit("Completed");
        } else if (task.getType() == Task.TIMED)
            progressTextViewItemView.setText(String.format("%d mins remaining", task.getDuration() - (int) task.getProgress()));
    }

    private void initObservers() {
        allTasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Update the cached copy of the words in the adapter.
                for (int i = 0; i < tasks.size(); i++) {
                    Log.d(TAG, "New task found allObservers: " + tasks.get(i).getTitle());
                    if (colorArcProgressBar != null && tasks.get(i).get_id() == selectedTask) {
                        updateDialogUi(tasks.get(i));
                    }
                    if (currentObserver.equals(allTasksObserver)) {
                        adapter.setDataSet(tasks);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        completedTasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Update the cached copy of the words in the adapter.
                for (int i = 0; i < tasks.size(); i++) {
                    Log.d(TAG, "New task found completedObservers: " + tasks.get(i).getTitle());
                    if (colorArcProgressBar != null && tasks.get(i).get_id() == selectedTask) {
                        updateDialogUi(tasks.get(i));
                    }
                    if (currentObserver.equals(completedTasksObserver)) {
                        adapter.setDataSet(tasks);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        incompleteTasksObserver = tasks -> {
            // Update the cached copy of the words in the adapter.
            for (int i = 0; i < tasks.size(); i++) {
                Log.d(TAG, "New task found unCompletedObservers: " + tasks.get(i).getTitle());
                if (colorArcProgressBar != null && tasks.get(i).get_id() == selectedTask) {
                    updateDialogUi(tasks.get(i));
                }
                if (currentObserver.equals(incompleteTasksObserver)) {
                    adapter.setDataSet(tasks);
                    adapter.notifyDataSetChanged();
                }
            }
        };
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.tasksRecyclerView);
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initService() {
        Intent serviceIntent = new Intent(this, TaskProgressService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void initViewModel() {
        mTaskViewModel = new TaskViewModel(getApplication());
        mTaskViewModel.getAllTasks().observe(this, allTasksObserver);
    }

    private void initChipNavigationBar() {
        ChipNavigationBar chipNavigationBar = findViewById(R.id.navigationView);
        chipNavigationBar.setItemSelected(R.id.allTasksMenuItem, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.allTasksMenuItem:
                        Log.d(TAG, "home navigation chosen");
                        removeAllObservers(allTasksObserver);
                        mTaskViewModel.getAllTasks().observe(MainActivity.this, allTasksObserver);
                        if (mTaskViewModel.getAllTasks().getValue().size() == 0) {
                            adapter.setDataSet(null);
                        }
                        break;
                    case R.id.completeTasksMenuItem:
                        Log.d(TAG, "activity navigation chosen");
                        removeAllObservers(completedTasksObserver);
                        mTaskViewModel.getCompletedTasks().observe(MainActivity.this, completedTasksObserver);
                        if (mTaskViewModel.getCompletedTasks().getValue() == null || mTaskViewModel.getCompletedTasks().getValue().size() == 0) {
                            adapter.setDataSet(null);
                        }
                        break;
                    case R.id.incompleteTasksMenuItem:
                        Log.d(TAG, "favorites navigation chosen");
                        removeAllObservers(incompleteTasksObserver);
                        mTaskViewModel.getUnCompletedTasks().observe(MainActivity.this, incompleteTasksObserver);
                        if (mTaskViewModel.getUnCompletedTasks().getValue() == null || mTaskViewModel.getCompletedTasks().getValue().size() == 0) {
                            adapter.setDataSet(null);
                        }
                        break;
                }
            }
        });
    }

    private void initFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            fab.setEnabled(false); // avoid multiple clicks
            startActivity(new Intent(this, StepActivity.class));
        });
    }
}