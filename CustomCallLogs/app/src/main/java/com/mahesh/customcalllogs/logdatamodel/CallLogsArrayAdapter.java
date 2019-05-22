package com.mahesh.customcalllogs.logdatamodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.provider.CallLog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.mahesh.customcalllogs.R;
import com.mahesh.customcalllogs.databinding.LogEntryViewBinding;
import com.mahesh.customcalllogs.util.CallLogUtils;

import java.util.ArrayList;

public class CallLogsArrayAdapter extends ArrayAdapter<LogEntry> {

    // Max entries shown to the user
    private final int MAX_ENTRIES = 50;

    // Array list to hold the call log data
    private ArrayList<LogEntry> mLogList;

    // check for Call log permission if granted
    private boolean callLogPermissionGranted = false;

    public CallLogsArrayAdapter(Context context, ArrayList<LogEntry> callLogEntry) {
        super(context, R.layout.call_log_item, callLogEntry);
        mLogList = callLogEntry;
        context.registerReceiver(mReceiver, new IntentFilter(CallLogUtils.ACTION_UPDATE_LOG_ENTRY));
    }

    public void setCallLogPermissionGranted (boolean permission) {
        callLogPermissionGranted = permission;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readCallLogs();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LogEntryViewBinding logEntryViewBinding;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.call_log_item, parent, false);
            logEntryViewBinding = DataBindingUtil.bind(convertView);
            convertView.setTag(logEntryViewBinding);
        } else {
            logEntryViewBinding = (LogEntryViewBinding)convertView.getTag();
        }

        // Update the background color for the row
        LinearLayout lytCallLog = (LinearLayout)convertView.findViewById(R.id.layout_section_label);
        if (position%2 == 0){
            lytCallLog.setBackgroundColor(AppCompatResources.getColorStateList(getContext(), R.color.colorLogItemEven).getDefaultColor());
        } else {
            lytCallLog.setBackgroundColor(AppCompatResources.getColorStateList(getContext(), R.color.colorLogItemOdd).getDefaultColor());
        }

        // Return the completed view to render on screen
        logEntryViewBinding.setCalllogmodel(mLogList.get(position));
        return logEntryViewBinding.getRoot();
    }

    // Read the call logs from content provider of call log
    // and store the records in an array list
    private void readCallLogs() {
        if (callLogPermissionGranted) {
            // Clear the existing records from array
            mLogList.clear();

            int currentEntry = 0;

            // Get the call logs records from content provider in descending order of date-time
            Cursor callLogCursor = getContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, null, null, null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (callLogCursor != null) {
                // iterate through the records, and store in the array list
                while (callLogCursor.moveToNext()) {
                    String number = callLogCursor.getString(callLogCursor
                            .getColumnIndex(CallLog.Calls.NUMBER));
                    int callType = callLogCursor.getInt(callLogCursor
                            .getColumnIndex(CallLog.Calls.TYPE));
                    long dateTimeMillis = callLogCursor.getLong(callLogCursor
                            .getColumnIndex(CallLog.Calls.DATE));

                    LogEntry callLogentry = new LogEntry(number, dateTimeMillis, callType);

                    mLogList.add(callLogentry);

                    if (++currentEntry >= MAX_ENTRIES) {
                        // exit the while loop after reading max record
                        break;
                    }
                }
                callLogCursor.close();
            }
        }
        notifyDataSetChanged();
        getContext().sendBroadcast(new Intent(CallLogUtils.ACTION_UPDATE_SWIPE_UI));
    }
}
