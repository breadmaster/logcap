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

import android.app.Application;
import android.util.Log;

import org.jasontian.logcap.util.Util;

/**
 * @author Jason Tian
 */
public class App extends Application {
    
    public static final String LOG_TAG = "LogCapture";
    
    public static Object mLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(Util.TAG, "app created");
    }

    @Override
    public void onTerminate() {
        Log.v(Util.TAG, "app terminated");
        super.onTerminate();
    }

}
