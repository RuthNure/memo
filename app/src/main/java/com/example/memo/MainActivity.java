package com.example.memo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Memo memo;
    private MemoDataSource dataSource;

    private EditText editTextSubject, editTextDescription, editTextDate;

    private MemoDatabaseHelper dbHelper;

    private RadioGroup radioGroupPriority;

    private RadioButton radioButtonLow, radioButtonMedium, radioButtonHigh;




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

        memo = new Memo();
        buttonSave = findViewById(R.id.buttonSave);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);

        String today = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        editTextDate.setText(today);
        memo.setDate(today);


        radioButtonLow = findViewById(R.id.radioButtonLow);
        radioButtonMedium = findViewById(R.id.radioButtonMedium);
        radioButtonHigh = findViewById(R.id.radioButtonHigh);
        radioGroupPriority = findViewById(R.id.radioGroupPriority);


        buttonList = findViewById(R.id.buttonList);
        buttonSettings = findViewById(R.id.buttonSettings);


        buttonList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MemoListActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MemoSettingsActivity.class);
            startActivity(intent);
        });






        buttonSave.setOnClickListener(v -> {
                initSaveButton();
        initPriorityClick();
    });
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        initTextChangedEvents();

        radioGroupPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initPriorityClick();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("memoId")) {
            int memoId = intent.getIntExtra("memoId", -1);

            if (memoId != -1) {
                dataSource.open();
                memo = dataSource.getMemoById(memoId);
                dataSource.close();

                if (memo != null) {
                    editTextSubject.setText(memo.getSubject());
                    editTextDescription.setText(memo.getDescription());
                    editTextDate.setText(memo.getDate());


                    if (memo.getPriority() != null) {
                        switch (memo.getPriority().toLowerCase()) {
                            case "high": radioButtonHigh.setChecked(true); break;
                            case "medium": radioButtonMedium.setChecked(true); break;
                            case "low": radioButtonLow.setChecked(true); break;
                        }
                    }
                }
            }
        } else {
            memo = new Memo();
            enableEditingFields();
        }






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
        String subject = editTextSubject.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (subject.isEmpty() || description.isEmpty()) {
            Snackbar.make(findViewById(R.id.main),
                    "Memo Title and Description are required!",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        memo.setSubject(subject);
        memo.setDescription(description);
        initPriorityClick();

        boolean wasSuccessful;
        try {
            dataSource.open();

            if (memo.getId() == -1) {
                wasSuccessful = dataSource.insertMemo(memo);
                if (wasSuccessful) {
                    int newId = dataSource.getLastID();
                    memo.setId(newId);
                    Snackbar.make(findViewById(R.id.main),
                            "Memo saved successfully!",
                            Snackbar.LENGTH_SHORT).show();
                }
            } else {
                wasSuccessful = dataSource.updateMemo(memo);
                if (wasSuccessful) {
                    Snackbar.make(findViewById(R.id.main),
                            "Memo updated successfully!",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
            if (wasSuccessful) {
                int newId = dataSource.getLastID();
                memo.setId(newId);
                Snackbar.make(findViewById(R.id.main),
                        "Memo saved successfully!",
                        Snackbar.LENGTH_SHORT).show();

                disableEditingFields();
            }

            dataSource.close();
        } catch (Exception e) {
            wasSuccessful = false;
            Log.e("MemoSaveError", "Error saving memo", e);
            Snackbar.make(findViewById(R.id.main),
                    "Error saving memo!",
                    Snackbar.LENGTH_SHORT).show();
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
                this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        editTextDate.setText(selectedDate);
                        memo.setDate(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void disableEditingFields() {
        editTextSubject.setEnabled(false);
        editTextDescription.setEnabled(false);
        editTextDate.setEnabled(false);

        radioButtonLow.setEnabled(false);
        radioButtonMedium.setEnabled(false);
        radioButtonHigh.setEnabled(false);

    }
    private void enableEditingFields() {
        editTextSubject.setEnabled(true);
        editTextDescription.setEnabled(true);
        editTextDate.setEnabled(true);

        radioButtonLow.setEnabled(true);
        radioButtonMedium.setEnabled(true);
        radioButtonHigh.setEnabled(true);

        buttonSave.setEnabled(true);
    }




}