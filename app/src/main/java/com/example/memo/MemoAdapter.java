package com.example.memo;/*package com.example.memo;

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
*/
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> memoList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MemoAdapter(Context context, List<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_list_item, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memoList.get(position);

        holder.memoTitleTextView.setText(memo.getSubject());
        // Truncate description
        String description = memo.getDescription();
        if (description.length() > 50) {
            description = description.substring(0, 50) + "...";
        }

        holder.memoDescriptionTextView.setText(description);
        holder.memoDateTextView.setText(memo.getDate());

        // Set priority color
        switch (memo.getPriority().toLowerCase()) {
            case "high":
                holder.priorityIndicatorView.setBackgroundColor(Color.RED);
                break;
            case "medium":
                holder.priorityIndicatorView.setBackgroundColor(Color.YELLOW);
                break;
            case "low":
                holder.priorityIndicatorView.setBackgroundColor(Color.GREEN);
                break;
            default:
                holder.priorityIndicatorView.setBackgroundColor(Color.GRAY);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("memoId", memo.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView memoTitleTextView;
        TextView memoDescriptionTextView;
        TextView memoDateTextView;
        View priorityIndicatorView;

        public MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            memoTitleTextView = itemView.findViewById(R.id.memoTitleTextView);
            memoDescriptionTextView = itemView.findViewById(R.id.memoDescriptionTextView);
            memoDateTextView = itemView.findViewById(R.id.memoDateTextView);
            priorityIndicatorView = itemView.findViewById(R.id.priorityIndicatorView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}