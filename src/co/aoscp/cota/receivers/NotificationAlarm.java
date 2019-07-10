package co.aoscp.cota.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.aoscp.cota.UpdateManager;
import co.aoscp.cota.utils.NetworkUtils;

public class NotificationAlarm extends BroadcastReceiver {

    private UpdateManager mUpdateManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mUpdateManager == null) {
            mUpdateManager = new UpdateManager(context, true, true);
        }
        if (NetworkUtils.isNetworkAvailable(context)) {
            mUpdateManager.check(true);
        }
    }
}