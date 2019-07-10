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

package co.aoscp.cota.task;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class RecoveryInfo {

	public static final int TWRP = 1;

    private int id;
    private String name = null;
    private String internalSdcard = null;
    private String externalSdcard = null;

    public RecoveryInfo() {
		setId(TWRP);
        setName("twrp");
        setInternalSdcard("sdcard");
        setExternalSdcard("external_sd");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalSdcard() {
        return internalSdcard;
    }

    public void setInternalSdcard(String sdcard) {
        this.internalSdcard = sdcard;
    }

    public String getExternalSdcard() {
        return externalSdcard;
    }

    public void setExternalSdcard(String sdcard) {
        this.externalSdcard = sdcard;
    }

    public String getCommandsFile() {
        return "openrecoveryscript";
    }

    public String[] getCommands(Context context, String[] items, String[] originalItems)
            throws Exception {
        List<String> commands = new ArrayList<>();
        for (String item : items) {
            commands.add("install " + item);
        }
        return commands.toArray(new String[commands.size()]);
    }
}
