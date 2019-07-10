package co.aoscp.cota.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.Service;
import android.os.IBinder;
import android.os.Handler;
import android.os.HandlerThread;

import co.aoscp.cota.R;
import co.aoscp.cota.UpdateSystem;
import co.aoscp.cota.UpdateManager;
import co.aoscp.cota.UpdateManager.PackageInfo;
import co.aoscp.cota.utils.AlarmUtils;

import java.io.Serializable;

public class UpdateService extends Service {

    private static final String TAG = "COTA:UpdateService";
    public static final String FILES_INFO = "co.aoscp.cota.Utils.FILES_INFO";

    public static final int NOTIFICATION_UPDATE = 122303235;
    public static final int NOTIFICATION_INSTALL = 122303246;

    private static Context mContext;
    private HandlerThread mMainThread;
    private Handler mBgThread;

    public static void start(Context context) {
        start(context, null);
    }

    private static void start(Context context, String action) {
        Intent updateService = new Intent(context, UpdateService.class);
        updateService.setAction(action);
        context.startService(updateService);
        mContext = context;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind: Service bound");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting COTA");

        mMainThread = new HandlerThread("COTA System Update Service");
        mMainThread.start();
        mBgThread = new Handler(mMainThread.getLooper());
        AlarmUtils.setAlarm(this, true);
    }

    @Override
    public void onDestroy() {
        mMainThread.quitSafely();
        super.onDestroy();
    }

    public static class NotificationInfo implements Serializable {
        public int mNotificationId;
        public UpdateManager.PackageInfo[] mPackageInfosRom;
    }
}
