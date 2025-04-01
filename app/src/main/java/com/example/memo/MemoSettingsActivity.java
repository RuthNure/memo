package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoSettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupSortBy, radioGroupPriorityFilter;
    private EditText editTextSearchResults;
    private String selectedSortOption = "Date"; // Default sorting option
    private String selectedPriorityFilter = "";

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

        buttonList.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SORT_OPTION", selectedSortOption);
            resultIntent.putExtra("SEARCH_QUERY", editTextSearchResults.getText().toString().trim());
            resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        buttonSettings.setOnClickListener(view ->
                Toast.makeText(MemoSettingsActivity.this, "Already on Settings", Toast.LENGTH_SHORT).show()
        );

        radioGroupSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPriority) {
                selectedSortOption = "Priority";
            } else if (checkedId == R.id.radioDate) {
                selectedSortOption = "Date";
            } else if (checkedId == R.id.radioSubject) {
                selectedSortOption = "Subject";
            }
        });

        radioGroupPriorityFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioFilterLow) {
                selectedPriorityFilter = "Low";
            } else if (checkedId == R.id.radioFilterMedium) {
                selectedPriorityFilter = "Medium";
            } else if (checkedId == R.id.radioFilterHigh) {
                selectedPriorityFilter = "High";
            }
        });

        buttonSearch.setOnClickListener(view -> {
            String searchQuery = editTextSearchResults.getText().toString().trim();

            if (TextUtils.isEmpty(searchQuery)) {
                Toast.makeText(this, "Please enter a search keyword", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("SORT_OPTION", selectedSortOption);
            resultIntent.putExtra("SEARCH_QUERY", searchQuery);
            resultIntent.putExtra("FILTER_PRIORITY", selectedPriorityFilter);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
