/*
 * Copyright (C) 2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.aoscp.cota;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import co.aoscp.cota.R;
import co.aoscp.cota.UpdateSystem;
import co.aoscp.cota.services.UpdateService;
import co.aoscp.cota.services.UpdateService.NotificationInfo;
import co.aoscp.cota.updater.Updater;
import co.aoscp.cota.updater.Updater.PackageInfo;

public class UpdateNotification {

	private static final String UPDATE_NOTIF_CHANNEL = "SYSUPDATE";

    private Context mContext;
	private NotificationManager mNoMan;
	
	private boolean mIsShowing;

    public UpdateNotification(Context context) {
        mContext = context;
    }

    public void showUpdate(Context context, PackageInfo[] info) {
        if (info == null) return;
        Resources resources = context.getResources();
        final int color = resources.getColor(R.color.colorPrimary);
		mIsShowing = true;

        Intent intent = new Intent(context, UpdateSystem.class);
        NotificationInfo fileInfo = new NotificationInfo();
        fileInfo.mNotificationId = UpdateService.NOTIFICATION_UPDATE;
        fileInfo.mPackageInfosRom = info;
        intent.putExtra(UpdateService.FILES_INFO, fileInfo);
        PendingIntent pIntent = PendingIntent.getActivity(context, UpdateService.NOTIFICATION_UPDATE, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification.Builder builder =
                new Notification.Builder(context, UPDATE_NOTIF_CHANNEL)
                        .setSmallIcon(R.drawable.ic_update_notification)
                        .setShowWhen(false)
                        .setContentTitle(String.format(mContext.getResources().getString(
                                R.string.update_system_notification_update_available),
                                info[0].getVersion().toString()))
                        .setContentText(resources.getString(R.string.update_system_notification_update_available_desc))
                        .setOnlyAlertOnce(false)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setOngoing(true)
                        .setContentIntent(pIntent)
                        .setColor(color);

		NotificationManager mNoMan =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationChannel updateChannel = new NotificationChannel(
                UPDATE_NOTIF_CHANNEL,
                mContext.getString(R.string.update_system_notification_channel),
                NotificationManager.IMPORTANCE_HIGH);
        updateChannel.setBlockableSystem(true);
        updateChannel.enableLights(true);
        updateChannel.enableVibration(true);
        mNoMan.createNotificationChannel(updateChannel);
        mNoMan.notify(UpdateService.NOTIFICATION_UPDATE, builder.build());
    }
	
	public void showInstall() {
        Resources resources = mContext.getResources();
        final int color = resources.getColor(R.color.colorPrimary);
		mIsShowing = true;

        Intent intent = new Intent(mContext, UpdateSystem.class);
        NotificationInfo fileInfo = new NotificationInfo();
        fileInfo.mNotificationId = UpdateService.NOTIFICATION_INSTALL;
        intent.putExtra(UpdateService.FILES_INFO, fileInfo);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, UpdateService.NOTIFICATION_INSTALL, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification.Builder builder =
                new Notification.Builder(mContext, UPDATE_NOTIF_CHANNEL)
                        .setSmallIcon(R.drawable.ic_update_notification)
                        .setShowWhen(false)
                        .setContentTitle(resources.getString(R.string.update_system_notification_install_update))
                        .setContentText(resources.getString(R.string.update_system_notification_install_update_desc))
                        .setOnlyAlertOnce(false)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setOngoing(true)
                        .setContentIntent(pIntent)
                        .setColor(color);

		NotificationManager mNoMan =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationChannel updateChannel = new NotificationChannel(
                UPDATE_NOTIF_CHANNEL,
                mContext.getString(R.string.update_system_notification_channel),
                NotificationManager.IMPORTANCE_HIGH);
        updateChannel.setBlockableSystem(true);
        updateChannel.enableLights(true);
        updateChannel.enableVibration(true);
        mNoMan.createNotificationChannel(updateChannel);
        mNoMan.notify(UpdateService.NOTIFICATION_INSTALL, builder.build());
    }

	public void cancelNotifications() {
        mNoMan.cancel(UpdateService.NOTIFICATION_INSTALL);
        mNoMan.cancel(UpdateService.NOTIFICATION_UPDATE);
		mIsShowing = false;
    }
	
	public boolean isShowing() {
		return mIsShowing;
	}
}