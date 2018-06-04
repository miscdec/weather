package com.opweather.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.opweather.R;

public class CenterAlertDialog extends Dialog {
    private TextView mCancel;
    private View.OnClickListener mCancelListener;
    private TextView mMsg;
    private TextView mOK;
    private View.OnClickListener mOKListener;
    private TextView mTitle;

    public CenterAlertDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(LayoutInflater.from(context).inflate(R.layout.center_alert_dialog, null));
        mMsg = (TextView) findViewById(R.id.center_alert_dialog_msg);
        mMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOK = (TextView) findViewById(R.id.center_alert_dialog_ok);
        mTitle = (TextView) findViewById(R.id.center_alert_dialog_title);
        mCancel = (TextView) findViewById(R.id.center_alert_dialog_cancel);
        mOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mOKListener != null) {
                    mOKListener.onClick(v);
                }
                dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCancelListener != null) {
                    mCancelListener.onClick(v);
                }
                dismiss();
            }
        });
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setMessage(String text) {
        mMsg.setText(text);
    }

    public void setConfirmVisibility(int visibility) {
        if (mOK != null) {
            mOK.setVisibility(visibility);
        }
    }

    public void setCancelVisibility(int visibility) {
        if (mCancel != null) {
            mCancel.setVisibility(visibility);
        }
    }

    public void setConfirmOnClickListener(View.OnClickListener l) {
        mOKListener = l;
    }

    public void setCancelOnClickListener(View.OnClickListener listener) {
        mCancelListener = listener;
    }

    public void setConfirmText(String text) {
        mOK.setText(text);
    }
}
