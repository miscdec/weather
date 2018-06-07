package com.opweather.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;

public abstract class IgnorCursorAdapter extends CursorAdapter {
    private SparseIntArray ingioPositionMap;
    private Context mContext;
    private HashSet<Long> mDeleteItemThreadIdList;

    public IgnorCursorAdapter(Context context, Cursor c) {
        this(context, c, false);
        mContext = context;
    }

    public IgnorCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mDeleteItemThreadIdList = new HashSet<>();
    }

    public IgnorCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItem(position) == null) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mCursor);
        return v;
    }

    public void clearDelete() {
        setDeleteItemThreadIdList(null);
    }

    public void delete(long id) {
        mDeleteItemThreadIdList.add(id);
        setDeleteItemThreadIdList(mDeleteItemThreadIdList);
        notifyDataSetChanged();
    }

    public void setDeleteItemThreadIdList(HashSet<Long> deleteItemThreadIdList) {
        if (deleteItemThreadIdList == null) {
            mDeleteItemThreadIdList = new HashSet<>();
            ingioPositionMap = null;
            return;
        }
        ingioPositionMap = new SparseIntArray();
        mDeleteItemThreadIdList = deleteItemThreadIdList;
        ingioPositionMap.put(0, 0);
        for (int i = 0; i < getCount(); i++) {
            int preOffsetPositon = ingioPositionMap.get(i - 1) + 1;
            if (i == 0) {
                preOffsetPositon = 0;
            }
            while (preOffsetPositon < getRealCount()) {
                Cursor cursor = (Cursor) getRealPositionItem(preOffsetPositon);
                if (!mDeleteItemThreadIdList.contains(cursor.getLong(cursor.getColumnIndex("_id")))) {
                    break;
                }
                preOffsetPositon++;
            }
            ingioPositionMap.put(i, preOffsetPositon);
        }
    }

    @Override
    public int getCount() {
        return super.getCount() - mDeleteItemThreadIdList.size();
    }

    @Override
    public Object getItem(int position) {
        int offsetPosition = position;
        if (ingioPositionMap != null) {
            offsetPosition = ingioPositionMap.get(position);
        }
        return super.getItem(offsetPosition);
    }

    public int getRealCount() {
        return super.getCount();
    }

    public Object getRealPositionItem(int position) {
        return super.getItem(position);
    }
}
