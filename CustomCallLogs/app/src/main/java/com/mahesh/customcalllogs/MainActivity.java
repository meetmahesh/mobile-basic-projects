package com.mahesh.customcalllogs;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // intent for updating entries for call log
    public static final String ACTION_UPDATE_LOG_ENTRY = "com.mahesh.advancedcalllogs.updateCallLog";

    // Array for entries of call logs
    public static ArrayList<LogEntry> allCallList;

    // Max entries shown to the user
    private final int MAX_ENTRIES = 50;

    // Progress bar while updating the log entries
    private static ProgressBar mPrBar;

    // List view for call log entries
    ListView mainListView;
    ArrayAdapter<MainActivity.LogEntry> listAdapter;

    // Layout for pull-to refresh feature
    SwipeRefreshLayout pullToRefresh;

    // List of dangerous permissions used in the application
    String[] PERMISSIONS = {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show popup for dangerous permission
        checkPermissionGranted();

        mPrBar = findViewById(R.id.logLoader);
        allCallList = new ArrayList<LogEntry>();

        // register intent for updating call log entries
        IntentFilter updateLogAndUI = new IntentFilter();
        updateLogAndUI.addAction(ACTION_UPDATE_LOG_ENTRY);
        this.registerReceiver(mReceiver, updateLogAndUI);

        // Read the call log entries and add to array
        new ReadLogs().execute();

        // Add the entries of call logs from array to list view
        mainListView = (ListView) findViewById(R.id.listView);;
        listAdapter = new CallLogsArrayAdapter(this, allCallList);
        mainListView.setAdapter(listAdapter);

        // Logic to update the entries of call log, when user pull down the list view
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ReadLogs().execute();
            }
        });
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_UPDATE_LOG_ENTRY) {
                // Update the call log entries from actual call log
                new ReadLogs().execute();
            }
        }
    };

    // Check if user granted the permissions
    // if not, request him to grant the permission
    private void checkPermissionGranted() {
        boolean requestPermissions = false;
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions = true;
                break;
            }
        }
        if (requestPermissions) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0);
        }
    }

    // Read the call logs from content provider of call log
    // and store the records in an array list
    private void readCallLogs() {

        // First clear the existing records
        allCallList.clear();

        int currentEntry = 0;

        // Get the call logs records from content provider in descending order of date-time
        Cursor callLogCursor = getContentResolver().query(
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
                String dateString = getDateTime(dateTimeMillis);

                LogEntry callLogentry = new LogEntry(number, dateString, callType);

                allCallList.add(callLogentry);

                if(++currentEntry >= MAX_ENTRIES) {
                    // exit the while loop after reading max record
                    break;
                }
            }
            callLogCursor.close();
        }
    }

    // Convert the date-time from call log into readable format
    private String getDateTime(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getInstance();
        return sdf.format(date);
    }

    // Seperate task for reading the call logs
    public class ReadLogs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mPrBar != null) {
                // Show the progress bar to the user while loading the call logs
                mPrBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Read the call logs and store locally for the application
            readCallLogs();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // On completion, hide the progress bar, and update the list view
            if (mPrBar != null) {
                mPrBar.setVisibility(View.INVISIBLE);
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing(true);
                    listAdapter.notifyDataSetChanged();
                    mainListView.invalidateViews();
                    pullToRefresh.setRefreshing(false);
                }
            });
            super.onPostExecute(result);
        }
    }

    public static class CallLogsArrayAdapter extends ArrayAdapter<LogEntry> {

        public CallLogsArrayAdapter(Context context, ArrayList<LogEntry> callLogEntry) {
            super(context, R.layout.call_log_item, callLogEntry);
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

            int callType = currEntry.getCallType();
            if(callType == CallLog.Calls.OUTGOING_TYPE) {
                tvType.setText(R.string.outgoingCallType);
                ivImage.setImageDrawable(AppCompatResources.getDrawable(convertView.getContext(), R.drawable.outgoingphone));
            } else if (callType == CallLog.Calls.INCOMING_TYPE) {
                tvType.setText(R.string.incomingCallType);
                ivImage.setImageDrawable(AppCompatResources.getDrawable(convertView.getContext(), R.drawable.incomingphone));
            } else if (callType == CallLog.Calls.MISSED_TYPE) {
                tvType.setText(R.string.missedCallType);
                ivImage.setImageDrawable(AppCompatResources.getDrawable(convertView.getContext(), R.drawable.missedphone));
            }

            // Return the completed view to render on screen
            return convertView;
        }
    }

    // Data object to hold call log entry
    private class LogEntry {
        private String number;
        private String date;
        private int type;

        public LogEntry(String number, String date, int type) {
            this.number = number;
            this.date = date;
            this.type = type;
        }

        // Contact Number
        public String getNumber() {
            return number;
        }

        // Date time of call
        public String getDate() {
            return date;
        }

        // type of call
        public int getCallType() {
            return type;
        }
    }

}
