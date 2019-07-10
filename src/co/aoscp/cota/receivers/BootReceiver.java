package co.aoscp.cota.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.aoscp.cota.App;
import co.aoscp.cota.UpdateManager;
import co.aoscp.cota.utils.AlarmUtils;
import co.aoscp.cota.utils.DeviceInfoUtils;
import co.aoscp.cota.utils.PreferenceUtils;

import org.piwik.sdk.PiwikApplication;
import org.piwik.sdk.Tracker;
import org.piwik.sdk.TrackHelper;
import android.util.Log;

import java.util.HashMap;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    private UpdateManager mUpdateManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive intent received " + intent.toString());

        mUpdateManager = new UpdateManager(context, true, true);
        mUpdateManager.check(true);
        AlarmUtils.setAlarm(context, true);

        if (!PreferenceUtils.getPreference(context, PreferenceUtils.PROPERTY_FIRST_BOOT, false)) {
            Log.v(TAG, "onReceive:First boot, recording version");

            //App app = (App)context.getApplicationContext();
            App app = App.getApplication();

            HashMap<String, String> segmentation = new HashMap<>();
            segmentation.put("device", DeviceInfoUtils.getDevice());
            segmentation.put("version", DeviceInfoUtils.getExplicitVersion());
            TrackHelper.track().screen("First Boot").variable(0, "Device", DeviceInfoUtils.getDevice()).variable(1, "Version", DeviceInfoUtils.getExplicitVersion()).with(app.getTracker());
            PreferenceUtils.setPreference(context, PreferenceUtils.PROPERTY_FIRST_BOOT, true);
        }
    }
}
