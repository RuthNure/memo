package com.example.memo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Memo> memos;
    private MemoAdapter memoAdapter;
    private MemoDataSource dataSource;

    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memoList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initNewMemo();
        openSettings();

        addButton = findViewById(R.id.buttonAddMemo);

        dataSource = new MemoDataSource(this);

        loadMemos();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Memo memo = memos.get(position);
                dataSource.deleteMemo(memo.getId());
                memos.remove(position);
                memoAdapter.notifyItemRemoved(position);
                Toast.makeText(MemoListActivity.this, "Memo Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void updateRecyclerView() {
        if (memoAdapter == null) {
            memoAdapter = new MemoAdapter(memos, this);
            memoAdapter.setOnItemClickListener(position -> {
                Memo selectedMemo = memos.get(position);
                Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
                intent.putExtra("memoID", selectedMemo.getId());
                startActivity(intent);
            });
            recyclerView.setAdapter(memoAdapter);
        } else {
            memoAdapter.setMemos(memos);
            memoAdapter.notifyDataSetChanged();
        }
    }

    private void loadMemos() {
        String sortBy = getSharedPreferences("MyMemoPreferences", MODE_PRIVATE)
                .getString("sortfield", "date");
        String sortOrder = getSharedPreferences("MyMemoPreferences", MODE_PRIVATE)
                .getString("sortorder", "ASC");

        try {
            dataSource.open();
            memos = dataSource.getMemos(sortBy, sortOrder);
            updateRecyclerView();
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving memos", Toast.LENGTH_LONG).show();
        } finally {
            dataSource.close();
        }
    }

    private void openSettings() {
        ImageButton settingsButton = findViewById(R.id.buttonSettings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MemoListActivity.this, MemoSettingsActivity.class);
            startActivity(intent);
        });
    }

    public void initNewMemo() {
        ImageButton buttonNewMemo = findViewById(R.id.buttonNewMemo);
        buttonNewMemo.setOnClickListener(v -> {
            Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataSource != null) {
            dataSource.close();
        }
    }
}