package com.lifeassistance.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khaledz.lifeassistance.R;
import com.lifeassistance.Adapters.RecyclerViewAdapter;
import com.lifeassistance.ViewModels.TaskViewModel;

public class ArchivedTasksActivity extends AppCompatActivity {

    RecyclerViewAdapter adapter;
    TaskViewModel mTaskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        mTaskViewModel = new TaskViewModel(getApplication());
        mTaskViewModel.getArchivedTasks().observe(this, tasks -> {
            adapter.setDataSet(tasks);
            adapter.notifyDataSetChanged();
        });

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.archivedTasksRecyclerView);
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}