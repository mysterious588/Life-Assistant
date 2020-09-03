package com.lifeassistance.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khaledz.lifeassistance.R;
import com.lifeassistance.Models.Task;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Task> dataSet;

    public List<Task> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<Task> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        //TODO: add the on click listener
        //view.setOnClickListener(MainActivity.myOnClickListener);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        TextView titleTextView = holder.titleTextView;
        TextView detailsTextView = holder.detailsTextView;

        titleTextView.setText(dataSet.get(listPosition).getTitle());
        detailsTextView.setText(dataSet.get(listPosition).getDetails());
    }

    @Override
    public int getItemCount() {
        if (dataSet != null) return dataSet.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView detailsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            this.detailsTextView = (TextView) itemView.findViewById(R.id.detailsTextView);
        }
    }
}