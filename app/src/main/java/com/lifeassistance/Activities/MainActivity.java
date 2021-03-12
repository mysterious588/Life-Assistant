package com.lifeassistance.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.khaledz.lifeassistance.R;
import com.lifeassistance.Adapters.ProgressiveTasksAdapter;
import com.lifeassistance.Adapters.RecyclerViewAdapter;
import com.lifeassistance.Database.Task;
import com.lifeassistance.Services.TaskProgressService;
import com.lifeassistance.ViewModels.TaskViewModel;
import com.ohoussein.playpause.PlayPauseView;
import com.shinelw.library.ColorArcProgressBar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static int selectedTask = -1;

    private static final String TAG = "MAIN ACTIVITY";

    private static TaskViewModel mTaskViewModel;
    private static ColorArcProgressBar colorArcProgressBar;

    private static TextView progressTextViewItemView;

    private FloatingActionButton fab;

    private static RecyclerViewAdapter adapter;

    private static RecyclerView progressiveTasksRecyclerView;

    private static Observer<List<Task>> allTasksObserver, incompleteTasksObserver, completedTasksObserver;
    private static Observer<List<Task>> currentObserver;

    private LiveData<List<Task>> returnTasks;

    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle mDrawerToggle;


    public static void viewTaskDialog(Context context, Task task, View v) {

        selectedTask = task.get_id();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.view_task_dialog);

        TextView dateTextView = dialog.findViewById(R.id.dateViewItemTextView);
        progressTextViewItemView = dialog.findViewById(R.id.progressViewItemTextView);

        String days = ChronoUnit.DAYS.between(task.getDateAdded(), LocalDateTime.now()) + "";
        DateTimeFormatter hourMinuteFormatter = DateTimeFormatter.ofPattern("H:mm a  ");
        dateTextView.setText(String.format("%s days ago %s", days, task.getDateAdded().format(hourMinuteFormatter)));

        colorArcProgressBar = dialog.findViewById(R.id.circularProgressBar);
        Log.d(TAG + " progress", Float.toString(task.getProgress()));
        colorArcProgressBar.setMaxValues(task.getDuration());
        colorArcProgressBar.setCurrentValues(task.getProgress());

        progressiveTasksRecyclerView = dialog.findViewById(R.id.tasksDialogRecyclerView);

        updateDialogUi(task, context);
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
    }
    /*

     */

    public static void viewTaskDetails(Context context, Task task, int position) {

        Dialog dialog = new Dialog(context);

        DialogInterface.OnClickListener deleteDialogClickListener = (d, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    mTaskViewModel.deleteTask(task);
                    adapter.notifyItemRemoved(position);
                    dialog.dismiss();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        DialogInterface.OnClickListener archiveDialogClickListener = (d, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    task.setArchived(true);
                    dialog.dismiss();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        dialog.setContentView(R.layout.view_task_details_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView titleTextView = dialog.findViewById(R.id.titleTextViewDialogDetails);
        TextView dateAddedTextView = dialog.findViewById(R.id.dateAddedTextViewDialogDetails);
        TextView deadlineTextView = dialog.findViewById(R.id.dateDeadlineDialogDetails);
        TextView progressTextView = dialog.findViewById(R.id.progressTextViewDialogDetails);
        TextView durationTextView = dialog.findViewById(R.id.durationTextViewDialogDetails);

        titleTextView.setText(task.getTitle());
        titleTextView.setMovementMethod(new ScrollingMovementMethod());
        dateAddedTextView.setText(task.getDateAdded().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
        progressTextView.setText(((int) task.getProgress()) * 100 / task.getDuration() + "%");
        if (task.getType() == Task.TIMED) durationTextView.setText(task.getDuration() + " Minutes");
        else durationTextView.setText(task.getDuration() + " Tasks");


        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);

        Button deleteButton = dialog.findViewById(R.id.deleteButtonDialogDetails);
        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to remove this?").
                    setPositiveButton("Yes", deleteDialogClickListener).
                    setNegativeButton("No", deleteDialogClickListener).
                    show();
        });

        Button archiveButton = dialog.findViewById(R.id.archiveButtonDialogDetails);
        archiveButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to archive this?").
                    setPositiveButton("Yes", archiveDialogClickListener).
                    setNegativeButton("No", archiveDialogClickListener).
                    show();
        });


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

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

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
        initNavigationDrawer();
    }

    private void initNavigationDrawer() {
        DrawerLayout mDrawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationDrawer);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.archivedTasksMenuItem, R.id.timelineMenuItem).
                setDrawerLayout(mDrawerLayout).
                build();
        // TODO make a fragment and set it up with the drawer
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view Item clicks here.
        int id = item.getItemId();

        if (id == R.id.archivedTasksMenuItem) {
            startActivity(new Intent(this, ArchivedTasksActivity.class));
            return true;
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static void updateDialogUi(Task task, Context context) {
        colorArcProgressBar.setCurrentValues(task.getProgress());

        if (task.isCompleted()) {
            progressTextViewItemView.setTextSize(24);
            progressTextViewItemView.setText(R.string.Completed);
            colorArcProgressBar.setUnit("Completed");
            if (task.getType() == Task.PROGRESSIVE) {
                ProgressiveTasksAdapter adapter = new ProgressiveTasksAdapter(task);
                progressiveTasksRecyclerView.setAdapter(adapter);
                progressiveTasksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                progressiveTasksRecyclerView.setVisibility(View.VISIBLE);
            }
        } else if (task.getType() == Task.TIMED) {
            progressTextViewItemView.setText(String.format("%d mins remaining", task.getDuration() - (int) task.getProgress()));
            colorArcProgressBar.setUnit("progress");
        } else {
            colorArcProgressBar.setUnit("progress");
            Log.d(TAG, "Launching milestones");
            progressTextViewItemView.setText(String.format("%d task(s) remaining", task.getDuration() - (int) task.getProgress()));
            ProgressiveTasksAdapter adapter = new ProgressiveTasksAdapter(task);
            progressiveTasksRecyclerView.setAdapter(adapter);
            progressiveTasksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            progressiveTasksRecyclerView.setVisibility(View.VISIBLE);

        }
    }

    public static void updateMilestonesState(Task task) {
        mTaskViewModel.updateTask(task);
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
                        Log.d(TAG, "All navigation chosen");
                        removeAllObservers(allTasksObserver);
                        returnTasks = mTaskViewModel.getAllTasks();
                        returnTasks.observe(MainActivity.this, allTasksObserver);
                        break;
                    case R.id.completeTasksMenuItem:
                        Log.d(TAG, "Complete navigation chosen");
                        removeAllObservers(completedTasksObserver);
                        returnTasks = mTaskViewModel.getCompletedTasks();
                        returnTasks.observe(MainActivity.this, completedTasksObserver);
                        break;
                    case R.id.incompleteTasksMenuItem:
                        Log.d(TAG, "Incomplete navigation chosen");
                        removeAllObservers(incompleteTasksObserver);
                        returnTasks = mTaskViewModel.getUnCompletedTasks();
                        returnTasks.observe(MainActivity.this, incompleteTasksObserver);
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

    private void initObservers() {
        allTasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Update the cached copy of the words in the adapter.
                for (int i = 0; i < tasks.size(); i++) {
                    Log.d(TAG, "New task found allObservers: " + tasks.get(i).getTitle());
                    if (colorArcProgressBar != null && tasks.get(i).get_id() == selectedTask) {
                        updateDialogUi(tasks.get(i), MainActivity.this);
                    }
                    if (currentObserver.equals(allTasksObserver)) {
                        refreshAdapter(tasks);
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
                        updateDialogUi(tasks.get(i), MainActivity.this);
                    }
                    if (currentObserver.equals(completedTasksObserver)) {
                        refreshAdapter(tasks);
                    }
                }
            }
        };
        incompleteTasksObserver = tasks -> {
            // Update the cached copy of the words in the adapter.
            for (int i = 0; i < tasks.size(); i++) {
                Log.d(TAG, "New task found unCompletedObservers: " + tasks.get(i).getTitle());
                if (colorArcProgressBar != null && tasks.get(i).get_id() == selectedTask) {
                    updateDialogUi(tasks.get(i), MainActivity.this);
                }
                if (currentObserver.equals(incompleteTasksObserver)) {
                    refreshAdapter(tasks);
                }
            }
        };
    }

    private void refreshAdapter(List<Task> tasks) {
        List<Task> tasksUpdated = new ArrayList<>();
        TextView emptyTextView = findViewById(R.id.emptyTextView);
        boolean showTextView = true;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!(task.get_id() == 1 || task.get_id() == 2)) {
                tasksUpdated.add(task);
                showTextView = false;
            }
        }
        if (showTextView) emptyTextView.setVisibility(View.VISIBLE);
        else emptyTextView.setVisibility(View.GONE);
        adapter.setDataSet(tasksUpdated);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}