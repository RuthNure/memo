package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MemoDataSource {

    private SQLiteDatabase database;
    private MemoDatabaseHelper dbHelper;

    public MemoDataSource(Context context) {
        dbHelper = new MemoDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public boolean insertMemo(Memo memo) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("subject", memo.getSubject());
            initialValues.put("description", memo.getDescription());
            initialValues.put("priority", memo.getPriority());
            initialValues.put("date", memo.getDate());

            long result = database.insert("memos", null, initialValues);
            didSucceed = result != -1;

            Log.d("MemoDatabase", "Insert result: " + result);
        } catch (Exception e) {
            Log.e("MemoDatabaseError", "Error inserting memo", e);
        }
        return didSucceed;
    }

    public boolean updateMemo(Memo memo) {
        boolean didSucceed = false;
        try {
            int id = memo.getId();
            ContentValues updateValues = new ContentValues();
            updateValues.put("subject", memo.getSubject());
            updateValues.put("description", memo.getDescription());
            updateValues.put("priority", memo.getPriority());
            updateValues.put("date", memo.getDate());

            didSucceed = database.update("memos", updateValues, "_id=" + id, null) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return didSucceed;
    }

    public int getLastID() {
        int lastId;
        try {
            String query = "SELECT MAX(_id) FROM memos";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }

    public boolean deleteMemo(int id) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("memos", "_id=?", new String[]{String.valueOf(id)}) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return didDelete;
    }

    public List<Memo> getAllMemos() {
        ArrayList<Memo> memoList = new ArrayList<>();

        try {
            Cursor cursor = database.query("memos", null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Memo memo = new Memo();
                    memo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                    memo.setSubject(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
                    memo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    memo.setPriority(cursor.getString(cursor.getColumnIndexOrThrow("priority")));
                    memo.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    memoList.add(memo);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("MemoDataSource", "Error retrieving memos", e);
        }

        return memoList;
    }

    public ArrayList<Memo> getMemos(String sortField, String sortOrder) {
        ArrayList<Memo> memos = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM memos ORDER BY " + sortField + " " + sortOrder;
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Memo newMemo = new Memo();
                    newMemo.setId(cursor.getInt(0));
                    newMemo.setSubject(cursor.getString(1));
                    newMemo.setDescription(cursor.getString(2));
                    newMemo.setPriority(cursor.getString(3));
                    newMemo.setDate(cursor.getString(4));
                    memos.add(newMemo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("MemoDBSource", "Error getting memos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return memos;
    }

    public Memo getMemoById(int id) {
        Memo memo = null;
        try {
            String query = "SELECT * FROM memos WHERE _id = ?";
            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                memo = new Memo();
                memo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                memo.setSubject(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
                memo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                memo.setPriority(cursor.getString(cursor.getColumnIndexOrThrow("priority")));
                memo.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memo;
    }

    public ArrayList<Memo> searchMemos(String keyword) {
        ArrayList<Memo> memoList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM memos WHERE subject LIKE ? OR description LIKE ?";
            String[] args = new String[]{"%" + keyword + "%", "%" + keyword + "%"};

            cursor = database.rawQuery(query, args);

            if (cursor.moveToFirst()) {
                do {
                    Memo memo = new Memo();
                    memo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                    memo.setSubject(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
                    memo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    memo.setPriority(cursor.getString(cursor.getColumnIndexOrThrow("priority")));
                    memo.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));

                    memoList.add(memo);
                } while (cursor.moveToNext());
            }

            Log.d("MemoDBSource", "Search successful. Found " + memoList.size() + " results.");
        } catch (Exception e) {
            Log.e("MemoDBSource", "Error searching memos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return memoList;
    }
}
