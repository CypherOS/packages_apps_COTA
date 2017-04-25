package com.aoscp.cota;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.aoscp.cota.activities.SystemActivity;

public class App extends Application implements
        Application.ActivityLifecycleCallbacks {
			
	private static App sApplication;
			
	private boolean mSystemUpdateInView;

    @Override
    public void onCreate() {
		mSystemUpdateInView = false;
        registerActivityLifecycleCallbacks(this);
		sApplication = this;
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
	
	public static App getApplication() {
        return sApplication;
	}
}
