package com.opweather.widget.swipelistview;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeListViewTouchListener implements OnTouchListener {
    private static final int DISPLACE_CHOICE = 80;
    private long animationTime;
    private View backView;
    private List<Boolean> checked = new ArrayList<>();
    private long configShortAnimationTime;
    private int dismissAnimationRefCount = 0;
    private int downPosition;
    private float downX;
    private View frontView;
    private float leftOffset = 0.0f;
    private boolean listViewMoving;
    private Spring mSpring;
    private SpringConfig mSpringConfig;
    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private int maxFlingVelocity;
    private int minFlingVelocity;
    private int oldSwipeActionLeft;
    private int oldSwipeActionRight;
    private List<Boolean> opened = new ArrayList<>();
    private List<Boolean> openedRight = new ArrayList<>();
    private View parentView;
    private boolean paused;
    private List<PendingDismissData> pendingDismisses = new ArrayList<>();
    private Rect rect = new Rect();
    private float rightOffset = 0.0f;
    private int slop;
    private int swipeActionLeft = 0;
    private int swipeActionRight = 0;
    private int swipeBackView = 0;
    private boolean swipeClosesAllItemsWhenListMoves = true;
    private int swipeCurrentAction = 3;
    private int swipeDrawableChecked = 0;
    private int swipeDrawableUnchecked = 0;
    private int swipeFrontView = 0;
    private SwipeListView swipeListView;
    private int swipeMode = 1;
    private boolean swipeOpenOnLongPress = true;
    private boolean swiping;
    private boolean swipingRight;
    private VelocityTracker velocityTracker;
    private int viewWidth = 1;

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

    static /* synthetic */ int access$906(SwipeListViewTouchListener x0) {
        int i = x0.dismissAnimationRefCount - 1;
        x0.dismissAnimationRefCount = i;
        return i;
    }

    public SwipeListViewTouchListener(SwipeListView swipeListView, int swipeFrontView, int swipeBackView) {
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
        this.mSpring = mSpringSystem.createSpring();
        this.mSpringConfig = SpringConfig.defaultConfig;
        this.mSpringConfig.tension = OrigamiValueConverter.tensionFromOrigamiValue(50.0d);
        this.mSpringConfig.friction = OrigamiValueConverter.frictionFromOrigamiValue(7.0d);
        this.mSpring.setSpringConfig(mSpringConfig);
    }

    private void setParentView(View parentView) {
        this.parentView = parentView;
    }

    private void setFrontView(View frontView, final int childPosition) {
        this.frontView = frontView;
        frontView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeListView.onClickFrontView(downPosition);
            }
        });
        frontView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
            @Override
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
        View child = swipeListView.getChildAt(position - swipeListView.getFirstVisiblePosition()).findViewById
                (swipeFrontView);
        if (child != null) {
            openAnimate(child, position);
        }
    }

    protected void closeAnimate(int position) {
        if (swipeListView != null) {
            View childContainer = swipeListView.getChildAt(position - swipeListView.getFirstVisiblePosition());
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
            setActionsTo(2);
        }
        if (lastCount == 1 && count == 0) {
            swipeListView.onChoiceEnded();
            returnOldActions();
        }
        SwipeListView swipeListView = this.swipeListView;
        if (lastChecked) {
            z = false;
        } else {
            z = true;
        }
        swipeListView.setItemChecked(position, z);
        SwipeListView swipeListView2 = this.swipeListView;
        if (lastChecked) {
            z2 = false;
        }
        swipeListView2.onChoiceChanged(position, z2);
        reloadChoiceStateInView(frontView, position);
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
        opened.remove(position);
        checked.remove(position);
        int start = swipeListView.getFirstVisiblePosition();
        int end = swipeListView.getLastVisiblePosition();
        View view = swipeListView.getChildAt(position - start);
        dismissAnimationRefCount++;
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
            ViewHelper.setTranslationX(frontView, 0.0f);
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
        for (int i = 0; i < checked.size(); i++) {
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
        ViewPropertyAnimator.animate(view).translationX(0.0f).setDuration(animationTime).setListener(new AnimatorListenerAdapter() {
            @Override
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
                moveTo = (openedRight.get(position) ? (int) (((float) viewWidth)
                        - rightOffset) : (int) (((float) (-viewWidth)) + leftOffset));
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
        ViewPropertyAnimator.animate(view).translationX((float) moveTo).alpha((float) alpha).setDuration
                (animationTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (swap) {
                    closeOpenedItems();
                    performDismiss(view, position, true);
                }
                swipeListView.resetScrolling();
                resetCell();
            }
        });
    }

    private void generateSpringbackAnimate(View view, boolean swap, boolean swapRight, int position) {
        int moveTo = 0;
        if (opened.get(position)) {
            if (!swap) {
                moveTo = (openedRight.get(position) ? (int) (((float) viewWidth)
                        - rightOffset) : (int) (((float) (-viewWidth)) + leftOffset));
            }
        } else if (swap) {
            moveTo = swapRight ? (int) (((float) viewWidth) - rightOffset) : (int) (((float) (-viewWidth)) +
                    leftOffset);
        }
        if (swap) {
            dismissAnimationRefCount++;
        }
        final float currentX = view.getTranslationX();
        final float toX = (float) moveTo;
        mSpring.removeAllListeners();
        final View view2 = view;
        final boolean z = swap;
        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0.0d, 1.0d,
                        (double) currentX, (double) toX);
                swipeListView.resetScrolling();
                view2.setTranslationX(mappedValue);
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                if (z) {
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
        if (opened.get(position)) {
            if (!swap) {
                moveTo = (openedRight.get(position) ? (int) (((float) viewWidth)
                        - rightOffset) : (int) (((float) (-viewWidth)) + leftOffset));
            }
        } else if (swap) {
            moveTo = swapRight ? (int) (((float) viewWidth) - rightOffset) : (int) (((float) (-viewWidth)) +
                    leftOffset);
        }
        ViewPropertyAnimator.animate(view).translationX((float) moveTo).setDuration(animationTime).setListener
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
        paused = !enabled;
    }

    public OnScrollListener makeScrollListener() {
        return new OnScrollListener() {
            private boolean isFirstItem = false;
            private boolean isLastItem = false;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != 1);
                if (swipeClosesAllItemsWhenListMoves && scrollState == 1) {
                    closeOpenedItems();
                }
                if (scrollState == 1) {
                    listViewMoving = true;
                    setEnabled(false);
                }
                if (scrollState != 2 && scrollState != 1) {
                    listViewMoving = false;
                    downPosition = -1;
                    swipeListView.resetScrolling();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setEnabled(true);
                        }
                    }, 500);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean onLastItemList = false;
                if (isFirstItem) {
                    if (firstVisibleItem == 1) {
                        isFirstItem = false;
                    }
                } else {
                    boolean onFirstItemList;
                    if (firstVisibleItem == 0) {
                        onFirstItemList = true;
                    } else {
                        onFirstItemList = false;
                    }
                    if (onFirstItemList) {
                        isFirstItem = true;
                        swipeListView.onFirstListItem();
                    }
                }
                if (isLastItem) {
                    boolean onBeforeLastItemList;
                    if (firstVisibleItem + visibleItemCount == totalItemCount - 1) {
                        onBeforeLastItemList = true;
                    } else {
                        onBeforeLastItemList = false;
                    }
                    if (onBeforeLastItemList) {
                        isLastItem = false;
                        return;
                    }
                    return;
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    onLastItemList = true;
                }
                if (onLastItemList) {
                    isLastItem = true;
                    swipeListView.onLastListItem();
                }
            }
        };
    }

    void closeOpenedItems() {
        if (opened != null) {
            int start = swipeListView.getFirstVisiblePosition();
            int end = swipeListView.getLastVisiblePosition();
            for (int i = start; i <= end; i++) {
                if (opened.get(i)) {
                    closeAnimate(swipeListView.getChildAt(i - start).findViewById(swipeFrontView), i);
                }
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (viewWidth < 2) {
            viewWidth = swipeListView.getWidth();
        }
        float deltaX;
        float velocityX;
        float velocityY;
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            case 0:
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
                        if (swipeListView.getAdapter().getItemViewType(childPosition) < 0 && mSpring != null) {
                            mSpring.setAtRest();
                            mSpring.removeAllListeners();
                        }
                        view.onTouchEvent(motionEvent);
                        return true;
                    }
                }
                view.onTouchEvent(motionEvent);
                return true;
            case 1:
                if (!(velocityTracker == null || !swiping || downPosition == -1)) {
                    deltaX = motionEvent.getRawX() - downX;
                    velocityTracker.addMovement(motionEvent);
                    velocityTracker.computeCurrentVelocity(1000);
                    velocityX = Math.abs(velocityTracker.getXVelocity());
                    if (!opened.get(downPosition)) {
                        if (swipeMode == 3 && velocityTracker.getXVelocity() > 0.0f) {
                        }
                        if (swipeMode == 2 && velocityTracker.getXVelocity() < 0.0f) {
                        }
                    }
                    velocityY = Math.abs(velocityTracker.getYVelocity());
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
                    frontView.setClickable(opened.get(downPosition));
                    frontView.setLongClickable(opened.get(downPosition));
                    frontView = null;
                    backView = null;
                    downPosition = -1;
                    break;
                }
            case 2:
                if (!(velocityTracker == null || paused || downPosition == -1)) {
                    velocityTracker.addMovement(motionEvent);
                    velocityTracker.computeCurrentVelocity(1000);
                    velocityX = Math.abs(velocityTracker.getXVelocity());
                    velocityY = Math.abs(velocityTracker.getYVelocity());
                    deltaX = motionEvent.getRawX() - downX;
                    float deltaMode = Math.abs(deltaX);
                    int swipeMode = this.swipeMode;
                    int changeSwipeMode = swipeListView.changeSwipeMode(downPosition);
                    if (changeSwipeMode >= 0) {
                        swipeMode = changeSwipeMode;
                    }
                    if (swipeMode == 0) {
                        deltaMode = 0.0f;
                    } else if (swipeMode != 1) {
                        if (opened.get(downPosition)) {
                            if (swipeMode == 3 && deltaX < 0.0f) {
                                deltaMode = 0.0f;
                            } else if (swipeMode == 2 && deltaX > 0.0f) {
                                deltaMode = 0.0f;
                            }
                        } else if (swipeMode == 3 && deltaX > 0.0f) {
                            deltaMode = 0.0f;
                        } else if (swipeMode == 2 && deltaX < 0.0f) {
                            deltaMode = 0.0f;
                        }
                    }
                    if (deltaMode > ((float) slop) && swipeCurrentAction == 3 && velocityY < velocityX) {
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
                            swipeListView.onStartOpen(downPosition, swipeCurrentAction, swipingRight);
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
                break;
        }
        return false;
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
        if (opened.get(downPosition)) {
            posX += (openedRight.get(downPosition) ? ((float) (-viewWidth))
                    + rightOffset : ((float) viewWidth) - leftOffset);
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
                ViewHelper.setAlpha(parentView, Math.max(0.0f, Math.min(1.0f, 1.0f - ((1.3f * Math.abs(deltaX))
                        / ((float) viewWidth)))));
            }
        } else if (swipeCurrentAction != 2) {
            ViewHelper.setTranslationX(frontView, deltaX);
        } else if ((swipingRight && deltaX > 0.0f && posX < 80.0f) || ((!swipingRight && deltaX < 0.0f &&
                posX > -80.0f) || ((swipingRight && deltaX < 80.0f) || (!swipingRight && deltaX > -80.0f)))) {
            ViewHelper.setTranslationX(frontView, deltaX);
        }
    }

    private float convertXWithDamp(float deltaX) {
        float value = (float) Math.pow((double) Math.abs(deltaX), 0.75d);
        return deltaX >= 0.0f ? value : -value;
    }

    protected void performDismiss(final View dismissView, int dismissPosition, boolean doPendingDismiss) {
        enableDisableViewGroup((ViewGroup) dismissView, false);
        final LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();
        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(animationTime);
        if (doPendingDismiss) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //SwipeListViewTouchListener.access$906(SwipeListViewTouchListener.this);
                    dismissAnimationRefCount --;
                    if (dismissAnimationRefCount == 0) {
                        removePendingDismisses(originalHeight);
                    }
                }
            });
        }
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SwipeListViewTouchListener.enableDisableViewGroup((ViewGroup) dismissView, true);
            }
        });
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (int) valueAnimator.getAnimatedValue();
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
            public void run() {
                removePendingDismisses(originalHeight);
            }
        }, animationTime + 100);
    }

    private void removePendingDismisses(int originalHeight) {
        Collections.sort(pendingDismisses);
        int[] dismissPositions = new int[pendingDismisses.size()];
        for (int i = pendingDismisses.size() - 1; i >= 0; i--) {
            dismissPositions[i] = pendingDismisses.get(i).position;
        }
        swipeListView.onDismiss(dismissPositions);
        for (PendingDismissData pendingDismiss : pendingDismisses) {
            if (pendingDismiss.view != null) {
                ViewHelper.setAlpha(pendingDismiss.view, 1.0f);
                ViewHelper.setTranslationX(pendingDismiss.view, 0.0f);
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
