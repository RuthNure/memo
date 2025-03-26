package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemoAdapter memoAdapter;
    private MemoDataSource dataSource;
    private List<Memo> memoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataSource = new MemoDataSource(this);
        dataSource.open();

        memoList = dataSource.getAllMemos();
        memoAdapter = new MemoAdapter(memoList);
        recyclerView.setAdapter(memoAdapter);
    }

    // ✅ NEW: Receive sort option result from MemoSettingsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String sortOption = data.getStringExtra("SORT_OPTION");
            if (sortOption != null) {
                sortMemos(sortOption);
            }
        }
    }

    // ✅ NEW: Sort memos based on selected option
    private void sortMemos(String option) {
        switch (option) {
            case "Subject":
                Collections.sort(memoList, Comparator.comparing(Memo::getSubject));
                break;
            case "Priority":
                Collections.sort(memoList, Comparator.comparing(Memo::getPriority));
                break;
            case "Date":
                Collections.sort(memoList, Comparator.comparing(Memo::getDate));
                break;
        }

        memoAdapter.notifyDataSetChanged();
    }

    // ✅ Optional: Call this when user clicks settings icon
    private void openSettings() {
        Intent intent = new Intent(MemoListActivity.this, MemoSettingsActivity.class);
        startActivityForResult(intent, 1);
    }
}
