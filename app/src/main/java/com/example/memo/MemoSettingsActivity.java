package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoSettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupSortBy, radioGroupPriorityFilter;
    private EditText editTextSearchResults;

    private String selectedSortOption = "Date"; // Default sort
    private String selectedPriorityFilter = ""; // Optional filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_settings);

        // Link UI elements
        radioGroupSortBy = findViewById(R.id.radioGroupSortBy);
        radioGroupPriorityFilter = findViewById(R.id.radioGroupPriorityFilter);
        editTextSearchResults = findViewById(R.id.editTextSearchResults);
        ImageButton buttonList = findViewById(R.id.buttonList);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonSearch = findViewById(R.id.buttonSearch);

        // ⬇️ Handle Sort Selection
        radioGroupSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioSubject) {
                selectedSortOption = "Subject";
            } else if (checkedId == R.id.radioDate) {
                selectedSortOption = "Date";
            }
        });

        // ⬇️ Handle Priority Filter Selection
        radioGroupPriorityFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioFilterLow) {
                selectedPriorityFilter = "Low";
            } else if (checkedId == R.id.radioFilterMedium) {
                selectedPriorityFilter = "Medium";
            } else if (checkedId == R.id.radioFilterHigh) {
                selectedPriorityFilter = "High";
            }
        });

        // ✅ Go back to List with preferences applied
        buttonList.setOnClickListener(view -> {
            sendResultAndFinish();
        });

        // ✅ Search by keyword
        buttonSearch.setOnClickListener(view -> {
            String keyword = editTextSearchResults.getText().toString().trim();
            if (TextUtils.isEmpty(keyword)) {
                Toast.makeText(this, "Please enter a search keyword", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("SORT_OPTION", selectedSortOption);
            resultIntent.putExtra("SEARCH_QUERY", keyword);
            resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // ✅ Feedback if already in settings
        buttonSettings.setOnClickListener(view ->
                Toast.makeText(this, "Already on Settings", Toast.LENGTH_SHORT).show()
        );
    }

    private void sendResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SORT_OPTION", selectedSortOption);
        resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
        resultIntent.putExtra("SEARCH_QUERY", editTextSearchResults.getText().toString().trim());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
