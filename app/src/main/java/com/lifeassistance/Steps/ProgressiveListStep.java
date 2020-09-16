package com.lifeassistance.Steps;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ernestoyaquello.com.verticalstepperform.Step;

public class ProgressiveListStep extends Step<ArrayList<String>> {

    private ArrayList<EditText> taskEditTexts;
    private Button addButton;
    private String hint;

    public ProgressiveListStep(String stepTitle, String hint) {
        super(stepTitle);
        this.hint = hint;
        taskEditTexts = new ArrayList<>();
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText taskEditText = new EditText(getContext());
        taskEditTexts.add(taskEditText);
        taskEditText.setSingleLine(true);
        taskEditText.setHint(hint);

        addButton = new Button(getContext());
        addButton.setText("Add");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAsCompletedOrUncompleted(true);
                EditText editText = new EditText(getContext());
                taskEditTexts.add(editText);
                editText.setSingleLine(true);
                Button removeButton = new Button(getContext());
                removeButton.setText("-");

                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.addView(editText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                rowLayout.addView(removeButton);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(rowLayout, linearLayout.getChildCount() - 1);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.removeView(rowLayout);
                    }
                });
                editText.requestFocus();
            }
        });
        linearLayout.addView(taskEditText);
        linearLayout.addView(addButton);

        return linearLayout;
    }

    @Override
    protected IsDataValid isStepDataValid(ArrayList<String> stepData) {
        boolean isValid = false;
        for (int i = 0; i < getStepData().size(); i++)
            if (!getStepData().get(i).isEmpty()) isValid = true;

        String errorMessage = !isValid ? "can't be empty" : "";

        return new IsDataValid(isValid, errorMessage);
    }

    @Override
    public ArrayList<String> getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        ArrayList<String> tasks = new ArrayList<>();
        for (int i = 0; i < taskEditTexts.size(); i++)
            if (!taskEditTexts.get(i).getText().toString().isEmpty())
                tasks.add(taskEditTexts.get(i).getText().toString());
        return tasks;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        try {
            String task = getStepData().get(0);
            return task;
        } catch (Exception e) {
            return "empty";
        }
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.

        // hide the keyboard after entering data
        View view = ((Activity) (getContext())).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        TypeStep.setMilestones(getStepData());
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
    public void restoreStepData(ArrayList<String> stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
    }
}