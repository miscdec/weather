package com.opweather.widget.anim;

import android.support.v4.widget.AutoScrollHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.opweather.R;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.List;

public class WeatherPathAnim {
    private boolean isOpen;
    private int mChildCount;
    private ViewGroup mMenuGroup;
    private List<ViewPropertyAnimator> viewAnimators;

    private class AnimListener implements AnimatorListener {
        private AnimatorListener mListener;
        private View target;

        public AnimListener(View _target, AnimatorListener l) {
            target = _target;
            mListener = l;
        }

        public void onAnimationStart(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationStart(animation);
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (!isOpen) {
                target.setVisibility(View.INVISIBLE);
            }
            if (mListener != null) {
                mListener.onAnimationEnd(animation);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationCancel(animation);
            }
        }

        public void onAnimationRepeat(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationRepeat(animation);
            }
        }
    }

    public WeatherPathAnim(ViewGroup menuGroup, int poscode) {
        isOpen = false;
        viewAnimators = new ArrayList<>();
        mMenuGroup = menuGroup;
        mChildCount = mMenuGroup.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            viewAnimators.add(ViewPropertyAnimator.animate(mMenuGroup.getChildAt(i)));
        }
    }

    public void startAnimationsOpen(int durationMillis) {
        isOpen = true;
        for (int i = 1; i < mMenuGroup.getChildCount(); i++) {
            View inoutimagebutton = mMenuGroup.getChildAt(i);
            ViewPropertyAnimator viewPropertyAnimator = (ViewPropertyAnimator) viewAnimators.get(i);
            viewPropertyAnimator.setListener(null);
            inoutimagebutton.setVisibility(View.VISIBLE);
            viewPropertyAnimator.x((float) inoutimagebutton.getLeft()).y((float) inoutimagebutton.getTop());
            viewPropertyAnimator.rotation(-360.0f);
            viewPropertyAnimator.setInterpolator(new OvershootInterpolator(2.0f));
        }
    }

    public void startAnimationsClose(int durationMillis, AnimatorListener l) {
        isOpen = false;
        for (int i = 1; i < mMenuGroup.getChildCount(); i++) {
            View inoutimagebutton = mMenuGroup.getChildAt(i);
            ViewPropertyAnimator viewPropertyAnimator = (ViewPropertyAnimator) viewAnimators.get(i);
            viewPropertyAnimator.x((float) (inoutimagebutton.getLeft() == 0 ? R.styleable.AppCompatTheme_dialogTheme
                    : R.styleable.AppCompatTheme_dialogTheme)).y((float) mMenuGroup.getChildAt(0).getTop());
            viewPropertyAnimator.rotation(AutoScrollHelper.RELATIVE_UNSPECIFIED);
            viewPropertyAnimator.setListener(new AnimListener(inoutimagebutton, l));
            viewPropertyAnimator.setInterpolator(new AccelerateInterpolator());
        }
    }

    public static Animation getRotateAnimation(float fromDegrees, float toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, 1, 0.5f, 1, 0.5f);
        rotate.setDuration((long) durationMillis);
        rotate.setFillAfter(true);
        return rotate;
    }
}
