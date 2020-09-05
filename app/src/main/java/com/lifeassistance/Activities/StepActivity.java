package com.lifeassistance.Activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.khaledz.lifeassistance.R;
import com.lifeassistance.Database.Task;
import com.lifeassistance.Steps.EditTextStep;
import com.lifeassistance.Steps.TimePIckStep;
import com.lifeassistance.Steps.TypeStep;
import com.lifeassistance.ViewModels.TaskViewModel;

import java.time.LocalDateTime;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class StepActivity extends Activity implements StepperFormListener {

    private static final String TAG = "STEP ACTIVITY";

    private EditTextStep titleEditText;
    private TypeStep typeStep;
    private TimePIckStep timePIckStep;

    private VerticalStepperFormView verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_step);

        // Create the steps.
        titleEditText = new EditTextStep("Task Title", "title");
        timePIckStep = new TimePIckStep("Duration (minutes)");

        // Find the form view, set it up and initialize it.
        verticalStepperForm = findViewById(R.id.stepper_forms);
        typeStep = new TypeStep("Type", verticalStepperForm);
        verticalStepperForm.setup(this, titleEditText, typeStep).
                allowNonLinearNavigation(true).
                closeLastStepOnCompletion(true).
                init();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.
        String typeString = typeStep.getStepData();
        int type = typeString.equals("Timed") ? Task.TIMED : Task.PROGRESSIVE;
        Log.d(TAG, "duration " + TypeStep.getDuration());
        Task task = new Task(titleEditText.getStepData(), type, 0, TypeStep.getDuration(), LocalDateTime.now());

        Log.d(TAG, "id: " + task.get_id());
        Log.d(TAG, "type: " + task.getType());
        Log.d(TAG, "title: " + task.getTitle());
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