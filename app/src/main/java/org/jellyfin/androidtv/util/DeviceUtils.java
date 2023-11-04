package org.jellyfin.androidtv.util;

import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class DeviceUtils {
    // Chromecast with Google TV
    private static final String CHROMECAST_GOOGLE_TV = "Chromecast";

    private static final String FIRE_TV_PREFIX = "AFT";
    // Fire TV Stick Models
    private static final String FIRE_STICK_MODEL_GEN_1 = "AFTM";
    private static final String FIRE_STICK_MODEL_GEN_2 = "AFTT";
    private static final String FIRE_STICK_MODEL_GEN_3 = "AFTSSS";
    private static final String FIRE_STICK_LITE_MODEL = "AFTSS";
    private static final String FIRE_STICK_4K_MODEL = "AFTMM";
    private static final String FIRE_STICK_4K_MAX_MODEL = "AFTKA";
    // Fire TV Cube Models
    private static final String FIRE_CUBE_MODEL_GEN_1 = "AFTA";
    private static final String FIRE_CUBE_MODEL_GEN_2 = "AFTR";
    // Fire TV (Box) Models
    private static final String FIRE_TV_MODEL_GEN_1 = "AFTB";
    private static final String FIRE_TV_MODEL_GEN_2 = "AFTS";
    private static final String FIRE_TV_MODEL_GEN_3 = "AFTN";
    // Nvidia Shield TV Model
    private static final String SHIELD_TV_MODEL = "SHIELD Android TV";
    // Zidoo
    private static final String ZIDOO_MODEL_Z9X = "Z9X";
    private static final String ZIDOO_MODEL_Z9X_P = "Z9X PRO";

    private static final String UNKNOWN = "Unknown";

    private static final HashMap<String, String> sSystemPropertyMap = new HashMap<String, String>();

    @NonNull
    static String getBuildModel() {
        return Build.MODEL != null ? Build.MODEL : UNKNOWN;
    }

    @NonNull
    static String getBrand() {
        return Build.BRAND != null ? Build.BRAND : UNKNOWN;
    }

    @NonNull
    static String getManufacturer() {
        return Build.MANUFACTURER != null ? Build.MANUFACTURER : UNKNOWN;
    }

    public static String getSystemPropertyCached(String key) {
        String prop = sSystemPropertyMap.get(key);
        if (prop == null) {
            prop = getSystemProperty(key, "none");
            sSystemPropertyMap.put(key, prop);
        }
        return prop;
    }

    private static String getSystemProperty(String key, String def) {
        try {
            final ClassLoader cl = ClassLoader.getSystemClassLoader();
            final Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
            final Class<?>[] paramTypes = new Class[] { String.class, String.class };
            final Method get = SystemProperties.getMethod("get", paramTypes);
            final Object[] params = new Object[] { key, def };
            return (String) get.invoke(SystemProperties, params);
        } catch (Exception e){
            return def;
        }
    }

    public static boolean isChromecastWithGoogleTV() {
        return getBuildModel().equals(CHROMECAST_GOOGLE_TV);
    }

    public static boolean isFireTv() {
        return getBuildModel().startsWith(FIRE_TV_PREFIX);
    }

    public static boolean isFireTvStickGen1() {
        return getBuildModel().equals(FIRE_STICK_MODEL_GEN_1);
    }

    public static boolean isFireTvGen2() {
        return getBuildModel().equals(FIRE_TV_MODEL_GEN_2);
    }

    public static boolean isFireTvStick4k() {
        return Arrays.asList(FIRE_STICK_4K_MODEL, FIRE_STICK_4K_MAX_MODEL)
            .contains(getBuildModel());
    }

    public static boolean isShieldTv() {
        return getBuildModel().equals(SHIELD_TV_MODEL);
    }

    // we have a mismatch "ZIDOO" vs "Realtek vs "rtk" for old/new API's
    private static boolean isZidooDevice() {
        if (getBrand().equalsIgnoreCase("ZIDOO"))
            return true;
        if (getManufacturer().equalsIgnoreCase("ZIDOO"))
            return true;

        return false;
    }

    public static boolean isZidooRTK() {
        if (!isZidooDevice())
            return false;
        if (getManufacturer().equalsIgnoreCase("rtk") || getManufacturer().equalsIgnoreCase("realtek"))
            return true;
        if (getBrand().equalsIgnoreCase("rtk") || getBrand().equalsIgnoreCase("realtek"))
            return true;

        return false;
    }

    public static boolean hasNewZidooApi() {
        if (isZidooDevice()) {
            // new 2023->"v1.0.45", 2022->"v6.4.42" and "v6.7.30" older "v2.3.88"
            String version = getSystemPropertyCached("ro.product.version").replaceFirst("v", "").replaceFirst("(_G)", "").replaceFirst("(beta)","");
            final String[] splitArray = version.split("\\.", 3);
            if (splitArray.length == 3) {
                try {
                    int majorV = Integer.parseInt(splitArray[0]);
                    int middleV = Integer.parseInt(splitArray[1]);
                    int minorV = Integer.parseInt(splitArray[2]);
                    if (majorV == 6 && (middleV == 4 || middleV == 7) && minorV >= 30) // RTD1619
                        return true;
                    if (majorV == 1 && middleV == 0 && minorV >= 45) // RTD1619BPD
                        return true;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return false;
    }

    public static boolean has4kVideoSupport() {
        String buildModel = getBuildModel();

        return !Arrays.asList(
                // These devices only support a max video resolution of 1080p
                FIRE_STICK_MODEL_GEN_1,
                FIRE_STICK_MODEL_GEN_2,
                FIRE_STICK_MODEL_GEN_3,
                FIRE_STICK_LITE_MODEL,
                FIRE_TV_MODEL_GEN_1,
                FIRE_TV_MODEL_GEN_2
        ).contains(buildModel) && !buildModel.equals(UNKNOWN);
    }

    public static boolean is60() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
