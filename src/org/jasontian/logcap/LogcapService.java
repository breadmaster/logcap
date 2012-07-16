/*
 *   LogCap: Capture system logs to files.
 *   Copyright (C) 2012  Jason Tian
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jasontian.logcap;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jasontian.logcap.R;
import org.jasontian.logcap.util.Util;

import java.io.IOException;
import java.util.HashSet;

/**
 * @author Jason Tian
 */
public class LogcapService extends Service {

    private HashSet<Process> mLogcatProcesses;
    
    public static final String PROPERTY_NAME = "debug.logcap.start";

    @Override
    public void onCreate() {
        super.onCreate();
        mLogcatProcesses = new HashSet<Process>(4);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean start = intent.getBooleanExtra(Util.EXTRA_START, false);
        if (start) {
            System.setProperty(PROPERTY_NAME, "true");
            Notification n = new Notification(R.drawable.star_notify,
                    getText(R.string.notification_ticker),
                    System.currentTimeMillis());
            PendingIntent pi = PendingIntent.getActivity(this, 0,
                    new Intent().setClass(this, MainActivity.class), 0);
            n.setLatestEventInfo(this, getText(R.string.app_name),
                    getText(R.string.notification_content), pi
                    );
            startForeground(R.drawable.star_notify, n);
            String[] buffers = intent.getStringArrayExtra(Util.EXTRA_BUFFER);
            String format = intent.getStringExtra(Util.EXTRA_FORMAT);
            try {
                synchronized (mLogcatProcesses) {
                    clearLogcapProc();
                    for (String buf : buffers) {
                        mLogcatProcesses.add(Util.capture(buf, format));
                    }
                    Log.d(App.LOG_TAG, "log cap proc started");
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.msg_start_failed,
                        Toast.LENGTH_LONG).show();
                Log.e(Util.TAG, getString(R.string.msg_start_failed), e);
                stopSelf();
            }
        } else {
            System.setProperty(PROPERTY_NAME, "false");
            synchronized (mLogcatProcesses) {
                clearLogcapProc();               
            }
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void clearLogcapProc() {
        for (Process proc : mLogcatProcesses) {
            if (proc != null) {
                proc.destroy();
                Log.d(Util.TAG, "proc destroyed");
            }
        }
        mLogcatProcesses.clear();
        
    }

    @Override
    public void onDestroy() {
        Log.v(Util.TAG, "service destroied");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.v(Util.TAG, "service low memory");
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
