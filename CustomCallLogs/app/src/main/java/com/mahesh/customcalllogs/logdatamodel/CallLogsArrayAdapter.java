package com.mahesh.customcalllogs.logdatamodel;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahesh.customcalllogs.R;
import com.mahesh.customcalllogs.util.CallLogUtils;

import java.util.ArrayList;

public class CallLogsArrayAdapter extends ArrayAdapter<LogEntry> {

    private ArrayList<LogEntry> mLogList;
    private Context mContext;

    public CallLogsArrayAdapter(Context context, ArrayList<LogEntry> callLogEntry) {
        super(context, R.layout.call_log_item, callLogEntry);
        mLogList = callLogEntry;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mLogList.size();
    }

    @Override
    public LogEntry getItem(int pos) {
        return mLogList.get(pos);
    }

    @Override
    public void notifyDataSetChanged() {
        readCallLogs();

        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LogEntry currEntry = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.call_log_item, parent, false);
        }

        // Update the background color for the row
        LinearLayout lytCallLog = (LinearLayout)convertView.findViewById(R.id.layout_section_label);
        if (position%2 == 0){
            lytCallLog.setBackgroundColor(AppCompatResources.getColorStateList(getContext(), R.color.colorLogItemEven).getDefaultColor());
        } else {
            lytCallLog.setBackgroundColor(AppCompatResources.getColorStateList(getContext(), R.color.colorLogItemOdd).getDefaultColor());
        }

        // Lookup view for data population
        TextView tvPhone = (TextView) convertView.findViewById(R.id.logPhoneNumber);
        TextView tvDate = (TextView) convertView.findViewById(R.id.logDate);
        TextView tvType = (TextView) convertView.findViewById(R.id.logType);
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.logImage);

        // Populate the data into the template view using the data object
        tvPhone.setText(currEntry.getNumber());
        tvDate.setText(currEntry.getDate());
        tvType.setText(currEntry.getCallType());
        ivImage.setImageDrawable(AppCompatResources.getDrawable(convertView.getContext(), currEntry.getCallTypeResource()));

        // Return the completed view to render on screen
        return convertView;
    }

    // Read the call logs from content provider of call log
    // and store the records in an array list
    private void readCallLogs() {
        // First clear the existing records
        mLogList.clear();

        int currentEntry = 0;

        // Get the call logs records from content provider in descending order of date-time
        Cursor callLogCursor = mContext.getContentResolver().query(
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

                if(++currentEntry >= CallLogUtils.MAX_ENTRIES) {
                    // exit the while loop after reading max record
                    break;
                }
            }
            callLogCursor.close();
        }
    }
}
