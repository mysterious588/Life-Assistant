package com.lifeassistance.Steps;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ernestoyaquello.com.verticalstepperform.Step;

public class TypeStep extends Step<String> {

    private RadioGroup mRadioGroup;
    private RadioButton timedRadioButton, progressiveRadioButton;

    public TypeStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        mRadioGroup = new RadioGroup(getContext());
        timedRadioButton = new RadioButton(getContext());
        timedRadioButton.setText("Timed");
        progressiveRadioButton = new RadioButton(getContext());
        progressiveRadioButton.setText("progressive");
        mRadioGroup.addView(timedRadioButton);
        mRadioGroup.addView(progressiveRadioButton);
        mRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                markAsCompletedOrUncompleted(true);
            }
        });

        return mRadioGroup;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(timedRadioButton.isChecked() || progressiveRadioButton.isChecked(), "You must select a type");
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        if (timedRadioButton.isChecked()) {
            return "Timed";
        } else if (progressiveRadioButton.isChecked()) return "Progressive";
        else {
            return "Empty";
        }
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return getStepData();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        if (stepData.equals("Timed")) timedRadioButton.setSelected(true);
        else progressiveRadioButton.setSelected(true);
    }
}