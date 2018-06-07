package com.opweather.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.ListPopupWindow;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class OPAlertDialog extends Dialog implements DialogInterface {
    private OPAlertController mAlert;
    protected Context mContext;

    public static class Builder {
        private final AlertParams P;

        public Builder(Context context) {
            this.P = new AlertParams(context);
        }

        public Builder(Context context, int theme) {
            this.P = new AlertParams(new ContextThemeWrapper(context, OPAlertDialog.resolveDialogTheme(context, theme)));
        }

        public Context getContext() {
            return this.P.mContext;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setTitle(int titleId) {
            this.P.mTitle = this.P.mContext.getText(titleId);
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setTitle(CharSequence title) {
            this.P.mTitle = title;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setCustomTitle(View customTitleView) {
            this.P.mCustomTitleView = customTitleView;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setMessage(int messageId) {
            this.P.mMessage = this.P.mContext.getText(messageId);
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setMessage(CharSequence message) {
            this.P.mMessage = message;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setIcon(int iconId) {
            this.P.mIconId = iconId;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setIcon(Drawable icon) {
            this.P.mIcon = icon;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setIconAttribute(int attrId) {
            TypedValue out = new TypedValue();
            this.P.mContext.getTheme().resolveAttribute(attrId, out, true);
            this.P.mIconId = out.resourceId;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setPositiveButton(int textId, OnClickListener listener) {
            this.P.mPositiveButtonText = this.P.mContext.getText(textId);
            this.P.mPositiveButtonListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            this.P.mPositiveButtonText = text;
            this.P.mPositiveButtonListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setNegativeButton(int textId, OnClickListener listener) {
            this.P.mNegativeButtonText = this.P.mContext.getText(textId);
            this.P.mNegativeButtonListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            this.P.mNegativeButtonText = text;
            this.P.mNegativeButtonListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setNeutralButton(int textId, OnClickListener listener) {
            this.P.mNeutralButtonText = this.P.mContext.getText(textId);
            this.P.mNeutralButtonListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            this.P.mNeutralButtonText = text;
            this.P.mNeutralButtonListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setCancelable(boolean cancelable) {
            this.P.mCancelable = cancelable;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.P.mOnCancelListener = onCancelListener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.P.mOnDismissListener = onDismissListener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setOnKeyListener(OnKeyListener onKeyListener) {
            this.P.mOnKeyListener = onKeyListener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setItems(int itemsId, OnClickListener listener) {
            this.P.mItems = this.P.mContext.getResources().getTextArray(itemsId);
            this.P.mOnClickListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setItems(CharSequence[] items, OnClickListener listener) {
            this.P.mItems = items;
            this.P.mOnClickListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setAdapter(ListAdapter adapter, OnClickListener listener) {
            this.P.mAdapter = adapter;
            this.P.mOnClickListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setCursor(Cursor cursor, OnClickListener listener, String labelColumn) {
            this.P.mCursor = cursor;
            this.P.mLabelColumn = labelColumn;
            this.P.mOnClickListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            this.P.mItems = this.P.mContext.getResources().getTextArray(itemsId);
            this.P.mOnCheckboxClickListener = listener;
            this.P.mCheckedItems = checkedItems;
            this.P.mIsMultiChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            this.P.mItems = items;
            this.P.mOnCheckboxClickListener = listener;
            this.P.mCheckedItems = checkedItems;
            this.P.mIsMultiChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn, OnMultiChoiceClickListener listener) {
            this.P.mCursor = cursor;
            this.P.mOnCheckboxClickListener = listener;
            this.P.mIsCheckedColumn = isCheckedColumn;
            this.P.mLabelColumn = labelColumn;
            this.P.mIsMultiChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener) {
            this.P.mItems = this.P.mContext.getResources().getTextArray(itemsId);
            this.P.mOnClickListener = listener;
            this.P.mCheckedItem = checkedItem;
            this.P.mIsSingleChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn, OnClickListener listener) {
            this.P.mCursor = cursor;
            this.P.mOnClickListener = listener;
            this.P.mCheckedItem = checkedItem;
            this.P.mLabelColumn = labelColumn;
            this.P.mIsSingleChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, OnClickListener listener) {
            this.P.mItems = items;
            this.P.mOnClickListener = listener;
            this.P.mCheckedItem = checkedItem;
            this.P.mIsSingleChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, OnClickListener listener) {
            this.P.mAdapter = adapter;
            this.P.mOnClickListener = listener;
            this.P.mCheckedItem = checkedItem;
            this.P.mIsSingleChoice = true;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setOnItemSelectedListener(OnItemSelectedListener listener) {
            this.P.mOnItemSelectedListener = listener;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setView(int layoutResId) {
            this.P.mView = null;
            this.P.mViewLayoutResId = layoutResId;
            this.P.mViewSpacingSpecified = false;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setView(View view) {
            this.P.mView = view;
            this.P.mViewLayoutResId = 0;
            this.P.mViewSpacingSpecified = false;
            return this;
        }

        @Deprecated
        public com.oneplus.lib.app.OPAlertDialog.Builder setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
            this.P.mView = view;
            this.P.mViewLayoutResId = 0;
            this.P.mViewSpacingSpecified = true;
            this.P.mViewSpacingLeft = viewSpacingLeft;
            this.P.mViewSpacingTop = viewSpacingTop;
            this.P.mViewSpacingRight = viewSpacingRight;
            this.P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        @Deprecated
        public com.oneplus.lib.app.OPAlertDialog.Builder setInverseBackgroundForced(boolean useInverseBackground) {
            this.P.mForceInverseBackground = useInverseBackground;
            return this;
        }

        public com.oneplus.lib.app.OPAlertDialog.Builder setRecycleOnMeasureEnabled(boolean enabled) {
            this.P.mRecycleOnMeasure = enabled;
            return this;
        }

        public OPAlertDialog create() {
            OPAlertDialog dialog = new OPAlertDialog(this.P.mContext);
            this.P.apply(dialog.mAlert);
            dialog.setCancelable(this.P.mCancelable);
            if (this.P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(this.P.mOnCancelListener);
            dialog.setOnDismissListener(this.P.mOnDismissListener);
            if (this.P.mOnKeyListener != null) {
                dialog.setOnKeyListener(this.P.mOnKeyListener);
            }
            return dialog;
        }

        public OPAlertDialog show() {
            OPAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    protected OPAlertDialog(Context context) {
        this(context, resolveDialogTheme(context, 0), true);
    }

    protected OPAlertDialog(Context context, int themeResId) {
        this(context, themeResId, true);
    }

    OPAlertDialog(Context context, int theme, boolean createThemeContextWrapper) {
        super(context, resolveDialogTheme(context, theme));
        this.mAlert = new OPAlertController(getContext(), this, getWindow());
        this.mContext = context;
    }

    protected OPAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, resolveDialogTheme(context, 0));
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
        this.mAlert = new OPAlertController(context, this, getWindow());
        this.mContext = context;
    }

    static int resolveDialogTheme(Context context, int themeResId) {
        if (themeResId >= 16777216) {
            return themeResId;
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(16843529, outValue, true);
        return outValue.resourceId;
    }

    public Button getButton(int whichButton) {
        return this.mAlert.getButton(whichButton);
    }

    public ListView getListView() {
        return this.mAlert.getListView();
    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.mAlert.setTitle(title);
    }

    public void setCustomTitle(View customTitleView) {
        this.mAlert.setCustomTitle(customTitleView);
    }

    public void setMessage(CharSequence message) {
        this.mAlert.setMessage(message);
    }

    public void setView(View view) {
        this.mAlert.setView(view);
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    public void setButton(int whichButton, CharSequence text, Message msg) {
        this.mAlert.setButton(whichButton, text, null, msg);
    }

    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        this.mAlert.setButton(whichButton, text, listener, null);
    }

    @Deprecated
    public void setButton(CharSequence text, Message msg) {
        setButton(-1, text, msg);
    }

    @Deprecated
    public void setButton2(CharSequence text, Message msg) {
        setButton((int) ListPopupWindow.WRAP_CONTENT, text, msg);
    }

    @Deprecated
    public void setButton3(CharSequence text, Message msg) {
        setButton((int) OpBoostFramework.REQUEST_FAILED_UNKNOWN_POLICY, text, msg);
    }

    @Deprecated
    public void setButton(CharSequence text, OnClickListener listener) {
        setButton(-1, text, listener);
    }

    @Deprecated
    public void setButton2(CharSequence text, OnClickListener listener) {
        setButton((int) ListPopupWindow.WRAP_CONTENT, text, listener);
    }

    @Deprecated
    public void setButton3(CharSequence text, OnClickListener listener) {
        setButton((int) OpBoostFramework.REQUEST_FAILED_UNKNOWN_POLICY, text, listener);
    }

    public void setIcon(int resId) {
        this.mAlert.setIcon(resId);
    }

    public void setIcon(Drawable icon) {
        this.mAlert.setIcon(icon);
    }

    public void setIconAttribute(int attrId) {
        TypedValue out = new TypedValue();
        this.mContext.getTheme().resolveAttribute(attrId, out, true);
        this.mAlert.setIcon(out.resourceId);
    }

    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        this.mAlert.setInverseBackgroundForced(forceInverseBackground);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAlert.installContent();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mAlert.onKeyDown(keyCode, event) ? true : super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mAlert.onKeyUp(keyCode, event) ? true : super.onKeyUp(keyCode, event);
    }
}
