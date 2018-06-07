package com.opweather.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.oneplus.commonctrl.R;
import com.oneplus.sdk.utils.OpBoostFramework;

import net.oneplus.weather.widget.openglbase.RainSurfaceView;

import java.lang.ref.WeakReference;

public class OPAlertController {
    private ListAdapter mAdapter;
    private int mAlertDialogLayout;
    private final OnClickListener mButtonHandler;
    private Button mButtonNegative;
    private Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    private Button mButtonNeutral;
    private Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private Button mButtonPositive;
    private Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private int mCheckedItem;
    private final Context mContext;
    private View mCustomTitleView;
    private final DialogInterface mDialogInterface;
    private boolean mForceInverseBackground;
    private Handler mHandler;
    private Drawable mIcon;
    private int mIconId;
    private ImageView mIconView;
    private int mListItemLayout;
    private int mListLayout;
    private ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    private int mMultiChoiceItemLayout;
    private ScrollView mScrollView;
    private int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified;
    private int mViewSpacingTop;
    private final Window mWindow;

    class AnonymousClass_2 implements OnApplyWindowInsetsListener {
        final /* synthetic */ View val$parent;

        AnonymousClass_2(View view) {
            this.val$parent = view;
        }

        public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
            if (insets.isRound()) {
                int roundOffset = OPAlertController.this.mContext.getResources().getDimensionPixelOffset(R.dimen.alert_dialog_round_padding);
                this.val$parent.setPadding(roundOffset, roundOffset, roundOffset, roundOffset);
            }
            return insets.consumeSystemWindowInsets();
        }
    }

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public int mCheckedItem;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public View mCustomTitleView;
        public boolean mForceInverseBackground;
        public Drawable mIcon;
        public int mIconAttrId;
        public int mIconId;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public OnCancelListener mOnCancelListener;
        public OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public OnDismissListener mOnDismissListener;
        public OnItemSelectedListener mOnItemSelectedListener;
        public OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public boolean mRecycleOnMeasure;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public boolean mViewSpacingSpecified;
        public int mViewSpacingTop;

        class AnonymousClass_1 extends ArrayAdapter<CharSequence> {
            final /* synthetic */ RecycleListView val$listView;

            AnonymousClass_1(Context x0, int x1, int x2, CharSequence[] x3, RecycleListView recycleListView) {
                this.val$listView = recycleListView;
                super(x0, x1, x2, x3);
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (com.oneplus.lib.app.OPAlertController.AlertParams.this.mCheckedItems != null && com.oneplus.lib.app.OPAlertController.AlertParams.this.mCheckedItems[position]) {
                    this.val$listView.setItemChecked(position, true);
                }
                return view;
            }
        }

        class AnonymousClass_2 extends CursorAdapter {
            private final int mIsCheckedIndex;
            private final int mLabelIndex;
            final /* synthetic */ OPAlertController val$dialog;
            final /* synthetic */ RecycleListView val$listView;

            AnonymousClass_2(Context x0, Cursor x1, boolean x2, RecycleListView recycleListView, OPAlertController oPAlertController) {
                this.val$listView = recycleListView;
                this.val$dialog = oPAlertController;
                super(x0, x1, x2);
                Cursor cursor = getCursor();
                this.mLabelIndex = cursor.getColumnIndexOrThrow(com.oneplus.lib.app.OPAlertController.AlertParams.this.mLabelColumn);
                this.mIsCheckedIndex = cursor.getColumnIndexOrThrow(com.oneplus.lib.app.OPAlertController.AlertParams.this.mIsCheckedColumn);
            }

            public void bindView(View view, Context context, Cursor cursor) {
                boolean z = true;
                ((CheckedTextView) view.findViewById(16908308)).setText(cursor.getString(this.mLabelIndex));
                RecycleListView recycleListView = this.val$listView;
                int position = cursor.getPosition();
                if (cursor.getInt(this.mIsCheckedIndex) != 1) {
                    z = false;
                }
                recycleListView.setItemChecked(position, z);
            }

            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return com.oneplus.lib.app.OPAlertController.AlertParams.this.mInflater.inflate(this.val$dialog.mMultiChoiceItemLayout, parent, false);
            }
        }

        class AnonymousClass_3 implements OnItemClickListener {
            final /* synthetic */ OPAlertController val$dialog;

            AnonymousClass_3(OPAlertController oPAlertController) {
                this.val$dialog = oPAlertController;
            }

            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                com.oneplus.lib.app.OPAlertController.AlertParams.this.mOnClickListener.onClick(this.val$dialog.mDialogInterface, position);
                if (!com.oneplus.lib.app.OPAlertController.AlertParams.this.mIsSingleChoice) {
                    this.val$dialog.mDialogInterface.dismiss();
                }
            }
        }

        class AnonymousClass_4 implements OnItemClickListener {
            final /* synthetic */ OPAlertController val$dialog;
            final /* synthetic */ RecycleListView val$listView;

            AnonymousClass_4(RecycleListView recycleListView, OPAlertController oPAlertController) {
                this.val$listView = recycleListView;
                this.val$dialog = oPAlertController;
            }

            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                if (com.oneplus.lib.app.OPAlertController.AlertParams.this.mCheckedItems != null) {
                    com.oneplus.lib.app.OPAlertController.AlertParams.this.mCheckedItems[position] = this.val$listView.isItemChecked(position);
                }
                com.oneplus.lib.app.OPAlertController.AlertParams.this.mOnCheckboxClickListener.onClick(this.val$dialog.mDialogInterface, position, this.val$listView.isItemChecked(position));
            }
        }

        public static interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            this.mIconId = 0;
            this.mIconAttrId = 0;
            this.mViewSpacingSpecified = false;
            this.mCheckedItem = -1;
            this.mRecycleOnMeasure = true;
            this.mContext = context;
            this.mCancelable = true;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void apply(OPAlertController dialog) {
            if (this.mCustomTitleView != null) {
                dialog.setCustomTitle(this.mCustomTitleView);
            } else {
                if (this.mTitle != null) {
                    dialog.setTitle(this.mTitle);
                }
                if (this.mIcon != null) {
                    dialog.setIcon(this.mIcon);
                }
                if (this.mIconId != 0) {
                    dialog.setIcon(this.mIconId);
                }
                if (this.mIconAttrId != 0) {
                    dialog.setIcon(dialog.getIconAttributeResId(this.mIconAttrId));
                }
            }
            if (this.mMessage != null) {
                dialog.setMessage(this.mMessage);
            }
            if (this.mPositiveButtonText != null) {
                dialog.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null);
            }
            if (this.mNegativeButtonText != null) {
                dialog.setButton(ListPopupWindow.WRAP_CONTENT, this.mNegativeButtonText, this.mNegativeButtonListener, null);
            }
            if (this.mNeutralButtonText != null) {
                dialog.setButton(OpBoostFramework.REQUEST_FAILED_UNKNOWN_POLICY, this.mNeutralButtonText, this.mNeutralButtonListener, null);
            }
            if (this.mForceInverseBackground) {
                dialog.setInverseBackgroundForced(true);
            }
            if (!(this.mItems == null && this.mCursor == null && this.mAdapter == null)) {
                createListView(dialog);
            }
            if (this.mView != null) {
                if (this.mViewSpacingSpecified) {
                    dialog.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                    return;
                }
                dialog.setView(this.mView);
            } else if (this.mViewLayoutResId != 0) {
                dialog.setView(this.mViewLayoutResId);
            }
        }

        private void createListView(OPAlertController dialog) {
            ListAdapter adapter;
            RecycleListView listView = (RecycleListView) this.mInflater.inflate(dialog.mListLayout, null);
            if (!this.mIsMultiChoice) {
                int layout;
                if (this.mIsSingleChoice) {
                    layout = dialog.mSingleChoiceItemLayout;
                } else {
                    layout = dialog.mListItemLayout;
                }
                if (this.mCursor != null) {
                    adapter = new SimpleCursorAdapter(this.mContext, layout, this.mCursor, new String[]{this.mLabelColumn}, new int[]{16908308});
                } else if (this.mAdapter != null) {
                    adapter = this.mAdapter;
                } else {
                    adapter = new CheckedItemAdapter(this.mContext, layout, 16908308, this.mItems);
                }
            } else if (this.mCursor == null) {
                adapter = new AnonymousClass_1(this.mContext, dialog.mMultiChoiceItemLayout, 16908308, this.mItems, listView);
            } else {
                ListAdapter anonymousClass_2 = new AnonymousClass_2(this.mContext, this.mCursor, false, listView, dialog);
            }
            if (this.mOnPrepareListViewListener != null) {
                this.mOnPrepareListViewListener.onPrepareListView(listView);
            }
            dialog.mAdapter = adapter;
            dialog.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                listView.setOnItemClickListener(new AnonymousClass_3(dialog));
            } else if (this.mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new AnonymousClass_4(listView, dialog));
            }
            if (this.mOnItemSelectedListener != null) {
                listView.setOnItemSelectedListener(this.mOnItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                listView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                listView.setChoiceMode(RainSurfaceView.RAIN_LEVEL_SHOWER);
            }
            listView.mRecycleOnMeasure = this.mRecycleOnMeasure;
            dialog.mListView = listView;
        }
    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference(dialog);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OpBoostFramework.REQUEST_FAILED_UNKNOWN_POLICY:
                case ListPopupWindow.WRAP_CONTENT:
                case RainSurfaceView.RAIN_LEVEL_DEFAULT:
                    ((DialogInterface.OnClickListener) msg.obj).onClick((DialogInterface) this.mDialog.get(), msg.what);
                case MSG_DISMISS_DIALOG:
                    ((DialogInterface) msg.obj).dismiss();
                default:
                    break;
            }
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public boolean hasStableIds() {
            return true;
        }

        public long getItemId(int position) {
            return (long) position;
        }
    }

    public static class RecycleListView extends ListView {
        boolean mRecycleOnMeasure;

        public RecycleListView(Context context) {
            super(context);
            this.mRecycleOnMeasure = true;
        }

        public RecycleListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mRecycleOnMeasure = true;
        }

        public RecycleListView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.mRecycleOnMeasure = true;
        }

        public RecycleListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.mRecycleOnMeasure = true;
        }

        protected boolean recycleOnMeasure() {
            return this.mRecycleOnMeasure;
        }
    }

    private static boolean shouldCenterSingleButton(Context context) {
        return false;
    }

    public OPAlertController(Context context, DialogInterface di, Window window) {
        this.mViewSpacingSpecified = false;
        this.mIconId = 0;
        this.mCheckedItem = -1;
        this.mButtonHandler = new OnClickListener() {
            public void onClick(View v) {
                Message m;
                if (v == OPAlertController.this.mButtonPositive && OPAlertController.this.mButtonPositiveMessage != null) {
                    m = Message.obtain(OPAlertController.this.mButtonPositiveMessage);
                } else if (v == OPAlertController.this.mButtonNegative && OPAlertController.this.mButtonNegativeMessage != null) {
                    m = Message.obtain(OPAlertController.this.mButtonNegativeMessage);
                } else if (v != OPAlertController.this.mButtonNeutral || OPAlertController.this.mButtonNeutralMessage == null) {
                    m = null;
                } else {
                    m = Message.obtain(OPAlertController.this.mButtonNeutralMessage);
                }
                if (m != null) {
                    m.sendToTarget();
                }
                OPAlertController.this.mHandler.obtainMessage(1, OPAlertController.this.mDialogInterface).sendToTarget();
            }
        };
        this.mContext = context;
        this.mDialogInterface = di;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(di);
        this.mAlertDialogLayout = R.layout.op_alert_dialog_material;
        this.mListLayout = R.layout.op_select_dialog_material;
        this.mMultiChoiceItemLayout = R.layout.op_select_dialog_multichoice_material;
        this.mSingleChoiceItemLayout = R.layout.op_select_dialog_singlechoice_material;
        this.mListItemLayout = R.layout.op_select_dialog_item_material;
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.OPAlertDialog, R.attr.OPAlertDialogStyle, 0);
        this.mAlertDialogLayout = a.getResourceId(R.styleable.OPAlertDialog_android_layout, R.layout.op_alert_dialog_material);
        this.mListLayout = a.getResourceId(R.styleable.OPAlertDialog_op_listLayout, R.layout.op_select_dialog_material);
        this.mMultiChoiceItemLayout = a.getResourceId(R.styleable.OPAlertDialog_op_multiChoiceItemLayout, R.layout.op_select_dialog_multichoice_material);
        this.mSingleChoiceItemLayout = a.getResourceId(R.styleable.OPAlertDialog_op_singleChoiceItemLayout, R.layout.op_select_dialog_singlechoice_material);
        this.mListItemLayout = a.getResourceId(R.styleable.OPAlertDialog_op_listItemLayout, R.layout.op_select_dialog_item_material);
        a.recycle();
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void installContent() {
        this.mWindow.requestFeature(1);
        this.mWindow.setContentView(this.mAlertDialogLayout);
        setupView();
        setupDecor();
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
    }

    public void setCustomTitle(View customTitleView) {
        this.mCustomTitleView = customTitleView;
    }

    public void setMessage(CharSequence message) {
        this.mMessage = message;
        if (this.mMessageView != null) {
            this.mMessageView.setText(message);
        }
    }

    public void setView(int layoutResId) {
        this.mView = null;
        this.mViewLayoutResId = layoutResId;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = viewSpacingLeft;
        this.mViewSpacingTop = viewSpacingTop;
        this.mViewSpacingRight = viewSpacingRight;
        this.mViewSpacingBottom = viewSpacingBottom;
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {
        if (msg == null && listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case OpBoostFramework.REQUEST_FAILED_UNKNOWN_POLICY:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
            case ListPopupWindow.WRAP_CONTENT:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
            case RainSurfaceView.RAIN_LEVEL_DEFAULT:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setIcon(int resId) {
        this.mIcon = null;
        this.mIconId = resId;
        if (this.mIconView == null) {
            return;
        }
        if (resId != 0) {
            this.mIconView.setImageResource(this.mIconId);
        } else {
            this.mIconView.setVisibility(DetectedActivity.RUNNING);
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        this.mIconId = 0;
        if (this.mIconView == null) {
            return;
        }
        if (icon != null) {
            this.mIconView.setImageDrawable(icon);
        } else {
            this.mIconView.setVisibility(DetectedActivity.RUNNING);
        }
    }

    public int getIconAttributeResId(int attrId) {
        TypedValue out = new TypedValue();
        this.mContext.getTheme().resolveAttribute(attrId, out, true);
        return out.resourceId;
    }

    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        this.mForceInverseBackground = forceInverseBackground;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case OpBoostFramework.REQUEST_FAILED_UNKNOWN_POLICY:
                return this.mButtonNeutral;
            case ListPopupWindow.WRAP_CONTENT:
                return this.mButtonNegative;
            case RainSurfaceView.RAIN_LEVEL_DEFAULT:
                return this.mButtonPositive;
            default:
                return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    private void setupDecor() {
        View decor = this.mWindow.getDecorView();
        View parent = this.mWindow.findViewById(R.id.parentPanel);
        if (parent != null && decor != null) {
            decor.setOnApplyWindowInsetsListener(new AnonymousClass_2(parent));
            decor.setFitsSystemWindows(true);
            decor.requestApplyInsets();
        }
    }

    private ViewGroup resolvePanel(View customPanel, View defaultPanel) {
        if (customPanel == null) {
            if (defaultPanel instanceof ViewStub) {
                defaultPanel = ((ViewStub) defaultPanel).inflate();
            }
            return (ViewGroup) defaultPanel;
        }
        if (defaultPanel != null) {
            ViewParent parent = defaultPanel.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(defaultPanel);
            }
        }
        if (customPanel instanceof ViewStub) {
            customPanel = ((ViewStub) customPanel).inflate();
        }
        return (ViewGroup) customPanel;
    }

    private void setupView() {
        View parentPanel = this.mWindow.findViewById(R.id.parentPanel);
        View defaultTopPanel = parentPanel.findViewById(R.id.topPanel);
        View defaultContentPanel = parentPanel.findViewById(R.id.contentPanel);
        View defaultButtonPanel = parentPanel.findViewById(R.id.buttonPanel);
        ViewGroup customPanel = (ViewGroup) parentPanel.findViewById(R.id.customPanel);
        setupCustomContent(customPanel);
        View customTopPanel = customPanel.findViewById(R.id.topPanel);
        View customContentPanel = customPanel.findViewById(R.id.contentPanel);
        View customButtonPanel = customPanel.findViewById(R.id.buttonPanel);
        ViewGroup topPanel = resolvePanel(customTopPanel, defaultTopPanel);
        ViewGroup contentPanel = resolvePanel(customContentPanel, defaultContentPanel);
        ViewGroup buttonPanel = resolvePanel(customButtonPanel, defaultButtonPanel);
        setupContent(contentPanel);
        setupButtons(buttonPanel);
        setupTitle(topPanel);
        boolean hasCustomPanel = (customPanel == null || customPanel.getVisibility() == 8) ? false : true;
        boolean hasTopPanel = (topPanel == null || topPanel.getVisibility() == 8) ? false : true;
        boolean hasButtonPanel = (buttonPanel == null || buttonPanel.getVisibility() == 8) ? false : true;
        if (!(hasButtonPanel || contentPanel == null)) {
            View spacer = contentPanel.findViewById(R.id.textSpacerNoButtons);
            if (spacer != null) {
                spacer.setVisibility(0);
            }
        }
        if (hasTopPanel && this.mScrollView != null) {
            this.mScrollView.setClipToPadding(true);
        }
        if (!hasCustomPanel) {
            View content = this.mListView != null ? this.mListView : this.mScrollView;
            if (content != null) {
                content.setScrollIndicators((hasTopPanel ? 1 : 0) | (hasButtonPanel ? RainSurfaceView.RAIN_LEVEL_SHOWER : 0), RainSurfaceView.RAIN_LEVEL_DOWNPOUR);
            }
        }
        TypedArray a = this.mContext.obtainStyledAttributes(null, R.styleable.OPAlertDialog, 16842845, 0);
        setBackground(a, topPanel, contentPanel, customPanel, buttonPanel, hasTopPanel, hasCustomPanel, hasButtonPanel);
        a.recycle();
    }

    private void setupCustomContent(ViewGroup customPanel) {
        View customView;
        boolean hasCustomView = false;
        if (this.mView != null) {
            customView = this.mView;
        } else if (this.mViewLayoutResId != 0) {
            customView = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, customPanel, false);
        } else {
            customView = null;
        }
        if (customView != null) {
            hasCustomView = true;
        }
        if (!(hasCustomView && canTextInput(customView))) {
            this.mWindow.setFlags(AccessibilityNodeInfoCompat.ACTION_SET_SELECTION, AccessibilityNodeInfoCompat.ACTION_SET_SELECTION);
        }
        if (hasCustomView) {
            FrameLayout custom = (FrameLayout) this.mWindow.findViewById(16908331);
            custom.addView(customView, new LayoutParams(-1, -1));
            if (this.mViewSpacingSpecified) {
                custom.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0.0f;
                return;
            }
            return;
        }
        customPanel.setVisibility(DetectedActivity.RUNNING);
    }

    private void setupTitle(ViewGroup topPanel) {
        boolean hasTextTitle = false;
        if (this.mCustomTitleView != null) {
            topPanel.addView(this.mCustomTitleView, 0, new LayoutParams(-1, -2));
            this.mWindow.findViewById(R.id.title_template).setVisibility(DetectedActivity.RUNNING);
            return;
        }
        this.mIconView = (ImageView) this.mWindow.findViewById(16908294);
        if (!TextUtils.isEmpty(this.mTitle)) {
            hasTextTitle = true;
        }
        if (hasTextTitle) {
            this.mTitleView = (TextView) this.mWindow.findViewById(R.id.alertTitle);
            this.mTitleView.setText(this.mTitle);
            if (this.mIconId != 0) {
                this.mIconView.setImageResource(this.mIconId);
                return;
            } else if (this.mIcon != null) {
                this.mIconView.setImageDrawable(this.mIcon);
                return;
            } else {
                this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                this.mIconView.setVisibility(DetectedActivity.RUNNING);
                return;
            }
        }
        this.mWindow.findViewById(R.id.title_template).setVisibility(DetectedActivity.RUNNING);
        this.mIconView.setVisibility(DetectedActivity.RUNNING);
        topPanel.setVisibility(DetectedActivity.RUNNING);
    }

    private void setupContent(ViewGroup contentPanel) {
        this.mScrollView = (ScrollView) contentPanel.findViewById(R.id.scrollView);
        this.mScrollView.setFocusable(false);
        this.mMessageView = (TextView) contentPanel.findViewById(16908299);
        if (this.mMessageView != null) {
            if (this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
                return;
            }
            this.mMessageView.setVisibility(DetectedActivity.RUNNING);
            this.mScrollView.removeView(this.mMessageView);
            if (this.mListView != null) {
                ViewGroup scrollParent = (ViewGroup) this.mScrollView.getParent();
                int childIndex = scrollParent.indexOfChild(this.mScrollView);
                scrollParent.removeViewAt(childIndex);
                scrollParent.addView(this.mListView, childIndex, new LayoutParams(-1, -1));
                return;
            }
            contentPanel.setVisibility(DetectedActivity.RUNNING);
        }
    }

    private static void manageScrollIndicators(View v, View upIndicator, View downIndicator) {
        int i = 0;
        if (upIndicator != null) {
            upIndicator.setVisibility(v.canScrollVertically(-1) ? 0 : 4);
        }
        if (downIndicator != null) {
            if (!v.canScrollVertically(1)) {
                i = 4;
            }
            downIndicator.setVisibility(i);
        }
    }

    private void setupButtons(ViewGroup buttonPanel) {
        boolean hasButtons = false;
        int whichButtons = 0;
        this.mButtonPositive = (Button) buttonPanel.findViewById(16908313);
        this.mButtonPositive.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(DetectedActivity.RUNNING);
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            whichButtons = 0 | 1;
        }
        this.mButtonNegative = (Button) buttonPanel.findViewById(16908314);
        this.mButtonNegative.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(DetectedActivity.RUNNING);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            whichButtons |= 2;
        }
        this.mButtonNeutral = (Button) buttonPanel.findViewById(16908315);
        this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(DetectedActivity.RUNNING);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            whichButtons |= 4;
        }
        if (shouldCenterSingleButton(this.mContext)) {
            if (whichButtons == 1) {
                centerButton(this.mButtonPositive);
            } else if (whichButtons == 2) {
                centerButton(this.mButtonNegative);
            } else if (whichButtons == 4) {
                centerButton(this.mButtonNeutral);
            }
        }
        if (whichButtons != 0) {
            hasButtons = true;
        }
        if (!hasButtons) {
            buttonPanel.setVisibility(DetectedActivity.RUNNING);
        }
    }

    private void centerButton(Button button) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.gravity = 1;
        params.weight = 0.5f;
        button.setLayoutParams(params);
    }

    private void setBackground(TypedArray a, View topPanel, View contentPanel, View customPanel, View buttonPanel, boolean hasTitle, boolean hasCustomView, boolean hasButtons) {
        int fullBright = 0;
        int bottomMedium = 0;
        View[] views = new View[4];
        boolean[] light = new boolean[4];
        View lastView = null;
        boolean lastLight = false;
        int pos = 0;
        if (hasTitle) {
            views[0] = topPanel;
            light[0] = false;
            pos = 0 + 1;
        }
        if (contentPanel.getVisibility() == 8) {
            contentPanel = null;
        }
        views[pos] = contentPanel;
        light[pos] = this.mListView != null;
        pos++;
        if (hasCustomView) {
            views[pos] = customPanel;
            light[pos] = this.mForceInverseBackground;
            pos++;
        }
        if (hasButtons) {
            views[pos] = buttonPanel;
            light[pos] = true;
        }
        boolean setView = false;
        for (pos = 0; pos < views.length; pos++) {
            View v = views[pos];
            if (v != null) {
                if (lastView != null) {
                    if (setView) {
                        lastView.setBackgroundResource(lastLight ? 0 : 0);
                    } else {
                        lastView.setBackgroundResource(lastLight ? 0 : 0);
                    }
                    setView = true;
                }
                lastView = v;
                lastLight = light[pos];
            }
        }
        if (lastView != null) {
            if (setView) {
                if (!lastLight) {
                    bottomMedium = 0;
                } else if (!hasButtons) {
                    bottomMedium = 0;
                }
                lastView.setBackgroundResource(bottomMedium);
            } else {
                if (!lastLight) {
                    fullBright = 0;
                }
                lastView.setBackgroundResource(fullBright);
            }
        }
        ListView listView = this.mListView;
        if (listView != null && this.mAdapter != null) {
            listView.setAdapter(this.mAdapter);
            int checkedItem = this.mCheckedItem;
            if (checkedItem > -1) {
                listView.setItemChecked(checkedItem, true);
                listView.setSelection(checkedItem);
            }
        }
    }
}
