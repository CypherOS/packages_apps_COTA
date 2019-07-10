/*
 * Copyright 2014 ParanoidAndroid Project
 *
 * This file is part of CypherOS OTA.
 *
 * CypherOS OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CypherOS OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CypherOS OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.aoscp.cota;

import android.app.Activity;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import co.aoscp.cota.task.Server;

import co.aoscp.cota.UpdateNotification;
import co.aoscp.cota.services.UpdateService;
import co.aoscp.cota.utils.DeviceInfoUtils;
import co.aoscp.cota.utils.UpdateUtils;
import co.aoscp.cota.utils.Version;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateManager implements Response.Listener<JSONObject>, Response.ErrorListener {

    private Context mContext;
    private PackageInfo[] mLastUpdates = new PackageInfo[0];
    private List<UpdateListener> mListeners = new ArrayList<>();
    private RequestQueue mQueue;
    private Server mServer;
    private boolean mScanning = false;
    private boolean mFromAlarm;
    private boolean mWithNotification;
    private boolean mServerWorks = false;

    private UpdateNotification mUpdateNotification;

    public UpdateManager(Context context, boolean fromAlarm, boolean withNotification) {
        mContext = context;
        mServer = new Server();
        mFromAlarm = fromAlarm;
        mWithNotification = withNotification;
        mQueue = Volley.newRequestQueue(context);
        mUpdateNotification = new UpdateNotification(context);
    }

    public Version getVersion() {
        return new Version(DeviceInfoUtils.getExplicitVersion());
    }

    public String getDevice() {
        return DeviceInfoUtils.getDevice();
    }

    public int getErrorStringId() {
        return R.string.download_failed_title;
    }

    protected Context getContext() {
        return mContext;
    }

    public PackageInfo[] getLastUpdates() {
        return mLastUpdates;
    }

    public void setLastUpdates(PackageInfo[] infos) {
        if (infos == null) {
            infos = new PackageInfo[0];
        }
        mLastUpdates = infos;
    }

    public void addUpdateListener(UpdateListener listener) {
        mListeners.add(listener);
    }

    public void check() {
        check(false);
    }

    public void check(boolean force) {
        if (mScanning) return;
        if (mFromAlarm && !force) return;
        mServerWorks = false;
        mScanning = true;
        fireStartChecking();
        nextServerCheck();
    }

    private void nextServerCheck() {
        mScanning = true;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(mServer.getUrl(
                getDevice(), getVersion()), null, this, this);
        mQueue.add(jsObjRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        mScanning = false;
        try {
            PackageInfo[] lastUpdates;
            setLastUpdates(null);
            List<PackageInfo> list = mServer.createPackageInfoList(response);
            String error = mServer.getError();

            lastUpdates = list.toArray(new PackageInfo[list.size()]);
            if (lastUpdates.length > 0) {
                mServerWorks = true;
                if (mFromAlarm && mWithNotification) {
                    mUpdateNotification.showUpdate(getContext(), lastUpdates);
                }
            } else {
                if (error != null && !error.isEmpty()) {
                    if (versionError(error)) {
                        return;
                    }
                } else {
                    mServerWorks = true;
                }
            }
            setLastUpdates(lastUpdates);
            fireCheckCompleted(lastUpdates);
        } catch (Exception ex) {
            ex.printStackTrace();
            versionError(null);
        }
    }

    @Override
    public void onErrorResponse(VolleyError ex) {
        mScanning = false;
        versionError(null);
    }

    private boolean versionError(String error) {
        if (!mFromAlarm && !mServerWorks) {
            int id = getErrorStringId();
            if (error != null) {
                UpdateUtils.showToastOnUiThread(getContext(), getContext().getResources().getString(id)
                        + ": " + error);
            } else {
                UpdateUtils.showToastOnUiThread(getContext(), id);
            }
        }
        fireCheckCompleted(null);
        fireCheckError(error);
        return false;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void removeUpdateListener(UpdateListener listener) {
        mListeners.remove(listener);
    }

    protected void fireStartChecking() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                public void run() {
                    for (UpdateListener listener : mListeners) {
                        listener.startChecking();
                    }
                }
            });
        }
    }

    protected void fireCheckCompleted(final PackageInfo[] info) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                public void run() {
                    for (UpdateListener listener : mListeners) {
                        listener.versionFound(info);
                    }
                }
            });
        }
    }

    protected void fireCheckError(final String cause) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                public void run() {
                    for (UpdateListener listener : mListeners) {
                        listener.checkError(cause);
                    }
                }
            });
        }
    }

    public interface PackageInfo extends Serializable {

        String getMd5();

        String getFilename();

        String getPath();

        String getHost();

        String getSize();

        String getText();

        Version getVersion();

        boolean isDelta();

        String getDeltaFilename();

        String getDeltaPath();

        String getDeltaMd5();
    }

    public interface UpdateListener {

        void startChecking();

        void versionFound(PackageInfo[] info);

        void checkError(String cause);
    }
}
