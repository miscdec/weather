package com.opweather.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST = 0;
    public static final int VERTICAL_LIST = 1;
    private int mOrientation;
    private int mPadding;
    private int mSpace;

    public SpacesItemDecoration(int orientation, int padding, int space) {
        mPadding = padding;
        mSpace = space;
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == 1) {
            mOrientation = orientation;
            return;
        }
        throw new IllegalArgumentException("invalid orientation");
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == 1) {
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.set(HORIZONTAL_LIST, mPadding, HORIZONTAL_LIST, HORIZONTAL_LIST);
            } else if (parent.getChildLayoutPosition(view) == state.getItemCount() - 1) {
                outRect.set(HORIZONTAL_LIST, mSpace, HORIZONTAL_LIST, mPadding);
            } else {
                outRect.set(HORIZONTAL_LIST, mSpace, HORIZONTAL_LIST, HORIZONTAL_LIST);
            }
        } else if (parent.getChildLayoutPosition(view) == 0) {
            outRect.set(mPadding, HORIZONTAL_LIST, HORIZONTAL_LIST, HORIZONTAL_LIST);
        } else if (parent.getChildLayoutPosition(view) == state.getItemCount() - 1) {
            outRect.set(mSpace, HORIZONTAL_LIST, mPadding, HORIZONTAL_LIST);
        } else {
            outRect.set(mSpace, HORIZONTAL_LIST, HORIZONTAL_LIST, HORIZONTAL_LIST);
        }
    }
}
