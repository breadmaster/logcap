package org.jasontian.logcap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver{
    
    public static boolean mBootCompleted = false;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String setting = System.getProperty(LogcapService.PROPERTY_NAME, "true");
            if ("true".endsWith(setting)) {
                Log.d(App.LOG_TAG, "boot completed! start logcapService");
                mBootCompleted = true;
                Intent activity = new Intent(context, MainActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activity);
            }      
            Log.d(App.LOG_TAG, "boot completed! but not start logcapService");
        }
    }

}
