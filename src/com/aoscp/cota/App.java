package com.aoscp.cota;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.piwik.sdk.DownloadTracker;
import org.piwik.sdk.PiwikApplication;
import org.piwik.sdk.TrackHelper;

import com.aoscp.cota.activities.SystemActivity;

public class App extends Application implements PiwikApplication,
        Application.ActivityLifecycleCallbacks {
			
	private boolean mSystemUpdateInView;

    @Override
    public String getTrackerUrl() {
        return "http://tracker.cypheros.co/";
    }

    @Override
    public Integer getSiteId() {
        return 2;
    }

    @Override
    public void onCreate() {
		mSystemUpdateInView = false;
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated (Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed (Activity activity) {
    }

    @Override
    public void onActivityPaused (Activity activity) {
    }

    @Override
    public void onActivityResumed (Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState (Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted (Activity activity) {
        if (activity instanceof SystemActivity) {
            mSystemUpdateInView = true;
        }
    }

    @Override
    public void onActivityStopped (Activity activity) {
        if (activity instanceof SystemActivity) {
            mSystemUpdateInView = false;
        }
    }

    public boolean isSystemUpdateInView() {
        return mSystemUpdateInView;
    }
}
