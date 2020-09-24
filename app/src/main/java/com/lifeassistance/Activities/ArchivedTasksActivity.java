package com.lifeassistance.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.khaledz.lifeassistance.R;
import com.lifeassistance.Adapters.RecyclerViewAdapter;
import com.lifeassistance.Database.Task;
import com.lifeassistance.ViewModels.TaskViewModel;

import java.util.List;

public class ArchivedTasksActivity extends AppCompatActivity {

    RecyclerViewAdapter adapter;
    TaskViewModel mTaskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        initRecyclerView();

        mTaskViewModel = new TaskViewModel(getApplication());
        mTaskViewModel.getArchivedTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setDataSet(tasks);
            }
        });

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.archivedTasksRecyclerView);
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}