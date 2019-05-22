package com.mahesh.customcalllogs.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.mahesh.customcalllogs.MainActivity;

public class CallReceiver extends BroadcastReceiver {
    // Previous call state
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    // If call type is incoming
    private static boolean isIncoming;

    @Override
    public void onReceive(Context context, Intent intent) {

        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        int state = 0;
        if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            state = TelephonyManager.CALL_STATE_RINGING;
        }

        onCallStateChanged(context, state, number);
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            // No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // This was missed call
                    context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_LOG_ENTRY));
                } else if (isIncoming) {
                    // This was incoming call
                    context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_LOG_ENTRY));
                } else {
                    // This was outgoing call
                    context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_LOG_ENTRY));
                }
                break;
        }
        lastState = state;
    }
}
