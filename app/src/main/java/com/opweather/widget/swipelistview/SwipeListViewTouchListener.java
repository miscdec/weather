package com.opweather.widget.swipelistview;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.DefaultRetryPolicy;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.OrigamiValueConverter;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.opweather.R;
import com.opweather.widget.openglbase.RainSurfaceView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeListViewTouchListener implements OnTouchListener {
    private static final int DISPLACE_CHOICE = 80;
    private long animationTime;
    private View backView;
    private List<Boolean> checked;
    private long configShortAnimationTime;
    private int dismissAnimationRefCount;
    private int downPosition;
    private float downX;
    private View frontView;
    private float leftOffset;
    private boolean listViewMoving;
    private Spring mSpring;
    private SpringConfig mSpringConfig;
    private final BaseSpringSystem mSpringSystem;
    private int maxFlingVelocity;
    private int minFlingVelocity;
    private int oldSwipeActionLeft;
    private int oldSwipeActionRight;
    private List<Boolean> opened;
    private List<Boolean> openedRight;
    private View parentView;
    private boolean paused;
    private List<PendingDismissData> pendingDismisses;
    private Rect rect;
    private float rightOffset;
    private int slop;
    private int swipeActionLeft;
    private int swipeActionRight;
    private int swipeBackView;
    private boolean swipeClosesAllItemsWhenListMoves;
    private int swipeCurrentAction;
    private int swipeDrawableChecked;
    private int swipeDrawableUnchecked;
    private int swipeFrontView;
    private SwipeListView swipeListView;
    private int swipeMode;
    private boolean swipeOpenOnLongPress;
    private boolean swiping;
    private boolean swipingRight;
    private VelocityTracker velocityTracker;
    private int viewWidth;

    class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public View view;

        public PendingDismissData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        public int compareTo(PendingDismissData other) {
            return other.position - this.position;
        }
    }

    public SwipeListViewTouchListener(SwipeListView swipeListView, int swipeFrontView, int swipeBackView) {
        this.mSpringSystem = SpringSystem.create();
        this.swipeMode = 1;
        this.swipeOpenOnLongPress = true;
        this.swipeClosesAllItemsWhenListMoves = true;
        this.swipeFrontView = 0;
        this.swipeBackView = 0;
        this.rect = new Rect();
        this.leftOffset = 0.0f;
        this.rightOffset = 0.0f;
        this.swipeDrawableChecked = 0;
        this.swipeDrawableUnchecked = 0;
        this.viewWidth = 1;
        this.pendingDismisses = new ArrayList();
        this.dismissAnimationRefCount = 0;
        this.swipeCurrentAction = 3;
        this.swipeActionLeft = 0;
        this.swipeActionRight = 0;
        this.opened = new ArrayList();
        this.openedRight = new ArrayList();
        this.checked = new ArrayList();
        this.swipeFrontView = swipeFrontView;
        this.swipeBackView = swipeBackView;
        ViewConfiguration vc = ViewConfiguration.get(swipeListView.getContext());
        this.slop = vc.getScaledTouchSlop();
        this.minFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        this.configShortAnimationTime = (long) swipeListView.getContext().getResources().getInteger(R.integer
                .app_bar_elevation_anim_duration);
        this.animationTime = this.configShortAnimationTime;
        this.swipeListView = swipeListView;
        this.mSpring = this.mSpringSystem.createSpring();
        this.mSpringConfig = SpringConfig.defaultConfig;
        this.mSpringConfig.tension = OrigamiValueConverter.tensionFromOrigamiValue(50.0d);
        this.mSpringConfig.friction = OrigamiValueConverter.frictionFromOrigamiValue(7.0d);
        this.mSpring.setSpringConfig(this.mSpringConfig);
    }

    private void setParentView(View parentView) {
        this.parentView = parentView;
    }

    private void setFrontView(View frontView, final int childPosition) {
        this.frontView = frontView;
        frontView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                swipeListView.onClickFrontView(SwipeListViewTouchListener.this
                        .downPosition);
            }
        });
        frontView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!swipeOpenOnLongPress) {
                    swapChoiceState(childPosition);
                } else if (downPosition >= 0) {
                    openAnimate(childPosition);
                }
                return false;
            }
        });
    }

    private void setBackView(View backView) {
        this.backView = backView;
        backView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                swipeListView.onClickBackView(downPosition);
            }
        });
    }

    public boolean isListViewMoving() {
        return this.listViewMoving;
    }

    public void setAnimationTime(long animationTime) {
        if (animationTime > 0) {
            this.animationTime = animationTime;
        } else {
            this.animationTime = configShortAnimationTime;
        }
    }

    public void setRightOffset(float rightOffset) {
        this.rightOffset = rightOffset;
    }

    public void setLeftOffset(float leftOffset) {
        this.leftOffset = leftOffset;
    }

    public void setSwipeClosesAllItemsWhenListMoves(boolean swipeClosesAllItemsWhenListMoves) {
        this.swipeClosesAllItemsWhenListMoves = swipeClosesAllItemsWhenListMoves;
    }

    public void setSwipeOpenOnLongPress(boolean swipeOpenOnLongPress) {
        this.swipeOpenOnLongPress = swipeOpenOnLongPress;
    }

    public void setSwipeMode(int swipeMode) {
        this.swipeMode = swipeMode;
    }

    protected boolean isSwipeEnabled() {
        return this.swipeMode != 0;
    }

    public int getSwipeActionLeft() {
        return this.swipeActionLeft;
    }

    public void setSwipeActionLeft(int swipeActionLeft) {
        this.swipeActionLeft = swipeActionLeft;
    }

    public int getSwipeActionRight() {
        return this.swipeActionRight;
    }

    public void setSwipeActionRight(int swipeActionRight) {
        this.swipeActionRight = swipeActionRight;
    }

    protected void setSwipeDrawableChecked(int swipeDrawableChecked) {
        this.swipeDrawableChecked = swipeDrawableChecked;
    }

    protected void setSwipeDrawableUnchecked(int swipeDrawableUnchecked) {
        this.swipeDrawableUnchecked = swipeDrawableUnchecked;
    }

    public void resetItems() {
        if (swipeListView.getAdapter() != null) {
            int count = swipeListView.getAdapter().getCount();
            for (int i = opened.size(); i <= count; i++) {
                opened.add(false);
                openedRight.add(false);
                checked.add(false);
            }
        }
    }

    protected void openAnimate(int position) {
        View child = swipeListView.getChildAt(position - swipeListView.getFirstVisiblePosition())
                .findViewById(swipeFrontView);
        if (child != null) {
            openAnimate(child, position);
        }
    }

    protected void closeAnimate(int position) {
        if (swipeListView != null) {
            View childContainer = swipeListView.getChildAt(position - swipeListView.getFirstVisiblePosition
                    ());
            if (childContainer != null) {
                View child = childContainer.findViewById(swipeFrontView);
                if (child != null) {
                    closeAnimate(child, position);
                }
            }
        }
    }

    private void swapChoiceState(int position) {
        boolean z;
        boolean z2 = true;
        int lastCount = getCountSelected();
        boolean lastChecked = checked.get(position);
        List list = checked;
        if (lastChecked) {
            z = false;
        } else {
            z = true;
        }
        list.set(position, z);
        int count = lastChecked ? lastCount - 1 : lastCount + 1;
        if (lastCount == 0 && count == 1) {
            swipeListView.onChoiceStarted();
            closeOpenedItems();
            setActionsTo(RainSurfaceView.RAIN_LEVEL_SHOWER);
        }
        if (lastCount == 1 && count == 0) {
            this.swipeListView.onChoiceEnded();
            returnOldActions();
        }
        if (VERSION.SDK_INT >= 11) {
            SwipeListView swipeListView = this.swipeListView;
            if (lastChecked) {
                z = false;
            } else {
                z = true;
            }
            swipeListView.setItemChecked(position, z);
        }
        SwipeListView swipeListView2 = swipeListView;
        if (lastChecked) {
            z2 = false;
        }
        swipeListView2.onChoiceChanged(position, z2);
        reloadChoiceStateInView(this.frontView, position);
    }

    protected void unselectedChoiceStates() {
        int start = swipeListView.getFirstVisiblePosition();
        int end = swipeListView.getLastVisiblePosition();
        int i = 0;
        while (i < checked.size()) {
            if (checked.get(i) && i >= start && i <= end) {
                reloadChoiceStateInView(swipeListView.getChildAt(i - start).findViewById(swipeFrontView), i);
            }
            checked.set(i, false);
            i++;
        }
        swipeListView.onChoiceEnded();
        returnOldActions();
    }

    protected int dismiss(int position) {
        this.opened.remove(position);
        this.checked.remove(position);
        int start = swipeListView.getFirstVisiblePosition();
        int end = swipeListView.getLastVisiblePosition();
        View view = swipeListView.getChildAt(position - start);
        this.dismissAnimationRefCount++;
        if (position < start || position > end) {
            pendingDismisses.add(new PendingDismissData(position, null));
            return 0;
        }
        performDismiss(view, position, false);
        return view.getHeight();
    }

    protected void reloadChoiceStateInView(View frontView, int position) {
        if (isChecked(position)) {
            if (swipeDrawableChecked > 0) {
                frontView.setBackgroundResource(swipeDrawableChecked);
            }
        } else if (swipeDrawableUnchecked > 0) {
            frontView.setBackgroundResource(swipeDrawableUnchecked);
        }
    }

    protected void reloadSwipeStateInView(View frontView, int position) {
        if (!opened.get(position)) {
            ViewHelper.setTranslationX(frontView, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        } else if (openedRight.get(position)) {
            ViewHelper.setTranslationX(frontView, (float) swipeListView.getWidth());
        } else {
            ViewHelper.setTranslationX(frontView, (float) (-swipeListView.getWidth()));
        }
    }

    protected boolean isChecked(int position) {
        return position < checked.size() && checked.get(position);
    }

    protected int getCountSelected() {
        int count = 0;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.get(i)) {
                count++;
            }
        }
        return count;
    }

    protected List<Integer> getPositionsSelected() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < this.checked.size(); i++) {
            if (checked.get(i)) {
                list.add(i);
            }
        }
        return list;
    }

    private void openAnimate(View view, int position) {
        if (!opened.get(position)) {
            generateRevealAnimate(view, true, false, position);
        }
    }

    private void closeAnimate(View view, int position) {
        if (opened.get(position)) {
            generateRevealAnimate(view, true, false, position);
        }
    }

    private void generateAnimate(View view, boolean swap, boolean swapRight, int position) {
        if (swipeCurrentAction == 0) {
            generateRevealAnimate(view, swap, swapRight, position);
        }
        if (swipeCurrentAction == 1) {
            generateDismissAnimate(parentView, swap, swapRight, position);
        }
        if (swipeCurrentAction == 2) {
            generateChoiceAnimate(view, position);
        }
    }

    private void generateChoiceAnimate(View view, int position) {
        ViewPropertyAnimator.animate(view).translationX(AutoScrollHelper.RELATIVE_UNSPECIFIED).setDuration(this
                .animationTime).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                swipeListView.resetScrolling();
                resetCell();
            }
        });
    }

    private void generateDismissAnimate(final View view, final boolean swap, boolean swapRight, final int position) {
        int moveTo = 0;
        if (opened.get(position)) {
            if (!swap) {
                moveTo = openedRight.get(position) ? (int) (((float) viewWidth) - rightOffset) : (int) (((float)
                        (-viewWidth)) + leftOffset);
            }
        } else if (swap) {
            moveTo = swapRight ? (int) (((float) viewWidth) - rightOffset) : (int) (((float) (-viewWidth)) +
                    leftOffset);
        }
        int alpha = 1;
        if (swap) {
            dismissAnimationRefCount++;
            alpha = 0;
        }
        ViewPropertyAnimator.animate(view).translationX((float) moveTo).alpha((float) alpha).setDuration(this
                .animationTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (swap) {
                    closeOpenedItems();
                    performDismiss(view, position, true);
                }
                SwipeListViewTouchListener.this.swipeListView.resetScrolling();
                SwipeListViewTouchListener.this.resetCell();
            }
        });
    }

    private void generateSpringbackAnimate(final View view, final boolean swap, boolean swapRight, int position) {
        int moveTo = 0;
        if (opened.get(position)) {
            if (!swap) {
                moveTo = openedRight.get(position) ? (int) (((float) this.viewWidth)
                        - this.rightOffset) : (int) (((float) (-this.viewWidth)) + this.leftOffset);
            }
        } else if (swap) {
            moveTo = swapRight ? (int) (((float) this.viewWidth) - this.rightOffset) : (int) (((float) (-this
                    .viewWidth)) + this.leftOffset);
        }
        if (swap) {
            this.dismissAnimationRefCount++;
        }
        final float currentX = view.getTranslationX();
        final float toX = (float) moveTo;
        mSpring.removeAllListeners();
        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0.0d, 1.0d,
                        (double) currentX, (double) toX);
                swipeListView.resetScrolling();
                view.setTranslationX(mappedValue);
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                if (swap) {
                    closeOpenedItems();
                }
                resetCell();
            }
        });
        mSpring.setCurrentValue(0.0d);
        mSpring.setEndValue(1.0d);
    }

    private void generateRevealAnimate(View view, final boolean swap, final boolean swapRight, final int position) {
        int moveTo = 0;
        if (((Boolean) this.opened.get(position)).booleanValue()) {
            if (!swap) {
                moveTo = ((Boolean) this.openedRight.get(position)).booleanValue() ? (int) (((float) this.viewWidth)
                        - this.rightOffset) : (int) (((float) (-this.viewWidth)) + this.leftOffset);
            }
        } else if (swap) {
            moveTo = swapRight ? (int) (((float) this.viewWidth) - this.rightOffset) : (int) (((float) (-this
                    .viewWidth)) + this.leftOffset);
        }
        ViewPropertyAnimator.animate(view).translationX((float) moveTo).setDuration(this.animationTime).setListener
                (new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        swipeListView.resetScrolling();
                        if (swap) {
                            boolean aux = !opened.get(position);
                            opened.set(position, aux);
                            if (aux) {
                                swipeListView.onOpened(position, swapRight);
                                openedRight.set(position, swapRight);
                            } else {
                                swipeListView.onClosed(position, openedRight.get(position));
                            }
                        }
                        swipeListView.resetScrolling();
                        resetCell();
                    }
                });
    }

    private void resetCell() {
        if (downPosition != -1 && swipeCurrentAction == 2) {
            backView.setVisibility(View.VISIBLE);
        }
    }

    public void setEnabled(boolean enabled) {
        this.paused = !enabled;
    }

    public OnScrollListener makeScrollListener() {
        return new OnScrollListener() {
            private boolean isFirstItem;
            private boolean isLastItem;

            {
                this.isFirstItem = false;
                this.isLastItem = false;
            }

            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                SwipeListViewTouchListener.this.setEnabled(scrollState != 1);
                if (SwipeListViewTouchListener.this.swipeClosesAllItemsWhenListMoves && scrollState == 1) {
                    SwipeListViewTouchListener.this.closeOpenedItems();
                }
                if (scrollState == 1) {
                    SwipeListViewTouchListener.this.listViewMoving = true;
                    SwipeListViewTouchListener.this.setEnabled(false);
                }
                if (scrollState != 2 && scrollState != 1) {
                    SwipeListViewTouchListener.this.listViewMoving = false;
                    SwipeListViewTouchListener.this.downPosition = -1;
                    SwipeListViewTouchListener.this.swipeListView.resetScrolling();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            SwipeListViewTouchListener.this.setEnabled(true);
                        }
                    }, 500);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean onLastItemList = false;
                if (this.isFirstItem) {
                    if (firstVisibleItem == 1) {
                        this.isFirstItem = false;
                    }
                } else {
                    boolean onFirstItemList;
                    if (firstVisibleItem == 0) {
                        onFirstItemList = true;
                    } else {
                        onFirstItemList = false;
                    }
                    if (onFirstItemList) {
                        this.isFirstItem = true;
                        SwipeListViewTouchListener.this.swipeListView.onFirstListItem();
                    }
                }
                if (this.isLastItem) {
                    boolean onBeforeLastItemList;
                    if (firstVisibleItem + visibleItemCount == totalItemCount - 1) {
                        onBeforeLastItemList = true;
                    } else {
                        onBeforeLastItemList = false;
                    }
                    if (onBeforeLastItemList) {
                        this.isLastItem = false;
                        return;
                    }
                    return;
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    onLastItemList = true;
                }
                if (onLastItemList) {
                    this.isLastItem = true;
                    SwipeListViewTouchListener.this.swipeListView.onLastListItem();
                }
            }
        };
    }

    void closeOpenedItems() {
        if (this.opened != null) {
            int start = this.swipeListView.getFirstVisiblePosition();
            int end = this.swipeListView.getLastVisiblePosition();
            for (int i = start; i <= end; i++) {
                if (((Boolean) this.opened.get(i)).booleanValue()) {
                    closeAnimate(this.swipeListView.getChildAt(i - start).findViewById(this.swipeFrontView), i);
                }
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.viewWidth < 2) {
            this.viewWidth = this.swipeListView.getWidth();
        }
        float deltaX;
        float abs;
        float abs2;
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
                if (paused && downPosition != -1) {
                    return false;
                }
                swipeCurrentAction = 3;
                int childCount = swipeListView.getChildCount();
                int[] listViewCoords = new int[2];
                swipeListView.getLocationOnScreen(listViewCoords);
                int x = ((int) motionEvent.getRawX()) - listViewCoords[0];
                int y = ((int) motionEvent.getRawY()) - listViewCoords[1];
                for (int i = 0; i < childCount; i++) {
                    View child = swipeListView.getChildAt(i);
                    child.getHitRect(rect);
                    int childPosition = swipeListView.getPositionForView(child);
                    if (swipeListView.getAdapter().isEnabled(childPosition) && rect.contains(x, y)) {
                        setParentView(child);
                        setFrontView(child.findViewById(swipeFrontView), childPosition);
                        downX = motionEvent.getRawX();
                        downPosition = childPosition;
                        frontView.setClickable(!opened.get(downPosition));
                        frontView.setLongClickable(false);
                        velocityTracker = VelocityTracker.obtain();
                        velocityTracker.addMovement(motionEvent);
                        if (swipeBackView > 0) {
                            setBackView(child.findViewById(swipeBackView));
                        }
                        if (swipeListView.getAdapter().getItemViewType(childPosition) < 0 && mSpring !=
                                null) {
                            mSpring.setAtRest();
                            mSpring.removeAllListeners();
                        }
                        view.onTouchEvent(motionEvent);
                        return true;
                    }
                }
                view.onTouchEvent(motionEvent);
                return true;
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                if (!(velocityTracker == null || !swiping || downPosition == -1)) {
                    deltaX = motionEvent.getRawX() - downX;
                    velocityTracker.addMovement(motionEvent);
                    velocityTracker.computeCurrentVelocity(1000);
                    abs = Math.abs(velocityTracker.getXVelocity());
                    if (!((Boolean) opened.get(downPosition)).booleanValue()) {
                        if (swipeMode == 3 && velocityTracker.getXVelocity() > 0.0f) {
                        }
                        if (swipeMode == 2 && velocityTracker.getXVelocity() < 0.0f) {
                        }
                    }
                    abs2 = Math.abs(velocityTracker.getYVelocity());
                    boolean swap = false;
                    boolean swapRight = false;
                    if (((double) Math.abs(deltaX)) > ((double) viewWidth) * 0.5d) {
                        swap = true;
                        swapRight = deltaX > 0.0f;
                    }
                    if (swipeListView.getAdapter().getItemViewType(downPosition) < 0) {
                        generateSpringbackAnimate(parentView, false, swapRight, downPosition);
                    } else {
                        generateAnimate(frontView, swap, swapRight, downPosition);
                    }
                    if (swipeCurrentAction == 2) {
                        swapChoiceState(downPosition);
                    }
                    velocityTracker.recycle();
                    velocityTracker = null;
                    downX = 0.0f;
                    swiping = false;
                    frontView.setClickable(((Boolean) opened.get(downPosition)).booleanValue());
                    frontView.setLongClickable(((Boolean) opened.get(downPosition)).booleanValue());
                    frontView = null;
                    backView = null;
                    downPosition = -1;
                }
                return false;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                if (!(velocityTracker == null || paused || downPosition == -1)) {
                    velocityTracker.addMovement(motionEvent);
                    velocityTracker.computeCurrentVelocity(1000);
                    abs = Math.abs(velocityTracker.getXVelocity());
                    abs2 = Math.abs(velocityTracker.getYVelocity());
                    deltaX = motionEvent.getRawX() - downX;
                    float deltaMode = Math.abs(deltaX);
                    int swipeMode = this.swipeMode;
                    int changeSwipeMode = swipeListView.changeSwipeMode(downPosition);
                    if (changeSwipeMode >= 0) {
                        swipeMode = changeSwipeMode;
                    }
                    if (swipeMode == 0) {
                        deltaMode = AutoScrollHelper.RELATIVE_UNSPECIFIED;
                    } else if (swipeMode != 1) {
                        if (opened.get(downPosition)) {
                            if (swipeMode == 3 && deltaX < 0.0f) {
                                deltaMode = AutoScrollHelper.RELATIVE_UNSPECIFIED;
                            } else if (swipeMode == 2 && deltaX > 0.0f) {
                                deltaMode = AutoScrollHelper.RELATIVE_UNSPECIFIED;
                            }
                        } else if (swipeMode == 3 && deltaX > 0.0f) {
                            deltaMode = AutoScrollHelper.RELATIVE_UNSPECIFIED;
                        } else if (swipeMode == 2 && deltaX < 0.0f) {
                            deltaMode = AutoScrollHelper.RELATIVE_UNSPECIFIED;
                        }
                    }
                    if (deltaMode > ((float) slop) && swipeCurrentAction == 3 && abs2 < abs) {
                        swiping = true;
                        swipingRight = deltaX > 0.0f;
                        if (opened.get(downPosition)) {
                            swipeListView.onStartClose(downPosition, swipingRight);
                            swipeCurrentAction = 0;
                        } else {
                            if (swipingRight && swipeActionRight == 1) {
                                swipeCurrentAction = 1;
                            } else if (!swipingRight && swipeActionLeft == 1) {
                                swipeCurrentAction = 1;
                            } else if (swipingRight && swipeActionRight == 2) {
                                swipeCurrentAction = 2;
                            } else if (swipingRight || swipeActionLeft != 2) {
                                swipeCurrentAction = 0;
                            } else {
                                swipeCurrentAction = 2;
                            }
                            swipeListView.onStartOpen(downPosition, swipeCurrentAction, this.swipingRight);
                        }
                        swipeListView.requestDisallowInterceptTouchEvent(true);
                        MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                        cancelEvent.setAction((MotionEventCompat.getActionIndex(motionEvent) << 8) | 3);
                        swipeListView.onTouchEvent(cancelEvent);
                        if (swipeCurrentAction == 2) {
                            backView.setVisibility(View.GONE);
                        }
                    }
                    if (swiping && downPosition != -1) {
                        if (opened.get(downPosition)) {
                            float f;
                            if (openedRight.get(downPosition)) {
                                f = ((float) viewWidth) - rightOffset;
                            } else {
                                f = ((float) (-viewWidth)) + leftOffset;
                            }
                            deltaX += f;
                        }
                        move(0.8f * deltaX);
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private void setActionsTo(int action) {
        oldSwipeActionRight = swipeActionRight;
        oldSwipeActionLeft = swipeActionLeft;
        swipeActionRight = action;
        swipeActionLeft = action;
    }

    protected void returnOldActions() {
        swipeActionRight = oldSwipeActionRight;
        swipeActionLeft = oldSwipeActionLeft;
    }

    public void move(float deltaX) {
        boolean z;
        swipeListView.onMove(downPosition, deltaX);
        float posX = ViewHelper.getX(frontView);
        if (((Boolean) opened.get(downPosition)).booleanValue()) {
            posX += ((Boolean) openedRight.get(downPosition)).booleanValue() ? ((float) (-viewWidth))
                    + rightOffset : ((float) viewWidth) - leftOffset;
        }
        if (posX > 0.0f && !swipingRight) {
            if (swipingRight) {
                z = false;
            } else {
                z = true;
            }
            swipingRight = z;
            swipeCurrentAction = swipeActionRight;
            if (swipeCurrentAction == 2) {
                backView.setVisibility(View.GONE);
            } else {
                backView.setVisibility(View.VISIBLE);
            }
        }
        if (posX < 0.0f && swipingRight) {
            if (swipingRight) {
                z = false;
            } else {
                z = true;
            }
            swipingRight = z;
            swipeCurrentAction = swipeActionLeft;
            if (swipeCurrentAction == 2) {
                backView.setVisibility(View.GONE);
            } else {
                backView.setVisibility(View.VISIBLE);
            }
        }
        if (swipeCurrentAction == 1) {
            if (swipeListView.getAdapter().getItemViewType(downPosition) < 0) {
                ViewHelper.setTranslationX(parentView, convertXWithDamp(deltaX));
            } else {
                ViewHelper.setTranslationX(parentView, deltaX);
            }
            if (swipeListView.getAdapter().getItemViewType(downPosition) >= 0) {
                ViewHelper.setAlpha(parentView, Math.max(AutoScrollHelper.RELATIVE_UNSPECIFIED, Math.min
                        (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 1.0f - ((1.3f * Math.abs(deltaX)) / ((float) this
                                .viewWidth)))));
            }
        } else if (swipeCurrentAction == 2) {
            if (!swipingRight || deltaX <= 0.0f || posX >= 80.0f) {
                if (swipingRight || deltaX >= 0.0f || posX <= -80.0f) {
                    if ((!swipingRight || deltaX >= 80.0f) && (swipingRight || deltaX <= -80.0f)) {
                        return;
                    }
                }
            }
            ViewHelper.setTranslationX(frontView, deltaX);
        } else {
            ViewHelper.setTranslationX(frontView, deltaX);
        }
    }

    private float convertXWithDamp(float deltaX) {
        float value = (float) Math.pow((double) Math.abs(deltaX), 0.75d);
        return deltaX >= 0.0f ? value : -value;
    }

    protected void performDismiss(final View dismissView, final int dismissPosition, boolean doPendingDismiss) {
        enableDisableViewGroup((ViewGroup) dismissView, false);
        final LayoutParams lp = dismissView.getLayoutParams();
        final ValueAnimator animator = ValueAnimator.ofInt(dismissView.getHeight(), 1).setDuration(animationTime);
        if (doPendingDismiss) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (dismissAnimationRefCount == 0) {
                        removePendingDismisses(dismissPosition);
                    }
                }
            });
        }
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                enableDisableViewGroup((ViewGroup) dismissView, true);
            }
        });
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = ((int) animator.getAnimatedValue());
                dismissView.setLayoutParams(lp);
            }
        });
        pendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
        animator.start();
    }

    protected void resetPendingDismisses() {
        pendingDismisses.clear();
    }

    protected void handlerPendingDismisses(final int originalHeight) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removePendingDismisses(originalHeight);
            }
        }, animationTime + 100);
    }

    private void removePendingDismisses(int originalHeight) {
        Collections.sort(pendingDismisses);
        int[] dismissPositions = new int[pendingDismisses.size()];
        for (int i = pendingDismisses.size() - 1; i >= 0; i--) {
            dismissPositions[i] = ((PendingDismissData) pendingDismisses.get(i)).position;
        }
        swipeListView.onDismiss(dismissPositions);
        for (PendingDismissData pendingDismiss : pendingDismisses) {
            if (pendingDismiss.view != null) {
                ViewHelper.setAlpha(pendingDismiss.view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                ViewHelper.setTranslationX(pendingDismiss.view, AutoScrollHelper.RELATIVE_UNSPECIFIED);
                LayoutParams lp = pendingDismiss.view.getLayoutParams();
                lp.height = originalHeight;
                pendingDismiss.view.setLayoutParams(lp);
            }
        }
        resetPendingDismisses();
    }

    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
}
