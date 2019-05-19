package com.mahesh.customcalllogs.logdatamodel;

import android.databinding.BaseObservable;
import android.provider.CallLog;
import com.mahesh.customcalllogs.R;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntry extends BaseObservable {
    private String number;
    private String date;
    private String callType;
    private int callImageResource;

    public LogEntry(String number, long date, int type) {
        this.number = number;
        this.date = getDateTime(date);
        if(type == CallLog.Calls.OUTGOING_TYPE) {
            callType = "Outgoing";
            callImageResource = R.drawable.outgoingphone;
        } else if (type == CallLog.Calls.INCOMING_TYPE) {
            callType = "Incoming";
            callImageResource = R.drawable.incomingphone;
        } else if (type == CallLog.Calls.MISSED_TYPE) {
            callType = "Missed";
            callImageResource = R.drawable.missedphone;
        } else if (type == CallLog.Calls.REJECTED_TYPE) {
            callType = "Rejected";
            callImageResource = R.drawable.unknownphone;
        } else {
            callType = "Unknown";
            callImageResource = R.drawable.unknownphone;
        }
    }

    // Contact Number
    public String getNumber() {
        return number;
    }

    // Date time of call
    public String getDate() {
        return date;
    }

    // type of call in string
    public String getCallType() {
        return callType;
    }

    // type of call
    public int getCallTypeResource() {
        return callImageResource;
    }

    // Convert the date-time from call log into readable format
    private String getDateTime(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM MM-dd-YYYY");
        return sdf.format(date);
    }
}
