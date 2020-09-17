package com.lifeassistance.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khaledz.lifeassistance.R;
import com.lifeassistance.Activities.MainActivity;
import com.lifeassistance.Database.Task;
import com.skydoves.progressview.ProgressView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static List<Task> dataSet;

    public List<Task> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<Task> dataSet) {
        RecyclerViewAdapter.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        TextView titleTextView = holder.titleTextView;
        TextView detailsTextView = holder.detailsTextView;
        TextView typeTextView = holder.typeTextView;
        TextView progressTextView = holder.progressTextView;
        ProgressView progressView = holder.progressView;

        Task task = dataSet.get(listPosition);

        titleTextView.setText(task.getTitle());
        detailsTextView.setText(task.getDetails());
        if (task.getType() == Task.TIMED)
            progressTextView.setText(String.format("%d/%d mins", (int) task.getProgress(), task.getDuration()));
        else
            progressTextView.setText(String.format("%d/%d tasks", (int) task.getProgress(), task.getDuration()));
        progressView.setMax(task.getDuration());
        progressView.setProgress(task.getProgress());

        if (task.getType() == Task.TIMED) typeTextView.setText(R.string.Timed);
        else typeTextView.setText(R.string.Progressive);
    }

    @Override
    public int getItemCount() {
        try {
            Log.d("rec", "" + dataSet.size());
        } catch (Exception ignored) {
        }
        if (dataSet != null) return dataSet.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, detailsTextView, typeTextView, progressTextView;
        ProgressView progressView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.titleTextView);
            this.detailsTextView = itemView.findViewById(R.id.detailsTextView);
            this.typeTextView = itemView.findViewById(R.id.typeTextView);
            this.progressView = itemView.findViewById(R.id.progressView);
            this.progressTextView = itemView.findViewById(R.id.progressTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Task task = dataSet.get(pos);
                        MainActivity.viewTaskDialog(view.getContext(), task, view);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Task task = dataSet.get(pos);
                        MainActivity.deleteTask(view.getContext(), task, pos);
                    }
                    return true;
                }
            });

        }
    }
}