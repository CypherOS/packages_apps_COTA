package co.aoscp.cota.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.aoscp.cota.UpdateSystem;

public class DownloadReceiver extends BroadcastReceiver {
    public static final String CHECK_DOWNLOADS_FINISHED = "co.aoscp.cota.Utils.CHECK_DOWNLOADS_FINISHED";
    public static final String CHECK_DOWNLOADS_ID = "co.aoscp.cota.Utils.CHECK_DOWNLOADS_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        Intent i = new Intent(context, UpdateSystem.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(CHECK_DOWNLOADS_FINISHED, true);
        i.putExtra(CHECK_DOWNLOADS_ID, id);
        context.startActivity(i);
    }

}