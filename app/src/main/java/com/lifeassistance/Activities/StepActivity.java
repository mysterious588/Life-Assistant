package com.lifeassistance.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.khaledz.lifeassistance.R;
import com.lifeassistance.EditTextStep;
import com.lifeassistance.Models.Task;
import com.lifeassistance.TypeStep;
import com.lifeassistance.ViewModels.TaskViewModel;

import java.time.LocalDateTime;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class StepActivity extends Activity implements StepperFormListener {

    private static final String TAG = "STEP ACTIVITY";

    private EditTextStep titleEditText, detailsEditText;
    private TypeStep typeStep;

    private VerticalStepperFormView verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_step);

        // Create the steps.
        titleEditText = new EditTextStep("Task Title", "title");
        detailsEditText = new EditTextStep("Details", "details");
        typeStep = new TypeStep("Type");

        // Find the form view, set it up and initialize it.
        verticalStepperForm = findViewById(R.id.stepper_forms);
        verticalStepperForm
                .setup(this, titleEditText, detailsEditText, typeStep).allowNonLinearNavigation(true).closeLastStepOnCompletion(true)
                .init();
    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.
        String typeString = typeStep.getStepData();
        int type = typeString.equals("Timed") ? Task.TIMED : Task.PROGRESSIVE;
        Task task = new Task(titleEditText.getStepData(), detailsEditText.getStepData(), type,0,60, LocalDateTime.now());

        Log.d(TAG, "id: " + task.get_id());
        Log.d(TAG, "type: " + task.getType());
        Log.d(TAG, "title: " + task.getTitle());
        Log.d(TAG, "details: " + task.getDetails());
        Log.d(TAG, "date: " + task.getDate().toString());

        TaskViewModel taskViewModel = new TaskViewModel(getApplication());
        taskViewModel.insert(task);
        finish();
    }

    @Override
    public void onCancelledForm() {
        // This method will be called when the user clicks on the cancel button of the form.
    }
}