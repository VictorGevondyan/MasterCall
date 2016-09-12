package com.flycode.paradoxidealmaster.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.paradoxidealmaster.R;

/**
 * Created by acerkinght on 9/9/16.
 */
public class ErrorNotificationUtil {
    public static void showErrorForCode(int code, Context context) {
        int content;

        if (code == 0) {
            content = R.string.network_issue;
        } else if (code == 500) {
            content = R.string.server_issue;
        } else {
            content = R.string.wrong_data;
        }

        new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(content)
                .positiveText(R.string.ok)
                .show();
    }
}
