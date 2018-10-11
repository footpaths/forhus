package luan.pc.husnew;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnOffReceiver extends BroadcastReceiver {
    private final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
    Context memoContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        this.memoContext = context;
        if(Intent.ACTION_SCREEN_OFF.equals(action))
        {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.");
        }
        if(Intent.ACTION_SCREEN_ON.equals(action))
        {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn on.");
        }
        Log.d("SCREEN_TOGGLE_TAG", "StartUpReceiver onReceive() was called");

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Intent newIntent=new Intent(context.getApplicationContext(),ScreenOnOffBackgroundService.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.memoContext.startActivity(newIntent);
            Log.d("SCREEN_TOGGLE_TAG", "StartUpReceiver onReceive() was called again");

        }


    }
}
