package com.flycode.paradoxidealmaster.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.flycode.paradoxidealmaster.R;


/**
 * Created - Schumakher on 31-Aug-16.
 */
public class LoadingProgressDialog extends ProgressDialog {

    public LoadingProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }
}
