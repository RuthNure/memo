package com.example.memo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MemoSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memo_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsXml), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initListButton();
        initSettingsButton();
        initSettings();
        initSortByClick();
        initSortPriorityClick();
        initSortSearch();
    }



    public void initListButton() {
        ImageButton imgButton = findViewById(R.id.buttonList);
        imgButton.setOnClickListener(i -> {
            Intent intent = new Intent(MemoSettingsActivity.this, MemoListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    public void initSettingsButton() {
        ImageButton imgButton = findViewById(R.id.buttonSettings);
        imgButton.setEnabled(false);
    }

    public void initSettings() {
        String sortBy = getSharedPreferences("MyMemoPreferences", Context.MODE_PRIVATE)
                .getString("sortfield", "date");
        String sortOrder = getSharedPreferences("MyMemoPreferences", Context.MODE_PRIVATE)
                .getString("sortorder", "ASC");

        RadioButton rbPriority = findViewById(R.id.radioPriority);
        RadioButton rbSubject = findViewById(R.id.radioSubject);
        RadioButton rbDate = findViewById(R.id.radioDate);

        if (sortBy.equalsIgnoreCase("priority")) {
            rbPriority.setChecked(true);
        } else if (sortBy.equalsIgnoreCase("subject")) {
            rbSubject.setChecked(true);
        } else {
            rbDate.setChecked(true);
        }

        RadioButton rbASC = findViewById(R.id.radioAsc);
        RadioButton rbDSC = findViewById(R.id.radioDesc);

        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbASC.setChecked(true);
        } else {
            rbDSC.setChecked(true);
        }
    }

    private void initSortByClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rbPriority = findViewById(R.id.radioPriority);
            RadioButton rbSubject = findViewById(R.id.radioSubject);

            SharedPreferences.Editor editor = getSharedPreferences("MyMemoPreferences", Context.MODE_PRIVATE).edit();

            if (rbPriority.isChecked()) {
                editor.putString("sortfield", "priority");
            } else if (rbSubject.isChecked()) {
                editor.putString("sortfield", "subject");
            } else {
                editor.putString("sortfield", "date");
            }

            editor.apply();
        });
    }

    private void initSortPriorityClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupOrder);
        rgSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rbASC = findViewById(R.id.radioAsc);
            RadioButton rbDSC = findViewById(R.id.radioDesc);

            SharedPreferences.Editor editor = getSharedPreferences("MyMemoPreferences", Context.MODE_PRIVATE).edit();

            if (rbASC.isChecked()) {
                editor.putString("sortorder", "ASC");
            } else if (rbDSC.isChecked()) {
                editor.putString("sortorder", "DESC");
            }

            editor.apply();
        });
    }

    private void initSortSearch() {
        Button search = findViewById(R.id.buttonSearch);
        EditText searchBar = findViewById(R.id.editTextSearchResults);

        search.setOnClickListener(s -> {
            String keyword = searchBar.getText().toString().trim();
            Log.d("SearchKeyword", "Keyword entered: " + keyword);

            if (!keyword.isEmpty()) {
                Intent intent = new Intent(MemoSettingsActivity.this, MemoListActivity.class);
                intent.putExtra("SEARCH_KEYWORD", keyword);
                startActivity(intent);
            } else {
                Toast.makeText(MemoSettingsActivity.this, "Enter a keyword!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}