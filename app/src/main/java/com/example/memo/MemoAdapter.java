package com.example.memo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> memoList;

    public MemoAdapter(List<Memo> memoList) {
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_item, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        holder.textSubject.setText(memo.getSubject());
        holder.textPriority.setText("Priority: " + memo.getPriority());
        holder.textDate.setText("Date: " + memo.getDate());
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public static class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView textSubject, textPriority, textDate;

        public MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            textSubject = itemView.findViewById(R.id.textSubject);
            textPriority = itemView.findViewById(R.id.textPriority);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}
