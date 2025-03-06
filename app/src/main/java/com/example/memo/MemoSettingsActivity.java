package com.example.memo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MemoSettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupSortBy;

    private RadioButton radioPriority, radioSubject, radioDate;

    private Button buttonList, buttonSettings;

    private EditText editTextSearchResults;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_settings);
    }
}
