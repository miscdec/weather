package com.opweather.util;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.widget.CenterAlertDialog;

public class AlertUtils {

    static class AnonymousClass_2 implements OnClickListener {
        final /* synthetic */ Context val$context;

        AnonymousClass_2(Context context) {
            this.val$context = context;
        }

        public void onClick(DialogInterface dialog, int id) {
            this.val$context.startActivity(new Intent("android.settings.SETTINGS"));
        }
    }

    static class AnonymousClass_3 implements OnClickListener {
        final /* synthetic */ Activity val$activity;

        AnonymousClass_3(Activity activity) {
            this.val$activity = activity;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$activity.finish();
        }
    }

    static class AnonymousClass_4 implements OnClickListener {
        final /* synthetic */ Activity val$activity;

        AnonymousClass_4(Activity activity) {
            this.val$activity = activity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", this.val$activity.getPackageName(), null));
            this.val$activity.startActivity(intent);
        }
    }

    private AlertUtils() {
    }

    public static CenterAlertDialog showCenterAlert(Context context, String title, String message, String
            confirmText, View.OnClickListener confirm, String cancelText, View.OnClickListener cancel) {
        CenterAlertDialog alert = new CenterAlertDialog(context);
        if (!TextUtils.isEmpty(message)) {
            alert.setMessage(message);
        }
        if (!TextUtils.isEmpty(title)) {
            alert.setTitle(title);
        }
        alert.setConfirmOnClickListener(confirm);
        alert.setCancelOnClickListener(cancel);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        return alert;
    }

    public static Dialog showNoConnectionDialog(Context context) {
        Builder builder = new Builder(context);
        builder.setMessage((int) R.string.warning_string_no_network).setPositiveButton((int) R.string.open, new
                AnonymousClass_2(context)).setNegativeButton((int) R.string.warning_string_dismiss, new
                OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog showPermissionDialog(Context context, String alert, OnClickListener posListener) {
        Builder builder = new Builder(context);
        builder.setMessage(alert).setPositiveButton(R.string.warning_string_ok, posListener);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    public static Dialog showSimpleAlertDialog(Context context, String message, OnClickListener listener) {
        Builder builder = new Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, listener);
        Dialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog showProgressDialog(Context context, String message) {
        Builder builder = new Builder(context);
        int padding = UIUtil.dip2px(context, 8.0f);
        LinearLayout layout = new LinearLayout(context.getApplicationContext());
        layout.setPadding(padding, 0, padding, 0);
        layout.setBackgroundColor(context.getColor(R.color.white));
        layout.addView(new ProgressBar(context.getApplicationContext()));
        LayoutParams flp = new LayoutParams(-1, -1);
        if (TextUtils.isEmpty(message)) {
            layout.setGravity(Gravity.CENTER);
        } else {
            layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            TextView textView = new TextView(context.getApplicationContext());
            textView.setText(message);
            textView.setTextColor(context.getResources().getColor(R.color.black));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(-2, -2);
            llp.leftMargin = padding;
            layout.addView(textView, llp);
        }
        layout.setLayoutParams(flp);
        builder.setView(layout);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().getDecorView().findViewById(android.R.id.custom).setLayoutParams(new LayoutParams(-1, 200));
        return dialog;
    }

    public static Dialog showProgressDialog(Context context) {
        return showProgressDialog(context, null);
    }

    public static void closeDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Throwable th) {
            }
        }
    }

    public static Dialog showNonePermissionDialog(Activity activity) {
        Dialog dialog = new Builder(activity).setMessage((int) R.string.dialog_necessary_permissions)
                .setPositiveButton((int) R.string.dialog_go_to_settings, new AnonymousClass_4(activity))
                .setNegativeButton((int) R.string.dialog_exit, new AnonymousClass_3(activity)).create();
        dialog.show();
        dialog.setCancelable(false);
        return dialog;
    }
}
