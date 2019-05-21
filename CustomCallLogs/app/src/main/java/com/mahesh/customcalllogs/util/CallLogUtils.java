package com.mahesh.customcalllogs.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.mahesh.customcalllogs.R;

public class CallLogUtils {

    // intent for updating entries for call log
    public static final String ACTION_UPDATE_LOG_ENTRY = "com.mahesh.customcalllogs.updateCallLog";

    // intent for updating Swipe UI after updating the logs
    public static final String ACTION_UPDATE_SWIPE_UI = "com.mahesh.customcalllogs.updateSwipeUI";

    public static void showAlertDialogForCallLogPermission (final Activity activity) {
        showAlertDialog(activity,
                activity.getResources().getString(R.string.permissionDeniedTitle),
                activity.getResources().getString(R.string.permissionDeniedMessageForCallLog),
                true);
    }

    public static void showAlertDialogForPhoneStatePermission (final Activity activity) {
        showAlertDialog(activity,
                activity.getResources().getString(R.string.permissionDeniedTitle),
                activity.getResources().getString(R.string.permissionDeniedMessageForPhoneState),
                false);
    }

    public static void showAlertDialog (final Activity activity, String title, String message, boolean finishParentActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);

        String btnText = activity.getResources().getString(R.string.dialogButton);
        if (finishParentActivity) {
            btnText = activity.getResources().getString(R.string.exitButton);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    activity.finish();
                }
            });
        }


        builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        Dialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }
}
