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
 * limitations under the License
 */

package co.aoscp.cota;

import android.annotation.Nullable;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settingslib.core.lifecycle.ObservableActivity;
import com.android.setupwizardlib.GlifLayout;

import co.aoscp.cota.R;
import co.aoscp.cota.utils.Utils;

public abstract class UpdateSystem extends ObservableActivity /*implements UpdaterListener, 
    DownloadHelper.DownloadCallback*/ {
	
	private static final String TAG = "UpdateSystem";
	
	private int mState;
    private static final int STATE_CHECK = 0;
    private static final int STATE_FOUND = 1;
    private static final int STATE_DOWNLOADING = 2;
    private static final int STATE_INSTALL = 3;
    private static final int STATE_ERROR = 4;

    /*private RomUpdater mRomUpdater;
    private RebootHelper mRebootHelper;

    private PackageInfo mUpdatePackage;
    private List<File> mFiles = new ArrayList<>();

	private DeviceInfoUtils mDeviceUtils;
	
    private UpdateService.NotificationInfo mNotificationInfo;
      
    private Context mContext;
      
    protected Context getContext() {
        return mContext;
    }*/
	
    private TextView mMessage;
    private TextView mSize;
    private Button mButton;
    private TextView mHeader;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UpdateSystem);
		setContentView(R.layout.update_system);

		mHeader = (TextView) findViewById(R.id.header);
		setHeaderText(R.string.update_system_brief_description_update_to_date)
		mButton = (Button) findViewById(R.id.update_action_button);
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        resid = Utils.getTheme(getIntent());
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initViews();
    }

    protected void initViews() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
		if (mButton != null) {
            //mButton.setOnClickListener(mButtonListener);
        }
    }

    protected GlifLayout getLayout() {
        return (GlifLayout) findViewById(R.id.setup_wizard_layout);
    }

    protected void setHeaderText(int resId, boolean force) {
        TextView layoutTitle = getLayout().getHeaderTextView();
        CharSequence previousTitle = layoutTitle.getText();
        CharSequence title = getText(resId);
        if (previousTitle != title || force) {
            if (!TextUtils.isEmpty(previousTitle)) {
                layoutTitle.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
            }
            getLayout().setHeaderText(title);
            setTitle(title);
        }
    }

    protected void setHeaderText(int resId) {
        setHeaderText(resId, false /* force */);
    }
}
