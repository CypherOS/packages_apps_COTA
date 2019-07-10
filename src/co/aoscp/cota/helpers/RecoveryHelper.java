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

package co.aoscp.cota.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.util.Log;

import co.aoscp.cota.task.RecoveryInfo;
import co.aoscp.cota.utils.FileUtils;
import co.aoscp.cota.utils.UpdateUtils;

public class RecoveryHelper {

    private static final String TAG = "RecoveryHelper";

    private Context mContext;
	private RecoveryInfo mRecovery;

    public RecoveryHelper(Context context) {
        mContext = context;
		mRecovery = new RecoveryInfo();
    }

    public String getCommandsFile(int id) {
        return mRecovery.getCommandsFile();
    }

    public String getRecoveryFilePath(int id, String filePath) {
        String internalStorage = mRecovery.getInternalSdcard();
        String externalStorage = mRecovery.getExternalSdcard();

        String primarySdcard = FileUtils.getPrimarySdCard();
        String secondarySdcard = FileUtils.getSecondarySdCard();

        boolean useInternal = false;
        boolean useExternal = false;

        @SuppressLint("SdCardPath") String[] internalNames = new String[]{
                primarySdcard == null ? "NOPE" : primarySdcard,
                "/mnt/sdcard",
                "/storage/sdcard/",
                "/sdcard",
                "/storage/sdcard0",
                "/storage/emulated/0"
        };

        String[] externalNames = new String[]{
                secondarySdcard == null ? "NOPE" : secondarySdcard,
                "/mnt/extSdCard",
                "/storage/extSdCard/",
                "/extSdCard",
                "/storage/sdcard1",
                "/storage/emulated/1"
        };

        Log.v(TAG, "getRecoveryFilePath:filePath = " + filePath);

        for (int i = 0; i < internalNames.length; i++) {
            String internalName = internalNames[i];

            Log.v(TAG, "getRecoveryFilePath:checking internalName = " + internalName);

            if (filePath.startsWith(internalName)) {
                filePath = filePath.replace(internalName, "/" + internalStorage);

                useInternal = true;

                break;
            }
        }

        if (!useInternal) {
            for (int i = 0; i < externalNames.length; i++) {
                String externalName = externalNames[i];

                Log.v(TAG, "getRecoveryFilePath:checking externalName = " + externalName);

                if (filePath.startsWith(externalName)) {
                    filePath = filePath.replace(externalName, "/" + externalStorage);

                    useExternal = true;

                    break;
                }
            }
        }

        while (filePath.startsWith("//")) {
            filePath = filePath.substring(1);
        }

        Log.v(TAG, "getRecoveryFilePath:new filePath = " + filePath);

        return filePath;
    }

    public String[] getCommands(int id, String[] items, String[] originalItems) throws Exception {
        return mRecovery.getCommands(mContext, items, originalItems);
    }
}
