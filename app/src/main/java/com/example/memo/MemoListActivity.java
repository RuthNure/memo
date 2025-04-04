package com.example.memo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemoAdapter memoAdapter;
    private MemoDataSource dataSource;
    private List<Memo> fullMemoList;
    private List<Memo> displayedMemoList;
    private ImageButton settingsButton;
    private ImageButton addButton;

    private Spinner sortSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        sortSpinner = findViewById(R.id.sortSpinner);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton buttonNewMemo = findViewById(R.id.buttonNewMemo);
        buttonNewMemo.setOnClickListener(v -> {
            Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
            startActivity(intent);
        });


        dataSource = new MemoDataSource(this);
        dataSource.open();

        fullMemoList = dataSource.getAllMemos();
        if (fullMemoList == null) {
            Toast.makeText(this, "Error Retrieving Memos", Toast.LENGTH_SHORT).show();
            fullMemoList = new ArrayList<>();
        }
        displayedMemoList = new ArrayList<>(fullMemoList);

        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        String savedSort = prefs.getString("sort_option", "Date");
        String savedFilter = prefs.getString("priority_filter", "");
        String savedKeyword = prefs.getString("search_query", "");


        if (!savedFilter.isEmpty()) {
            Log.d("PrefsLoad", "Applying saved priority filter: " + savedFilter);
            filterByPriority(savedFilter);
        }

        if (!savedKeyword.isEmpty()) {
            filterByKeyword(savedKeyword);
        }

        sortMemos(savedSort);


        memoAdapter = new MemoAdapter(this, displayedMemoList);
        recyclerView.setAdapter(memoAdapter);

        //sortMemos("Date");
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                sortMemos(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default fallback
                sortMemos("Date");
            }
        });


        settingsButton = findViewById(R.id.buttonSettings);
        addButton = findViewById(R.id.buttonAddMemo);

        settingsButton.setOnClickListener(v -> openSettings());

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        memoAdapter.setOnItemClickListener(position -> {
            Memo memo = displayedMemoList.get(position);
            Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
            intent.putExtra("MEMO_ID", memo.getId());
            startActivity(intent);
        });



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Memo memo = displayedMemoList.get(position);
                dataSource.deleteMemo(memo.getId());
                displayedMemoList.remove(position);
                memoAdapter.notifyItemRemoved(position);
                Toast.makeText(MemoListActivity.this, "Memo Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Always reset first!
            displayedMemoList = new ArrayList<>(fullMemoList);

            // Apply filter first
            if (data.hasExtra("FILTER_PRIORITY")) {
                String filterPriority = data.getStringExtra("FILTER_PRIORITY");
                Log.d("DEBUG_FILTER", "Priority: " + filterPriority);

                if (filterPriority != null && !filterPriority.isEmpty()) {
                    filterByPriority(filterPriority);
                }
            }

            // Then search
            if (data.hasExtra("SEARCH_QUERY")) {
                String keyword = data.getStringExtra("SEARCH_QUERY");
                if (keyword != null && !keyword.isEmpty()) {
                    filterByKeyword(keyword);
                }
            }

            // Then sort
            if (data.hasExtra("SORT_OPTION")) {
                String sortOption = data.getStringExtra("SORT_OPTION");
                sortMemos(sortOption);
            }

            memoAdapter = new MemoAdapter(this, displayedMemoList);
            recyclerView.setAdapter(memoAdapter);
        }
    }


    private void sortMemos(String option) {
        if (displayedMemoList != null && memoAdapter != null) {
            Comparator<Memo> comparator;
            switch (option) {
                case "Subject":
                    comparator = Comparator.comparing(
                            Memo::getSubject,
                            Comparator.nullsLast(String::compareToIgnoreCase)
                    );
                    break;

                case "Priority":
                    comparator = Comparator.comparing(
                            m -> getPriorityValue(m.getPriority()),
                            Comparator.nullsLast(Integer::compareTo).reversed());
                    break;

                case "Date":
                default:
                    comparator = Comparator.comparing(
                            Memo::getDate,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    );
                    break;
            }

            Collections.sort(displayedMemoList, comparator);
            memoAdapter.notifyDataSetChanged();
        }
    }


    private Integer getPriorityValue(String priority) {
        if (priority == null) return 0;
        switch (priority.toLowerCase()) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }


    private void filterByPriority(String level) {
        List<Memo> filtered = new ArrayList<>();
        for (Memo memo : fullMemoList) {
            if (memo.getPriority() != null && memo.getPriority().equalsIgnoreCase(level)) {
                filtered.add(memo);
            }
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No memos with priority: " + level, Toast.LENGTH_SHORT).show();
        }

        displayedMemoList = filtered;
    }



    private void filterByKeyword(String query) {
        List<Memo> filtered = new ArrayList<>();
        for (Memo memo : displayedMemoList) {
            if (memo.getSubject().toLowerCase().contains(query.toLowerCase()) ||
                    memo.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(memo);
            }
        }
        displayedMemoList = filtered;

    }

    private void openSettings() {
        Intent intent = new Intent(MemoListActivity.this, MemoSettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataSource != null) {
            dataSource.close();
        }
    }



}
