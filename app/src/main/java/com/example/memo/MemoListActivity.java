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

import com.example.memo.MainActivity;
import com.example.memo.Memo;
import com.example.memo.MemoAdapter;
import com.example.memo.MemoDataSource;
import com.example.memo.MemoSettingsActivity;
import com.example.memo.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemoAdapter memoAdapter;
    private MemoDataSource dataSource;
    private List<Memo> memoList;
    private ImageButton settingsButton;
    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        recyclerView = findViewById(R.id.recyclerView); // Corrected ID
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataSource = new MemoDataSource(this);
        dataSource.open();

        memoList = dataSource.getAllMemos();

        if (memoList == null) {
            Toast.makeText(this, "Error Retrieving Memos", Toast.LENGTH_SHORT).show();
            memoList = Collections.emptyList();
        }

        memoAdapter = new MemoAdapter(this, memoList);
        recyclerView.setAdapter(memoAdapter);

        settingsButton = findViewById(R.id.buttonSettings);
        addButton = findViewById(R.id.buttonAddMemo); // Corrected ID

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        memoAdapter.setOnItemClickListener(new MemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Memo memo = memoList.get(position);
                Intent intent = new Intent(MemoListActivity.this, MainActivity.class);
                intent.putExtra("MEMO_ID", memo.getId());
                startActivity(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Memo memo = memoList.get(position);
                dataSource.deleteMemo(memo.getId());
                memoList.remove(position);
                memoAdapter.notifyItemRemoved(position);
                Toast.makeText(MemoListActivity.this, "Memo Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

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

    private void sortMemos(String option) {
        if (memoList != null) {
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
                default:
                    Toast.makeText(this, "Invalid Sort Option", Toast.LENGTH_SHORT).show();
                    return;
            }
            memoAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Memo list is null", Toast.LENGTH_SHORT).show();
        }
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