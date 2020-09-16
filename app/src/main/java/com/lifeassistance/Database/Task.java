package com.lifeassistance.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.util.ArrayList;

@TypeConverters({LocalDateTimeConverter.class, ArrayListConverter.class})
@Entity(tableName = "tasks_table")
public class Task {
    public static final int TIMED = 0;
    public static final int PROGRESSIVE = 1;

    @PrimaryKey(autoGenerate = true)
    private int _id;

    private String title, details;
    private float progress;
    private int duration, type;
    private LocalDateTime dateAdded, dateDeadline;
    private boolean isPlaying = false, isCompleted = false, archived = false, isRepeating = false;
    private ArrayList<String> subTasks;


    public Task(String title, int type, float progress, int duration, LocalDateTime dateAdded) {
        this.title = title;
        this.type = type;
        this.progress = progress;
        this.duration = duration;
        this.dateAdded = dateAdded;
    }

    public LocalDateTime getDateDeadline() {
        return dateDeadline;
    }

    public void setDateDeadline(LocalDateTime dateDeadline) {
        this.dateDeadline = dateDeadline;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public ArrayList<String> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<String> subTasks) {
        this.subTasks = subTasks;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
