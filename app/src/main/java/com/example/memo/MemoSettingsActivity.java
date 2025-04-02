package com.example.memo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoSettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupSortBy, radioGroupPriorityFilter;
    private EditText editTextSearchResults;

    private String selectedSortOption = "Date";
    private String selectedPriorityFilter = "";
    private static final String PREFS_NAME = "MemoPrefs";
    private static final String KEY_SORT_OPTION = "sort_option";
    private static final String KEY_PRIORITY_FILTER = "priority_filter";
    private static final String KEY_SEARCH_QUERY = "search_query";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_settings);

        radioGroupSortBy = findViewById(R.id.radioGroupSortBy);
        radioGroupPriorityFilter = findViewById(R.id.radioGroupPriorityFilter);
        editTextSearchResults = findViewById(R.id.editTextSearchResults);
        ImageButton buttonList = findViewById(R.id.buttonList);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonSearch = findViewById(R.id.buttonSearch);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedSortOption = prefs.getString(KEY_SORT_OPTION, "Date");
        selectedPriorityFilter = prefs.getString(KEY_PRIORITY_FILTER, "");
        Log.d("PrefsLoad", "Loaded priority filter: " + selectedPriorityFilter);

        String savedKeyword = prefs.getString(KEY_SEARCH_QUERY, "");
        editTextSearchResults.setText(savedKeyword);


        if ("Subject".equals(selectedSortOption)) {
            radioGroupSortBy.check(R.id.radioSubject);
        } else if ("Priority".equals(selectedSortOption)) {
            radioGroupSortBy.check(R.id.radioPriority); // 👈 Make sure this ID exists
        } else {
            radioGroupSortBy.check(R.id.radioDate);
        }



        switch (selectedPriorityFilter) {
            case "Low":
                radioGroupPriorityFilter.check(R.id.radioFilterLow);
                break;
            case "Medium":
                radioGroupPriorityFilter.check(R.id.radioFilterMedium);
                break;
            case "High":
                radioGroupPriorityFilter.check(R.id.radioFilterHigh);
                break;
        }


        radioGroupSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioSubject) {
                selectedSortOption = "Subject";
            } else if (checkedId == R.id.radioPriority) {
                selectedSortOption = "Priority";
            } else if (checkedId == R.id.radioDate) {
                selectedSortOption = "Date";
            }
            Log.d("SortSelection", "Selected sort: " + selectedSortOption);
        });


        // Priority selection listener
        radioGroupPriorityFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioFilterLow) {
                selectedPriorityFilter = "Low";
            } else if (checkedId == R.id.radioFilterMedium) {
                selectedPriorityFilter = "Medium";
            } else if (checkedId == R.id.radioFilterHigh) {
                selectedPriorityFilter = "High";
            }
            Log.d("PrioritySelection", "Selected: " + selectedPriorityFilter);

        });

        buttonList.setOnClickListener(view -> {
            String keyword = editTextSearchResults.getText().toString().trim();

            Log.d("PrefsSave", "Saving priority filter: " + selectedPriorityFilter);
            prefs.edit()
                    .putString(KEY_SORT_OPTION, selectedSortOption)
                    .putString(KEY_PRIORITY_FILTER, selectedPriorityFilter)
                    .putString(KEY_SEARCH_QUERY, keyword)
                    .apply();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("SORT_OPTION", selectedSortOption);
            resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
            resultIntent.putExtra("SEARCH_QUERY", keyword);
            setResult(RESULT_OK, resultIntent);
            finish();
        });


        buttonSearch.setOnClickListener(view -> {
            String keyword = editTextSearchResults.getText().toString().trim();
            if (TextUtils.isEmpty(keyword)) {
                Toast.makeText(this, "Please enter a search keyword", Toast.LENGTH_SHORT).show();
                return;
            }


            prefs.edit()
                    .putString(KEY_SORT_OPTION, selectedSortOption)
                    .putString(KEY_PRIORITY_FILTER, selectedPriorityFilter)
                    .putString(KEY_SEARCH_QUERY, keyword)
                    .apply();


            Intent resultIntent = new Intent();
            resultIntent.putExtra("SORT_OPTION", selectedSortOption);
            resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
            resultIntent.putExtra("SEARCH_QUERY", keyword);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        buttonSettings.setOnClickListener(view ->
                Toast.makeText(this, "Already on Settings", Toast.LENGTH_SHORT).show()
        );
    }

/*
    private void sendResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SORT_OPTION", selectedSortOption);
        resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
        resultIntent.putExtra("SEARCH_QUERY", editTextSearchResults.getText().toString().trim());
        setResult(RESULT_OK, resultIntent);
        finish();
    }*/

    private void sendResultAndFinish() {
        String keyword = editTextSearchResults.getText().toString().trim();

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_SORT_OPTION, selectedSortOption)
                .putString(KEY_PRIORITY_FILTER, selectedPriorityFilter)
                .putString(KEY_SEARCH_QUERY, keyword)
                .apply();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("SORT_OPTION", selectedSortOption);
        resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
        resultIntent.putExtra("SEARCH_QUERY", keyword);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
