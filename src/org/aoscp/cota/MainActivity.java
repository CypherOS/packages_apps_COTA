/* 
 * Copyright (C) 2013-2014 Jorrit "Chainfire" Jongma
 * Copyright (C) 2013-2015 The OmniROM Project
 */
/* 
 * This file is part of OpenDelta.
 * 
 * OpenDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenDelta. If not, see <http://www.gnu.org/licenses/>.
 */

package org.aoscp.cota;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView mHeader;
	private TextView mMessage;
	private TextView mSize = null;
	private Button mButton;
	private ProgressBar mProgressBar;
	private TextView mProgressText = null;
	private TextView mProgressSubText;
	private TextView mProgressPercent;
    private Config config;
    private boolean mPermOk;
	
	private Intent mIntent;

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UpdateService.start(this);

        setContentView(R.layout.activity_main);

        mHeader = (TextView) findViewById(R.id.header);
		mMessage = (TextView) findViewById(R.id.message);
		mButton = (Button) findViewById(R.id.action);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressText = (TextView) findViewById(R.id.progress_text);
        mProgressSubText = (TextView) findViewById(R.id.progress_sub_text);
		mProgressPercent = (TextView) findViewById(R.id.progress_percent);
		mSize = (TextView) findViewById(R.id.size);

        config = Config.getInstance(this);
        mPermOk = false;
        requestPermissions();
    }

    private IntentFilter updateFilter = new IntentFilter(
            UpdateService.BROADCAST_INTENT);
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        private String formatLastChecked() {
			return new SimpleDateFormat("HH:mm aa", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String downloadSizeText = "";
			String progressText = "";
            String progressSubText = "";
			String progressPercent = "";
            long current = 0L;
            long total = 1L;
            boolean deltaUpdatePossible = false;
            boolean fullUpdatePossible = false;
            boolean enableProgress = false;
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this);
			String lastChecked = formatLastChecked();

            String state = intent.getStringExtra(UpdateService.EXTRA_STATE);
            // don't try this at home
            if (state != null) {
                try {
                    mHeader.setText(getResources().getIdentifier(
                            "state_" + state, "string", getPackageName()));
                } catch (Exception e) {
                    // String for this state could not be found (displays empty
                    // string)
                    Logger.ex(e);
                }
                // check for first start until check button has been pressed
                // use a special title then - but only once
                if (UpdateService.STATE_ACTION_NONE.equals(state)
                        && !prefs.getBoolean(SettingsActivity.PREF_START_HINT_SHOWN, false)) {
					mHeader.setText(R.string.header_update_unavailable);
					mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
					mButton.setText(R.string.update_action_check);
					mButton.setOnClickListener(mButtonCheckListener);
                }
                // dont spill for progress
                if (!UpdateService.isProgressState(state)) {
                    Logger.d("onReceive state = " + state);
                }
            }

            if (UpdateService.STATE_ERROR_DISK_SPACE.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
                mButton.setText(R.string.update_action_check);
				mButton.setOnClickListener(mButtonCheckListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ERROR_UNKNOWN.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
                mButton.setText(R.string.update_action_check);
				mButton.setOnClickListener(mButtonCheckListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ERROR_DOWNLOAD.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
                mButton.setText(R.string.update_action_check);
				mButton.setOnClickListener(mButtonCheckListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ERROR_CONNECTION.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
                mButton.setText(R.string.update_action_check);
				mButton.setOnClickListener(mButtonCheckListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ERROR_PERMISSIONS.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
				mButton.setText(R.string.update_action_check);
				mButton.setOnClickListener(mButtonCheckListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ERROR_FLASH.equals(state)) {
				mHeader.setText(R.string.header_update_install);
				mMessage.setText(R.string.update_message_install);
                mButton.setText(R.string.update_action_install);
				mButton.setOnClickListener(mButtonFlashListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.VISIBLE);
            } else if (UpdateService.STATE_ACTION_NONE.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
                mButton.setText(R.string.update_action_check);
				mButton.setOnClickListener(mButtonCheckListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ACTION_READY.equals(state)) {
				mHeader.setText(R.string.header_update_install);
				mMessage.setText(R.string.update_message_install);
                mButton.setText(R.string.update_action_install);
				mButton.setOnClickListener(mButtonFlashListener);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);
            } else if (UpdateService.STATE_ACTION_BUILD.equals(state)) {
				mHeader.setText(R.string.header_update_unavailable);
				mMessage.setText(String.format(
                            getResources().getString(R.string.update_message_unavailable, lastChecked)));
                mButton.setText(R.string.update_action_check);
                mProgressBar.setIndeterminate(false);
				mSize.setVisibility(View.GONE);

                final String latestFull = prefs.getString(
                        UpdateService.PREF_LATEST_FULL_NAME,
                        UpdateService.PREF_READY_FILENAME_DEFAULT);
                final String latestDelta = prefs.getString(
                        UpdateService.PREF_LATEST_DELTA_NAME,
                        UpdateService.PREF_READY_FILENAME_DEFAULT);

                String latestDeltaZip = latestDelta != UpdateService.PREF_READY_FILENAME_DEFAULT ? new File(
                        latestDelta).getName() : null;
                String latestFullZip = latestFull != UpdateService.PREF_READY_FILENAME_DEFAULT ? latestFull
                        : null;

                deltaUpdatePossible = latestDeltaZip != null;
                fullUpdatePossible = latestFullZip != null;


                if (deltaUpdatePossible) {
					mHeader.setText(R.string.header_update_available);
					mMessage.setText(R.string.update_message_available);
                    mButton.setText(R.string.update_action_download);
					mButton.setOnClickListener(mButtonBuildListener);
					mSize.setVisibility(View.VISIBLE);
                } else if (fullUpdatePossible) {
					mHeader.setText(R.string.header_update_available);
					mMessage.setText(R.string.update_message_available);
                    mButton.setText(R.string.update_action_download);
				    mButton.setOnClickListener(mButtonBuildListener);
					mSize.setVisibility(View.VISIBLE);
                }
                long mSize = prefs.getLong(
                        UpdateService.PREF_DOWNLOAD_SIZE, -1);
                if(mSize == -1) {
                    downloadSizeText = "";
                } else if (mSize == 0) {
                    downloadSizeText = getString(R.string.update_size_unknown);
                } else {
                    downloadSizeText = Formatter.formatFileSize(context, mSize);
                }
            } else if (UpdateService.STATE_ACTION_SEARCHING.equals(state)
                    || UpdateService.STATE_ACTION_CHECKING.equals(state)) {
                enableProgress = true;
                mProgressBar.setIndeterminate(true);
                current = 1;
				mSize.setVisibility(View.GONE);
            } else {
                enableProgress = true;
                if (UpdateService.STATE_ACTION_DOWNLOADING.equals(state)) {
					mHeader.setText(R.string.header_update_downloading);
					mMessage.setText(R.string.update_message_downloading);
					mButton.setText(R.string.update_action_cancel);
					mButton.setOnClickListener(mButtonStopListener);
					mSize.setVisibility(View.VISIBLE);
                }
				current = intent.getLongExtra(UpdateService.EXTRA_CURRENT,
                        current);
                total = intent.getLongExtra(UpdateService.EXTRA_TOTAL, total);
                mProgressBar.setIndeterminate(false);

                long mSize = prefs.getLong(
                        UpdateService.PREF_DOWNLOAD_SIZE, -1);
                if(mSize == -1) {
                    downloadSizeText = "";
                } else if (mSize == 0) {
                    downloadSizeText = getString(R.string.update_size_unknown);
                } else {
                    downloadSizeText = Formatter.formatFileSize(context, mSize);
                }

                // long --> int overflows FTL (progress.setXXX)
                boolean progressInK = false;
                if (total > 1024L * 1024L * 1024L) {
                    progressInK = true;
                    current /= 1024L;
                    total /= 1024L;
                }
				
				String filename = intent
                        .getStringExtra(UpdateService.EXTRA_FILENAME);
                if (filename != null) {
                    progressText = filename;
                    long ms = intent.getLongExtra(UpdateService.EXTRA_MS, 0);
                    progressPercent = String.format(Locale.ENGLISH, "%.0f %%",
                                intent.getFloatExtra(UpdateService.EXTRA_PROGRESS, 0));

                    if ((ms > 500) && (current > 0) && (total > 0)) {
                        float kibps = ((float) current / 1024f)
                                / ((float) ms / 1000f);
                        if (progressInK)
                            kibps *= 1024f;
                        int sec = (int) (((((float) total / (float) current) * (float) ms) - ms) / 1000f);
                        if (kibps < 10000) {
                            progressSubText = String.format(Locale.ENGLISH,
                                    "%.0f KiB/s, %02d:%02d",
                                    kibps, sec / 60, sec % 60);
                        } else {
                            progressSubText = String.format(Locale.ENGLISH,
                                    "%.0f MiB/s, %02d:%02d",
                                    kibps / 1024f, sec / 60, sec % 60);
                        }
                    }
                }
            }
			mSize.setText(String.format(
                            getResources().getString(R.string.update_size, downloadSizeText)));

            mProgressBar.setProgress((int) current);
            mProgressBar.setMax((int) total);
            mProgressBar.setVisibility(!enableProgress ? View.INVISIBLE : View.VISIBLE);
			mProgressText.setText(progressText);
            mProgressSubText.setText(progressSubText);
			mProgressPercent.setText(progressPercent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateReceiver, updateFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(updateReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateService.startUpdate(this);
    }
	
	private final Button.OnClickListener mButtonCheckListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
			final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
            prefs.edit().putBoolean(SettingsActivity.PREF_START_HINT_SHOWN, true).commit();
            UpdateService.startCheck(MainActivity.this);
		}
	};
	
	private final Button.OnClickListener mButtonBuildListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
			UpdateService.startBuild(MainActivity.this);
		}
	};
	
	private final Button.OnClickListener mButtonFlashListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
			flashRecoveryWarning.run();
		}
	};
	
	private final Button.OnClickListener mButtonStopListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
			stopDownload();
		}
	};

    private Runnable flashRecoveryWarning = new Runnable() {
        @Override
        public void run() {
            // Show a warning message about recoveries we support, depending
            // on the state of secure mode and if we've shown the message before

            final Runnable next = flashWarningFlashAfterUpdateZIPs;

            CharSequence message = null;
            if (!config.getSecureModeCurrent()
                    && !config.getShownRecoveryWarningNotSecure()) {
                message = Html
                        .fromHtml(getString(R.string.recovery_notice_description_not_secure));
                config.setShownRecoveryWarningNotSecure();
            } else if (config.getSecureModeCurrent()
                    && !config.getShownRecoveryWarningSecure()) {
                message = Html
                        .fromHtml(getString(R.string.recovery_notice_description_secure));
                config.setShownRecoveryWarningSecure();
            }

            if (message != null) {
                (new AlertDialog.Builder(MainActivity.this))
                        .setTitle(R.string.recovery_notice_title)
                        .setMessage(message)
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        next.run();
                                    }
                                }).show();
            } else {
                next.run();
            }
        }
    };

    private Runnable flashWarningFlashAfterUpdateZIPs = new Runnable() {
        @Override
        public void run() {
            // If we're in secure mode, but additional ZIPs to flash have been
            // detected, warn the user that these will not be flashed

            final Runnable next = flashStart;

            if (config.getSecureModeCurrent()
                    && (config.getFlashAfterUpdateZIPs().size() > 0)) {
                (new AlertDialog.Builder(MainActivity.this))
                        .setTitle(R.string.flash_after_update_notice_title)
                        .setMessage(
                                Html.fromHtml(getString(R.string.flash_after_update_notice_description)))
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        next.run();
                                    }
                                }).show();
            } else {
                next.run();
            }
        }
    };

    private Runnable flashStart = new Runnable() {
        @Override
        public void run() {
            mButton.setEnabled(false);
            UpdateService.startFlash(MainActivity.this);
        }
    };

    private void stopDownload() {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.edit()
                .putBoolean(
                        UpdateService.PREF_STOP_DOWNLOAD,
                        !prefs.getBoolean(UpdateService.PREF_STOP_DOWNLOAD,
                                false)).commit();
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            mPermOk = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermOk = true;
                }
            }
        }
    }
}
