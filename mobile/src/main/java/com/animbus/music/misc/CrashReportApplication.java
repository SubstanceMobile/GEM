package com.animbus.music.misc;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.animbus.music.R;
import com.animbus.music.ui.activity.IssueReportingActivity;
import com.google.repacked.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by Adrian on 11/14/2015.
 */
public class CrashReportApplication extends Application implements Thread.UncaughtExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        final String stackTrace = ExceptionUtils.getStackTrace(ex);
        final String type = ex.getClass().getSimpleName();

        new MaterialDialog.Builder(this)
                .content(R.string.msg_crashed)
                .positiveText(android.R.string.ok)
                .neutralText(R.string.crashed_report)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        new MaterialDialog.Builder(CrashReportApplication.this)
                                .content(R.string.msg_crash_message)
                                .input(R.string.crash_hint, 0, true, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                        sendReport(charSequence.toString(), stackTrace, type);
                                    }
                                })
                                .positiveText(android.R.string.ok)
                                .negativeText(android.R.string.cancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                        sendReport(materialDialog.getInputEditText().getText().toString(), stackTrace, type);
                                    }
                                })
                                .build().show();
                    }
                })
                .build().show();
    }

    private void sendReport(@Nullable String message, @NonNull String stackTrace, @Nullable String type) {
        String msg = !TextUtils.isEmpty(message) ? message : "No Message Provided";
        startActivity(new Intent(CrashReportApplication.this, IssueReportingActivity.class)
                .putExtra("error", stackTrace)
                .putExtra("msg", msg)
                .putExtra("type", type));
    }
}
