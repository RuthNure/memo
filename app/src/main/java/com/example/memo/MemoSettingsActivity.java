package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoSettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupSortBy;
    private EditText editTextSearchResults;
    private String selectedSortOption = "Date"; // Default sorting option

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_settings);


        radioGroupSortBy = findViewById(R.id.radioGroupSortBy);
        editTextSearchResults = findViewById(R.id.editTextSearchResults);
        ImageButton buttonList = findViewById(R.id.buttonList);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);


        buttonList.setOnClickListener(view -> {
            Intent intent = new Intent(MemoSettingsActivity.this, MemoListActivity.class);
            startActivity(intent);
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


            Intent resultIntent = new Intent();
            resultIntent.putExtra("SORT_OPTION", selectedSortOption);
            setResult(RESULT_OK, resultIntent);
        });


        editTextSearchResults.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().trim();
                if (!searchQuery.isEmpty()) {
                    searchMemos(searchQuery);
                }
            }
        });
    }

    private void searchMemos(String query) {
        // TODO: Implement database search function and update UI accordingly
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }
}
