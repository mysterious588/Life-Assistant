package com.lifeassistance.Steps;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.khaledz.lifeassistance.R;

import java.util.ArrayList;

import ernestoyaquello.com.verticalstepperform.Step;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;

public class TypeStep extends Step<String> {

    private static final int TIMED = 0;
    private static final int PROGRESSIVE = 1;
    private static final int DEFINED = 2;
    private static final int UNDEFINED = -1;

    private RadioGroup mRadioGroup;
    private RadioButton timedRadioButton, progressiveRadioButton;
    private static int duration;
    private static ArrayList<String> milestones;
    private final VerticalStepperFormView verticalStepperFormView;
    private TimePIckStep timePIckStep;
    private ProgressiveListStep progressiveListStep;
    private int chosenState = UNDEFINED;

    public TypeStep(String stepTitle, VerticalStepperFormView verticalStepperFormView) {
        super(stepTitle);
        this.verticalStepperFormView = verticalStepperFormView;
    }

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        TypeStep.duration = duration;
    }

    public static ArrayList<String> getMilestones() {
        return milestones;
    }

    public static void setMilestones(ArrayList<String> milestones) {
        TypeStep.milestones = milestones;
    }

    @Override
    protected View createStepContentLayout() {

        ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_enabled} //disabled
                , new int[]{android.R.attr.state_enabled} //enabled
        }, new int[]{getContext().getResources().getColor(R.color.colorAccent, null) //disabled
                , getContext().getResources().getColor(R.color.colorAccent, null)    //enabled
        });

        timedRadioButton = new RadioButton(getContext());
        timedRadioButton.setText("Timed");
        timedRadioButton.setTextColor(getContext().getResources().getColor(R.color.colorText, null));
        timedRadioButton.setButtonTintList(colorStateList);

        progressiveRadioButton = new RadioButton(getContext());
        progressiveRadioButton.setText("Progressive");
        progressiveRadioButton.setTextColor(getContext().getResources().getColor(R.color.colorText, null));
        progressiveRadioButton.setButtonTintList(colorStateList);

        mRadioGroup = new RadioGroup(getContext());
        mRadioGroup.addView(timedRadioButton);
        mRadioGroup.addView(progressiveRadioButton);
        mRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

        mRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> markAsCompletedOrUncompleted(true));

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
        if (getStepData().equals("Timed") && chosenState != TIMED) {
            // timed is picked
            timePIckStep = new TimePIckStep("Duration (minutes)");
            if (chosenState == PROGRESSIVE) {
                verticalStepperFormView.removeStep(2);
            }
            verticalStepperFormView.addStep(2, timePIckStep);
            chosenState = TIMED;

        } else if (getStepData().equals("Progressive") && chosenState != PROGRESSIVE) {
            // progressive is picked
            progressiveListStep = new ProgressiveListStep("Milestones", "Milestone");
            if (chosenState == TIMED) verticalStepperFormView.removeStep(2);
            chosenState = PROGRESSIVE;
            verticalStepperFormView.addStep(2, progressiveListStep);

        } else {
            // nothing is picked
            if (chosenState != UNDEFINED & chosenState != TIMED & chosenState != PROGRESSIVE) {
                verticalStepperFormView.removeStep(2);
                chosenState = DEFINED;
            }
        }
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