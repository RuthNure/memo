package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataSource = new MemoDataSource(this);
        dataSource.open();

        fullMemoList = dataSource.getAllMemos();
        if (fullMemoList == null) {
            Toast.makeText(this, "Error Retrieving Memos", Toast.LENGTH_SHORT).show();
            fullMemoList = new ArrayList<>();
        }
        displayedMemoList = new ArrayList<>(fullMemoList);

        sortMemos("Date");

        memoAdapter = new MemoAdapter(this, displayedMemoList);
        recyclerView.setAdapter(memoAdapter);

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
            displayedMemoList = new ArrayList<>(fullMemoList);

            if (data.hasExtra("FILTER_PRIORITY")) {
                String filterPriority = data.getStringExtra("FILTER_PRIORITY");
                if (filterPriority != null && !filterPriority.isEmpty()) {
                    filterByPriority(filterPriority);
                }
            }

            if (data.hasExtra("SEARCH_QUERY")) {
                String keyword = data.getStringExtra("SEARCH_QUERY");
                if (keyword != null && !keyword.isEmpty()) {
                    filterByKeyword(keyword);
                }
            }

            if (data.hasExtra("SORT_OPTION")) {
                String sortOption = data.getStringExtra("SORT_OPTION");
                sortMemos(sortOption);
            }

            memoAdapter = new MemoAdapter(this, displayedMemoList);
            recyclerView.setAdapter(memoAdapter);
        }
    }

    private void sortMemos(String option) {
        if (displayedMemoList != null) {
            Comparator<Memo> comparator;
            switch (option) {
                case "Subject":
                    comparator = Comparator.comparing(Memo::getSubject);
                    break;
                case "Priority":
                    comparator = Comparator.comparing(m -> getPriorityValue(m.getPriority()));
                    break;
                case "Date":
                default:
                    comparator = Comparator.comparing(Memo::getDate);
                    break;
            }
            Collections.sort(displayedMemoList, comparator);
            memoAdapter.notifyDataSetChanged();
        }
    }

    private int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "low": return 1;
            case "medium": return 2;
            case "high": return 3;
            default: return 0;
        }
    }

    private void filterByPriority(String level) {
        List<Memo> filtered = new ArrayList<>();
        for (Memo memo : displayedMemoList) {
            if (memo.getPriority().equalsIgnoreCase(level)) {
                filtered.add(memo);
            }
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
