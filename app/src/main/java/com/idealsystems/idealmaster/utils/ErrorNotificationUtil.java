package com.idealsystems.idealmaster.utils;

import android.content.Context;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.activities.LoginActivity;
import com.idealsystems.idealmaster.constants.IntentConstants;
import com.idealsystems.idealmaster.model.IdealMasterService;
import com.idealsystems.idealmaster.model.IdealTransaction;
import com.idealsystems.idealmaster.model.Order;
import com.idealsystems.idealmaster.settings.AppSettings;

import io.realm.Realm;

/**
 * Created by acerkinght on 9/9/16.
 */
public class ErrorNotificationUtil {
    public static void showErrorForCode(int code, Context context) {
        if (code == 401 && !(context instanceof LoginActivity)) {
            Realm
                    .getDefaultInstance()
                    .executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm
                                    .where(Order.class)
                                    .findAll()
                                    .deleteAllFromRealm();
                            realm
                                    .where(IdealMasterService.class)
                                    .findAll()
                                    .deleteAllFromRealm();
                            realm
                                    .where(IdealTransaction.class)
                                    .findAll()
                                    .deleteAllFromRealm();
                        }
                    });

            AppSettings.sharedSettings(context).setIsUserLoggedIn(false);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(IntentConstants.EXTRA_ENDS_SESSION, true);
            context.startActivity(intent);
            return;
        }

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
