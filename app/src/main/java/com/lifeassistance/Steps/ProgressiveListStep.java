package com.lifeassistance.Steps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.khaledz.lifeassistance.R;
import com.lifeassistance.Apis.YouTubeAPI;
import com.lifeassistance.Utils.RegexProvider;

import java.util.ArrayList;

import ernestoyaquello.com.verticalstepperform.Step;

public class ProgressiveListStep extends Step<ArrayList<String>> {

    private static final String TAG = "Progressive List Step";
    private final ArrayList<EditText> taskEditTexts;
    private final String hint;
    private MaterialButton addButton, importFromYouTubeButton;

    public ProgressiveListStep(String stepTitle, String hint) {
        super(stepTitle);
        this.hint = hint;
        taskEditTexts = new ArrayList<>();
    }

    @SuppressLint("ResourceType")
    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.

        Context context = getContext();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText taskEditText = new EditText(context);
        taskEditTexts.add(taskEditText);
        taskEditText.setSingleLine(true);
        taskEditText.setHint(hint);
        taskEditText.setTextColor(context.getResources().getColor(R.color.colorText, null));
        taskEditText.setHintTextColor(context.getResources().getColor(R.color.colorHintText, null));

        importFromYouTubeButton = new MaterialButton(context);
        importFromYouTubeButton.setText("Import From YouTube");
        importFromYouTubeButton.setBackgroundColor(Color.RED);
        importFromYouTubeButton.setTextColor(Color.WHITE);

        importFromYouTubeButton.setOnClickListener(view -> {
            Dialog importDialog = new Dialog(context);

            LinearLayout dialogLinearLayout = new LinearLayout(context);
            dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLinearLayout.setBackgroundColor(Color.BLACK);
            dialogLinearLayout.setPadding(16, 16, 16, 16);

            EditText editText = new EditText(context);
            editText.setHintTextColor(Color.DKGRAY);
            editText.setTextColor(Color.WHITE);
            editText.setHint("Link");
            editText.setId(223);

            MaterialButton button = new MaterialButton(context);
            button.setText("Import");

            dialogLinearLayout.addView(editText);
            dialogLinearLayout.addView(button);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            importDialog.addContentView(dialogLinearLayout, params);
            importDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setOnClickListener(view1 -> {
                if (editText.getText().toString().isEmpty()) {
                    showToast("Please type in the link");
                } else if (!RegexProvider.isLinkValid(editText.getText().toString().trim())) {
                    showToast("Link isn't valid");
                } else {
                    Integer[] ids = {editText.getId(), linearLayout.getId()};
                    ParamsAsync paramsAsync = new ParamsAsync(importDialog, ids);
                    new YouTubeListDownloader().execute(paramsAsync);
                    importDialog.hide();
                }
            });

            importDialog.show();
        });

        addButton = new MaterialButton(context);
        addButton.setText("Add");
        addButton.setBackgroundColor(context.getResources().getColor(R.color.colorAccent, null));
        addButton.setTextColor(context.getResources().getColor(R.color.colorText, null));

        linearLayout.setId(941);

        addButton.setOnClickListener(view -> {
            addMileStone(linearLayout.getId(), "");
        });

        MaterialButton repeatedButtonAdd = new MaterialButton(context);
        repeatedButtonAdd.setText("Add repeated task");
        repeatedButtonAdd.setOnClickListener(view -> {
            Dialog addRepeatedDialog = new Dialog(context);
            LinearLayout dialogLinearLayout = new LinearLayout(context);
            dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLinearLayout.setBackgroundColor(Color.BLACK);
            dialogLinearLayout.setPadding(16, 16, 16, 16);

            EditText editTextName = new EditText(context);
            editTextName.setHintTextColor(Color.DKGRAY);
            editTextName.setTextColor(Color.WHITE);
            editTextName.setHint("Repeated task name");
            editTextName.setId(223);

            EditText editTextRepeat = new EditText(context);
            editTextRepeat.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextRepeat.setHintTextColor(Color.DKGRAY);
            editTextRepeat.setTextColor(Color.WHITE);
            editTextRepeat.setHint("Repeat times");
            editTextRepeat.setId(224);


            MaterialButton generateButton = new MaterialButton(context);
            generateButton.setText("Generate");

            dialogLinearLayout.addView(editTextName);
            dialogLinearLayout.addView(editTextRepeat);
            dialogLinearLayout.addView(generateButton);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            addRepeatedDialog.addContentView(dialogLinearLayout, params);
            addRepeatedDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            generateButton.setOnClickListener(view1 -> {
                        if (editTextName.getText().toString().isEmpty()) {
                            showToast("Please type in the link");
                        } else if (editTextRepeat.getText().toString().isEmpty()) {
                            showToast("Please specify the repetition number");
                        } else {
                            Integer[] ids = {editTextName.getId(), linearLayout.getId()};
                            ParamsAsync paramsAsync = new ParamsAsync(addRepeatedDialog, ids);
                            new YouTubeListDownloader().execute(paramsAsync);
                            try {
                                int n = Integer.parseInt(editTextRepeat.getText().toString().trim());
                                for (int i = 1; i <= n; i++) {
                                    addMileStone(linearLayout.getId(), editTextName.getText().toString() + " " + i);
                                }
                                addRepeatedDialog.hide();
                            } catch (Exception e) {
                                showToast("inavlid");
                            }

                        }
                    }
            );

            addRepeatedDialog.show();
        });

        linearLayout.addView(taskEditText);
        linearLayout.addView(addButton);
        linearLayout.addView(repeatedButtonAdd);
        linearLayout.addView(importFromYouTubeButton);

        return linearLayout;
    }

    private PlaylistItemListResponse importYouTube(int editTextId, Dialog dialog) {
        EditText editText = dialog.findViewById(editTextId);

        String link = editText.getText().toString().trim();

        if (RegexProvider.isLinkValid(link)) {
            try {
                String playlistId = RegexProvider.extractYouTubePlaylistId(link);
                if (playlistId == null) return null;
                Log.d(TAG, "Playlist Id extracted: " + playlistId);
                PlaylistItemListResponse response = YouTubeAPI.getData(playlistId, getContext());
                return response;
            } catch (Exception e) {
                Log.d(TAG, "Retrieving list failed");
                e.printStackTrace();
            }

        }
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addMileStone(int linearLayoutId, String text) {
        LinearLayout linearLayout = ((Activity) getContext()).findViewById(linearLayoutId);

        Context context = getContext();
        markAsCompletedOrUncompleted(true);
        EditText editText = new EditText(context);
        editText.setTextColor(getContext().getResources().getColor(R.color.colorText, null));
        taskEditTexts.add(editText);
        editText.setSingleLine(true);
        editText.setText(text);
        MaterialButton removeButton = new MaterialButton(context);
        removeButton.setText("-");

        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.addView(editText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        rowLayout.addView(removeButton);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(rowLayout, linearLayout.getChildCount() - 3);
        removeButton.setOnClickListener(view1 -> linearLayout.removeView(rowLayout));
        editText.requestFocus();
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
                tasks.add(taskEditTexts.get(i).getText().toString().trim());
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

    private static class ParamsAsync {
        Dialog dialog;
        Integer[] ids;

        public ParamsAsync(Dialog dialog, Integer[] ids) {
            this.dialog = dialog;
            this.ids = ids;
        }

        public Dialog getDialog() {
            return dialog;
        }

        public Integer[] getIds() {
            return ids;
        }
    }

    private class YouTubeListDownloader extends AsyncTask<ParamsAsync, Void, PlaylistItemListResponse> {
        Integer[] ids;

        @Override
        protected PlaylistItemListResponse doInBackground(ParamsAsync... params) {
            this.ids = params[0].getIds();
            return importYouTube(this.ids[0], params[0].getDialog());
        }

        @Override
        protected void onPostExecute(PlaylistItemListResponse playlistItemListResponse) {
            super.onPostExecute(playlistItemListResponse);
            try {
                Log.d(TAG, "Number of Videos Found: " + playlistItemListResponse.getItems().size());
                for (int i = 0; i < playlistItemListResponse.getItems().size(); i++) {
                    String title = playlistItemListResponse.getItems().get(i).getSnippet().getTitle();
                    if (i == 0) taskEditTexts.get(0).setText(title);
                    else addMileStone(ids[1], title);
                }
            } catch (Exception ignored) {
                Log.d(TAG, "Zero Videos");
            }
        }
    }
}