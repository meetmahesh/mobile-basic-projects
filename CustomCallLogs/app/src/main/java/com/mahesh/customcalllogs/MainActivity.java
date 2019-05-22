package com.mahesh.customcalllogs;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.mahesh.customcalllogs.logdatamodel.CallLogsArrayAdapter;
import com.mahesh.customcalllogs.logdatamodel.LogEntry;
import com.mahesh.customcalllogs.phoneutil.CallReceiver;
import com.mahesh.customcalllogs.util.CallLogUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Array for entries of call logs
    public ArrayList<LogEntry> allCallList;

    // List view for call log entries
    ListView mainListView;
    CallLogsArrayAdapter listAdapter;

    // For updating logs dynamically after call ends
    CallReceiver mCallReceiver;

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

        // Add the entries of call logs from array to list view
        mainListView = (ListView) findViewById(R.id.listView);;
        allCallList = new ArrayList<LogEntry>();
        listAdapter = new CallLogsArrayAdapter(this, allCallList);
        mainListView.setAdapter(listAdapter);

        // Show popup for dangerous permission
        checkPermissionGranted();

        IntentFilter updateSwipeUI = new IntentFilter();
        updateSwipeUI.addAction(CallLogUtils.ACTION_UPDATE_SWIPE_UI);
        this.registerReceiver(mReceiver, updateSwipeUI);

        // Logic to update the entries of call log, when user pull down the list view
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendBroadcast(new Intent(CallLogUtils.ACTION_UPDATE_LOG_ENTRY));
            }
        });

        sendBroadcast(new Intent(CallLogUtils.ACTION_UPDATE_LOG_ENTRY));
    }

    // After updating logs in ListView, hide the progress bar of swipe layout
    // As logs are updated asynchronously, progress bar will also update asynchronously
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (pullToRefresh != null) {
                pullToRefresh.setRefreshing(false);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (PERMISSIONS[0].equals(permissions[i])) { // For Call Log permission
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) { // permission is granted for call log
                        listAdapter.setCallLogPermissionGranted(true);

                        sendBroadcast(new Intent(CallLogUtils.ACTION_UPDATE_LOG_ENTRY));
                    } else {
                        // Show the pop up for permission denied
                        CallLogUtils.showAlertDialogForCallLogPermission(this);

                        // No need to check the phone state permission in next iteration, as we are closing the app
                        break;
                    }
                } else if (PERMISSIONS[1].equals(permissions[i])) { // For phone state permission
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // permission is denied for phone/call state
                        boolean showRationale = shouldShowRequestPermissionRationale(permissions[i]);
                        if(showRationale) {
                            // Show the pop up for phone state permission denied
                            CallLogUtils.showAlertDialogForPhoneStatePermission(this);
                        }
                    } else {
                        startCallStateReceiver();
                    }
                }
            }
        }
    }

    // Check if user granted the permissions
    // if not, request him to grant the permission
    private void checkPermissionGranted() {
        boolean requestPermissions = false;
        if (ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions = true;
        } else {
            listAdapter.setCallLogPermissionGranted(true);
        }

        if (ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions = true;
        } else {
            startCallStateReceiver();
        }

        if (requestPermissions) {
            // though requesting for all permissions related to this app,
            // framework will take care to request only necessary permission
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0);
        }
    }

    // Start the receiver to handle call state
    private void startCallStateReceiver() {
        mCallReceiver = new CallReceiver();

        // register intent for handling call state
        IntentFilter forCallState = new IntentFilter();
        forCallState.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(mCallReceiver, forCallState);
    }

}
