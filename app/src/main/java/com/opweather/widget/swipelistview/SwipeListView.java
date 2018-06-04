package com.opweather.widget.swipelistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.opweather.R;

import java.util.List;

public class SwipeListView extends ListView {

    public static final boolean DEBUG = false;
    public static final int SWIPE_ACTION_CHOICE = 2;
    public static final int SWIPE_ACTION_DISMISS = 1;
    public static final int SWIPE_ACTION_NONE = 3;
    public static final int SWIPE_ACTION_REVEAL = 0;
    public static final String SWIPE_DEFAULT_BACK_VIEW = "swipelist_backview";
    public static final String SWIPE_DEFAULT_FRONT_VIEW = "swipelist_frontview";
    public static final int SWIPE_MODE_BOTH = 1;
    public static final int SWIPE_MODE_DEFAULT = -1;
    public static final int SWIPE_MODE_LEFT = 3;
    public static final int SWIPE_MODE_NONE = 0;
    public static final int SWIPE_MODE_RIGHT = 2;
    public static final String TAG = "SwipeListView";
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING_X = 1;
    private static final int TOUCH_STATE_SCROLLING_Y = 2;
    private float lastMotionX;
    private float lastMotionY;
    int swipeBackView;
    int swipeFrontView;
    private SwipeListViewListener swipeListViewListener;
    private SwipeListViewTouchListener touchListener;
    private int touchSlop;
    private int touchState;

    public SwipeListView(Context context, int swipeBackView, int swipeFrontView) {
        this(context, null);
        this.swipeFrontView = swipeFrontView;
        this.swipeBackView = swipeBackView;
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int swipeMode = TOUCH_STATE_SCROLLING_X;
        boolean swipeOpenOnLongPress = true;
        boolean swipeCloseAllItemsWhenMoveList = true;
        long swipeAnimationTime = 0;
        float swipeOffsetLeft = AutoScrollHelper.RELATIVE_UNSPECIFIED;
        float swipeOffsetRight = AutoScrollHelper.RELATIVE_UNSPECIFIED;
        int swipeDrawableChecked = TOUCH_STATE_REST;
        int swipeDrawableUnchecked = TOUCH_STATE_REST;
        int swipeActionLeft = TOUCH_STATE_REST;
        int swipeActionRight = TOUCH_STATE_REST;
        if (attrs != null) {
            TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeListView);
            swipeMode = styled.getInt(R.styleable.SwipeListView_swipeMode, 1);
            swipeActionLeft = styled.getInt(R.styleable.SwipeListView_swipeActionLeft, 0);
            swipeActionRight = styled.getInt(R.styleable.SwipeListView_swipeActionRight, 0);
            swipeOffsetLeft = styled.getDimension(R.styleable.SwipeListView_swipeOffsetLeft, 0.0f);
            swipeOffsetRight = styled.getDimension(R.styleable.SwipeListView_swipeOffsetRight, 0.0f);
            swipeOpenOnLongPress = styled.getBoolean(R.styleable.SwipeListView_swipeOpenOnLongPress, true);
            swipeAnimationTime = (long) styled.getInteger(R.styleable.SwipeListView_swipeAnimationTime, 0);
            swipeCloseAllItemsWhenMoveList = styled.getBoolean(R.styleable
                    .SwipeListView_swipeCloseAllItemsWhenMoveList, true);
            swipeDrawableChecked = styled.getResourceId(R.styleable.SwipeListView_swipeDrawableChecked, 0);
            swipeDrawableUnchecked = styled.getResourceId(R.styleable.SwipeListView_swipeDrawableUnchecked, 0);
            swipeFrontView = styled.getResourceId(R.styleable.SwipeListView_swipeFrontView, 0);
            swipeBackView = styled.getResourceId(R.styleable.SwipeListView_swipeBackView, 0);
            styled.recycle();
        }
        if (swipeFrontView == 0 || swipeBackView == 0) {
            swipeFrontView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_FRONT_VIEW, "id",
                    getContext().getPackageName());
            swipeBackView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_BACK_VIEW, "id", getContext
                    ().getPackageName());
            if (swipeFrontView == 0 || swipeBackView == 0) {
                Object[] objArr = new Object[2];
                objArr[0] = SWIPE_DEFAULT_FRONT_VIEW;
                objArr[1] = SWIPE_DEFAULT_BACK_VIEW;
                throw new RuntimeException(String.format("You forgot the attributes swipeFrontView or swipeBackView. " +
                        "You can add this attributes or use '%s' and '%s' identifiers", objArr));
            }
        }
        touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(getContext()));
        touchListener = new SwipeListViewTouchListener(this, swipeFrontView, swipeBackView);
        if (swipeAnimationTime > 0) {
            touchListener.setAnimationTime(swipeAnimationTime);
        }
        touchListener.setRightOffset(swipeOffsetRight);
        touchListener.setLeftOffset(swipeOffsetLeft);
        touchListener.setSwipeActionLeft(swipeActionLeft);
        touchListener.setSwipeActionRight(swipeActionRight);
        touchListener.setSwipeMode(swipeMode);
        touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
        touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
        touchListener.setSwipeDrawableChecked(swipeDrawableChecked);
        touchListener.setSwipeDrawableUnchecked(swipeDrawableUnchecked);
        setOnTouchListener(touchListener);
        setOnScrollListener(touchListener.makeScrollListener());
    }

    public void recycle(View convertView, int position) {
        touchListener.reloadChoiceStateInView(convertView.findViewById(swipeFrontView), position);
        touchListener.reloadSwipeStateInView(convertView.findViewById(swipeFrontView), position);
        for (int j = TOUCH_STATE_REST; j < ((ViewGroup) convertView).getChildCount(); j++) {
            ((ViewGroup) convertView).getChildAt(j).setPressed(DEBUG);
        }
    }

    public boolean isChecked(int position) {
        return this.touchListener.isChecked(position);
    }

    public List<Integer> getPositionsSelected() {
        return this.touchListener.getPositionsSelected();
    }

    public int getCountSelected() {
        return this.touchListener.getCountSelected();
    }

    public void unselectedChoiceStates() {
        this.touchListener.unselectedChoiceStates();
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.touchListener.resetItems();
        if (adapter != null) {
            adapter.registerDataSetObserver(new DataSetObserver() {
                public void onChanged() {
                    super.onChanged();
                    SwipeListView.this.onListChanged();
                    SwipeListView.this.touchListener.resetItems();
                }
            });
        }
    }

    public void dismiss(int position) {
        int height = this.touchListener.dismiss(position);
        if (height > 0) {
            this.touchListener.handlerPendingDismisses(height);
            return;
        }
        onDismiss(new int[]{position});
        this.touchListener.resetPendingDismisses();
    }

    public void dismissSelected() {
        List<Integer> list = this.touchListener.getPositionsSelected();
        int[] dismissPositions = new int[list.size()];
        int height = TOUCH_STATE_REST;
        for (int i = TOUCH_STATE_REST; i < list.size(); i++) {
            int position = ((Integer) list.get(i)).intValue();
            dismissPositions[i] = position;
            int auxHeight = this.touchListener.dismiss(position);
            if (auxHeight > 0) {
                height = auxHeight;
            }
        }
        if (height > 0) {
            this.touchListener.handlerPendingDismisses(height);
        } else {
            onDismiss(dismissPositions);
            this.touchListener.resetPendingDismisses();
        }
        this.touchListener.returnOldActions();
    }

    public void openAnimate(int position) {
        this.touchListener.openAnimate(position);
    }

    public void closeAnimate(int position) {
        touchListener.closeAnimate(position);
    }

    protected void onDismiss(int[] reverseSortedPositions) {
        if (swipeListViewListener != null) {
            swipeListViewListener.onDismiss(reverseSortedPositions);
        }
    }

    protected void onStartOpen(int position, int action, boolean right) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onStartOpen(position, action, right);
        }
    }

    protected void onStartClose(int position, boolean right) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onStartClose(position, right);
        }
    }

    protected void onClickFrontView(int position) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onClickFrontView(position);
        }
    }

    protected void onClickBackView(int position) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onClickBackView(position);
        }
    }

    protected void onOpened(int position, boolean toRight) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onOpened(position, toRight);
        }
    }

    protected void onClosed(int position, boolean fromRight) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onClosed(position, fromRight);
        }
    }

    protected void onChoiceChanged(int position, boolean selected) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onChoiceChanged(position, selected);
        }
    }

    protected void onChoiceStarted() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onChoiceStarted();
        }
    }

    protected void onChoiceEnded() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onChoiceEnded();
        }
    }

    protected void onFirstListItem() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onFirstListItem();
        }
    }

    protected void onLastListItem() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onLastListItem();
        }
    }

    protected void onListChanged() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onListChanged();
        }
    }

    protected void onMove(int position, float x) {
        if (swipeListViewListener != null && position != -1) {
            swipeListViewListener.onMove(position, x);
        }
    }

    protected int changeSwipeMode(int position) {
        return (swipeListViewListener == null || position == -1) ? SWIPE_MODE_DEFAULT : swipeListViewListener
                .onChangeSwipeMode(position);
    }

    public void setSwipeListViewListener(SwipeListViewListener swipeListViewListener) {
        this.swipeListViewListener = swipeListViewListener;
    }

    public void resetScrolling() {
        touchState = 0;
    }

    public void setOffsetRight(float offsetRight) {
        touchListener.setRightOffset(offsetRight);
    }

    public void setOffsetLeft(float offsetLeft) {
        touchListener.setLeftOffset(offsetLeft);
    }

    public void setSwipeCloseAllItemsWhenMoveList(boolean swipeCloseAllItemsWhenMoveList) {
        touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
    }

    public void setSwipeOpenOnLongPress(boolean swipeOpenOnLongPress) {
        touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
    }

    public void setSwipeMode(int swipeMode) {
        touchListener.setSwipeMode(swipeMode);
    }

    public int getSwipeActionLeft() {
        return touchListener.getSwipeActionLeft();
    }

    public void setSwipeActionLeft(int swipeActionLeft) {
        touchListener.setSwipeActionLeft(swipeActionLeft);
    }

    public int getSwipeActionRight() {
        return touchListener.getSwipeActionRight();
    }

    public void setSwipeActionRight(int swipeActionRight) {
        touchListener.setSwipeActionRight(swipeActionRight);
    }

    public void setAnimationTime(long animationTime) {
        touchListener.setAnimationTime(animationTime);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        float x = ev.getX();
        float y = ev.getY();
        if (!touchListener.isSwipeEnabled()) {
            touchListener.onTouch(this, ev);
        }
        if (isEnabled() && touchListener.isSwipeEnabled()) {
            if (touchState == 1) {
                return touchListener.onTouch(this, ev);
            }
            switch (action) {
                case TOUCH_STATE_REST:
                    super.onInterceptTouchEvent(ev);
                    touchListener.onTouch(this, ev);
                    touchState = 0;
                    lastMotionX = x;
                    lastMotionY = y;
                    return false;
                case TOUCH_STATE_SCROLLING_X:
                    touchListener.onTouch(this, ev);
                    return touchState == 2;
                case TOUCH_STATE_SCROLLING_Y:
                    checkInMoving(x, y);
                    return touchState == 2;
                case SWIPE_MODE_LEFT:
                    touchState = 0;
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void checkInMoving(float x, float y) {
        boolean xMoved;
        boolean yMoved = DEBUG;
        int xDiff = (int) Math.abs(x - lastMotionX);
        int yDiff = (int) Math.abs(y - lastMotionY);
        int touchSlop = this.touchSlop;
        if (xDiff > touchSlop) {
            xMoved = true;
        } else {
            xMoved = false;
        }
        if (yDiff > touchSlop) {
            yMoved = true;
        }
        if (xMoved) {
            touchState = 1;
            lastMotionX = x;
            lastMotionY = y;
        }
        if (yMoved) {
            touchState = 2;
            lastMotionX = x;
            lastMotionY = y;
        }
    }

    public void closeOpenedItems() {
        touchListener.closeOpenedItems();
    }
}
