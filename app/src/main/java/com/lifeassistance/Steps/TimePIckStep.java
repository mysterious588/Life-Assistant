package com.lifeassistance.Steps;

import android.view.View;
import android.widget.NumberPicker;

import ernestoyaquello.com.verticalstepperform.Step;

public class TimePIckStep extends Step<Integer> {

    private NumberPicker timePicker;

    public TimePIckStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        if (timePicker == null) timePicker = new NumberPicker(getContext());
        timePicker.setMinValue(0);
        timePicker.setMaxValue(24 * 60);

        timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                markAsCompletedOrUncompleted(true);
            }
        });
        return timePicker;
    }

    @Override
    protected IsDataValid isStepDataValid(Integer stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.

        return new IsDataValid(stepData > 0);
    }

    @Override
    public Integer getStepData() {
        return timePicker.getValue();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return getStepData().toString() + " minutes";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
        TypeStep.setDuration(getStepData());
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
    public void restoreStepData(Integer stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        timePicker.setValue(stepData);
    }
}
