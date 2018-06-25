package co.aoscp.cota.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DeviceInfoUtils {
    private static final String MOD_VERSION = "ro.modversion";
    private static final String AOSCP_VERSION = "ro.aoscp.version";
    private static final String AOSCP_CODENAME = "ro.aoscp.codename";
    private static final String AOSCP_MAINTENANCE_PATCH = "ro.aoscp.maintenance_patch";
    private static final String PROPERTY_DEVICE = "ro.aoscp.device";
    private static final String PROPERTY_DEVICE_EXT = "ro.product.device";
    private static final String PROPERTY_DEVICE_MODEL = "ro.product.model";
  
    private final String mPatch = UpdateUtils.getProp(AOSCP_MAINTENANCE_PATCH);

    public static String getDate() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(System
                .currentTimeMillis()));
    }

    public static String getRealTime() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public static String getDevice() {
        String device = UpdateUtils.getProp(PROPERTY_DEVICE);
        if (device == null || device.isEmpty()) {
            device = UpdateUtils.getProp(PROPERTY_DEVICE_EXT);
        }
        return device == null ? "" : device.toLowerCase();
    }

    public static String getModel() {
        String model = UpdateUtils.getProp(PROPERTY_DEVICE_MODEL);
        if (model == null || model.isEmpty()) {
            model = UpdateUtils.getProp(PROPERTY_DEVICE_EXT);
        }
        return model == null ? "" : model;
    }

    public static String getExplicitVersion() {
        return UpdateUtils.getProp(MOD_VERSION);
    }

    public static String getVersionDisplay() {
        return UpdateUtils.getProp(AOSCP_VERSION);
    }

    public static String getCodeName() {
        return UpdateUtils.getProp(AOSCP_CODENAME);
    }

    public static String getMaintenancePatch() {
        if (!"".equals(mPatch)) {
            try {
                SimpleDateFormat template = new SimpleDateFormat("yyyy-MM-dd");
                Date patchDate = template.parse(mPatch);
                String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
                mPatch = DateFormat.format(format, patchDate).toString();
            } catch (ParseException e) {}
            return mPatch;
        }
    }

    public static String getReadableDate(String fileDate) {
        try {
            Date currentDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date parsedDate = format.parse(fileDate);
            long diff = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - parsedDate.getTime());
            return diff > 1 ? diff + " days ago" : "today";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
