package com.example.memo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.widget.ImageButton;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Memo memo;
    private MemoDataSource dataSource;

    private EditText editTextSubject, editTextDescription, editTextDate;

    private MemoDatabaseHelper dbHelper;

    private RadioGroup radioGroupPriority;

    private RadioButton radioButtonLow, radioButtonMedium, radioButtonHigh;




    // ✅ FIXED: correct button types
    private ImageButton buttonList, buttonSettings;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        dataSource = new MemoDataSource(this);
        dataSource.open();

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("hailu", "hailu");
        Log.d("hailu", "hailu");


        buttonSave = findViewById(R.id.buttonSave);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        radioButtonLow = findViewById(R.id.radioButtonLow);
        radioButtonMedium = findViewById(R.id.radioButtonMedium);
        radioButtonHigh = findViewById(R.id.radioButtonHigh);
        radioGroupPriority = findViewById(R.id.radioGroupPriority);

        // 🔧 ADD THIS to wire the buttons
        buttonList = findViewById(R.id.buttonList);
        buttonSettings = findViewById(R.id.buttonSettings);

        // 🔧 ADD CLICK LOGIC
        buttonList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MemoListActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MemoSettingsActivity.class);
            startActivity(intent);
        });


        memo = new Memo();


        buttonSave.setOnClickListener(v -> {
                initSaveButton();
        initPriorityClick();
    });
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        initTextChangedEvents();

        radioGroupPriority.setOnCheckedChangeListener((group, checkedId) -> {
        initPriorityClick();});
        //String priorityLevel = getSharedPreferences("MyMemoPriority", Context.MODE_PRIVATE).getString("prioritylevel","low");

    }
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    private void initTextChangedEvents() {
        editTextSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                memo.setSubject(editTextSubject.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                memo.setDescription(editTextDescription.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                memo.setDate(editTextDate.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }


    private void initSaveButton() {
        dataSource = new MemoDataSource(MainActivity.this);
        initPriorityClick();
        boolean wasSuccessful;
        try {
            dataSource.open();

            if (memo.getId() == -1) {
                wasSuccessful = dataSource.insertMemo(memo);
                if (wasSuccessful) {
                    int newId = dataSource.getLastID();
                    memo.setId(newId);
                    Log.d("MemoSave", "Memo saved successfully: " + memo.toString());
                }
            } else {
                wasSuccessful = dataSource.updateMemo(memo);
            }
            dataSource.close();
        } catch (Exception e) {
            wasSuccessful = false;
            Log.e("MemoSaveError", "Error saving memo", e);
            e.printStackTrace();
        }
    }

    private void initPriorityClick() {
        if (radioGroupPriority != null) {
            int selectedId = radioGroupPriority.getCheckedRadioButtonId();
            if (selectedId == R.id.radioButtonLow) {
                memo.setPriority("Low");
            } else if (selectedId == R.id.radioButtonMedium) {
                memo.setPriority("Medium");
            } else if (selectedId == R.id.radioButtonHigh) {
                memo.setPriority("High");
            } else {
                memo.setPriority("Low");
            }
            Log.d("PriorityDebug", "Selected priority: " + memo.getPriority());
        } else {
            Log.e("PriorityError", "RadioGroup is null");
        }
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    editTextDate.setText(selectedDate);
                    memo.setDate(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }


}