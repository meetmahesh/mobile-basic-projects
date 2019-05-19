package com.mahesh.customcalllogs.phoneutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.mahesh.customcalllogs.util.CallLogUtils;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

        if(stateStr != null && stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            try {
                // added sleep of 100ms, so that original call log will be updated first,
                // and based on those call logs, our records will be updated
                // This is needed for low end devices
                Thread.sleep(100);
            } catch (Exception ex) {
                // Do nothing
            } finally {
                context.sendBroadcast(new Intent(CallLogUtils.ACTION_UPDATE_LOG_ENTRY));
            }
        }

    }
}
