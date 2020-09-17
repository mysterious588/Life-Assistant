package com.lifeassistance.Adapters;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khaledz.lifeassistance.R;
import com.lifeassistance.Database.Task;
import com.lifeassistance.ViewModels.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProgressiveTasksAdapter extends RecyclerView.Adapter<ProgressiveTasksAdapter.ViewHolder> {

    private static final String TAG = "Progressive Tasks Adapter";

    private static List<String> subTasks;
    private static Task mTask;

    public ProgressiveTasksAdapter(Task task) {
        mTask = task;
        setDataSet(task);
    }

    public void setDataSet(Task task) {
        subTasks = task.getSubTasks();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressive_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        TextView milestoneTextView = holder.milestoneTextView;
        CheckBox milestoneCheckBox = holder.milestoneCheckBox;

        Boolean state = mTask.getSubTasksState().get(listPosition);
        Log.d(TAG, "subtask: " + mTask.getSubTasks().get(listPosition) + ", state: " + state);
        milestoneCheckBox.setChecked(mTask.getSubTasksState().get(listPosition));

        String task = subTasks.get(listPosition);
        milestoneTextView.setText(task);
    }

    @Override
    public int getItemCount() {
        try {
            Log.d(TAG, "size of milestones: " + subTasks.size());
        } catch (Exception ignored) {
        }
        if (subTasks != null) return subTasks.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView milestoneTextView;
        CheckBox milestoneCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.milestoneTextView = itemView.findViewById(R.id.milestoneTextView);
            this.milestoneCheckBox = itemView.findViewById(R.id.milestoneCheckBox);
            milestoneCheckBox.setEnabled(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int progress = 0;
                    ArrayList<Boolean> states = mTask.getSubTasksState();
                    if (milestoneCheckBox.isChecked()) {
                        states.set(getAdapterPosition(), false);
                        mTask.setSubTasksState(states);
                        TaskViewModel taskViewModel = new TaskViewModel((Application) itemView.getContext().getApplicationContext());
                        taskViewModel.updateTask(mTask);
                        milestoneCheckBox.setChecked(false);
                    } else {
                        states.set(getAdapterPosition(), true);
                        mTask.setSubTasksState(states);
                        milestoneCheckBox.setChecked(true);
                    }

                    for (int i = 0; i < states.size(); i++)
                        if (states.get(i)) progress++;
                    mTask.setProgress(progress);
                    if (progress == states.size()) mTask.setCompleted(true);
                    else mTask.setCompleted(false);
                    TaskViewModel taskViewModel = new TaskViewModel((Application) itemView.getContext().getApplicationContext());
                    taskViewModel.updateTask(mTask);
                }
            });

//            milestoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    ArrayList<Boolean> states = mTask.getSubTasksState();
//                    states.set(getAdapterPosition(), b);
//                    mTask.setSubTasksState(states);
//                    TaskViewModel taskViewModel = new TaskViewModel((Application) itemView.getContext().getApplicationContext());
//                    taskViewModel.updateTask(mTask);
//                }
//            });
        }
    }
}