package org.jellyfin.androidtv.util;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.jellyfin.androidtv.preference.UserPreferences;
import org.jellyfin.androidtv.preference.constant.AudioBehavior;
import org.jellyfin.sdk.model.api.UserDto;
import org.koin.java.KoinJavaComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * A collection of utility methods, all static.
 */
public class Utils {
    static public final long RUNTIME_TICKS_TO_MS = 10000;

    /**
     * Shows a (long) toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) toast.
     *
     * @param context
     * @param resourceId
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    public static int convertDpToPixel(@NonNull Context ctx, int dp) {
        return convertDpToPixel(ctx, (float) dp);
    }

    // NOTE: Zidoo default = 1.5 AndroidTV = 2.0
    public static int convertDpToPixel(@NonNull Context ctx, float dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static int convertPixelToDP(@NonNull Context ctx, float px) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }

    public static int convertSpToPixel(@NonNull Context ctx, float sp) {
        float density = ctx.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(sp * density);
    }

    public static int convertPixelToSP(@NonNull Context ctx, float px) {
        float density = ctx.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(px / density);
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static String firstToUpper(String value) {
        if (value == null || value.length() == 0) return "";
        return value.substring(0, 1).toUpperCase() + (value.length() > 1 ? value.substring(1) : "");
    }

    /**
     * A null safe version of {@code String.equalsIgnoreCase}.
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    public static boolean equalsIgnoreCaseTrim(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return equalsIgnoreCase(str1.trim(), str2.trim());
    }

    public static <T> T getSafeValue(T value, T defaultValue) {
        if (value == null) return defaultValue;
        return value;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.equals("");
    }

    public static boolean isNonEmpty(String value) {
        return value != null && !value.equals("");
    }

    public static boolean isEmptyTrim(String value) {
        return value == null || value.trim().equals("");
    }

    public static boolean isNonEmptyTrim(String value) {
        return value != null && !value.trim().equals("");
    }

    public static <T> boolean isEmpty(Collection<T> coll) {
        return coll == null || coll.isEmpty();
    }

    public static <T> boolean isNonEmpty(Collection<T> coll) {
        return coll != null && !coll.isEmpty();
    }

    public static String join(String separator, Iterable<String> items) {
        StringBuilder builder = new StringBuilder();

        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());

            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }

        return builder.toString();
    }

    public static String join(String separator, String... items) {
        return join(separator, Arrays.asList(items));
    }

    @NonNull
    public static String getMillisecondsFormated(@Nullable Integer milliseconds) {
        if (milliseconds != null) {
            try {
                long HH = TimeUnit.MILLISECONDS.toHours(milliseconds);
                long MM = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
                long SS = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
                return String.format(Locale.US, "%02d:%02d:%02d", HH, MM, SS);
            } catch (Exception ignored) {
            }
        }
        return String.format(Locale.US, "%02d:%02d:%02d", 0, 0, 0);
    }

    // FIXME: this is broken on Zidoo? I get like 1/10 of the actual bitrate, which triggers transcode on "Auto"
    public static int getMaxBitrate() {
        String maxRate = KoinJavaComponent.<UserPreferences>get(UserPreferences.class).get(UserPreferences.Companion.getMaxBitrate());
        if (maxRate.equals(UserPreferences.MAX_BITRATE_AUTO)) {
            maxRate = "100.0"; // avoid transcode via bitrate
        }
        return (int) (Float.parseFloat(maxRate) * 1_000_000);
//        Long autoRate = KoinJavaComponent.<AutoBitrate>get(AutoBitrate.class).getBitrate();
//        if (maxRate.equals(UserPreferences.MAX_BITRATE_AUTO) && autoRate != null) {
//            return autoRate.intValue();
//        } else {
//            return (int) (Float.parseFloat(maxRate) * 1_000_000);
//        }
    }

    public static int getThemeColor(@NonNull Context context, int resourceId) {
        TypedArray styledAttributes = context.getTheme()
                .obtainStyledAttributes(new int[]{resourceId});
        int themeColor = styledAttributes.getColor(0, 0);
        styledAttributes.recycle();

        return themeColor;
    }

    public static boolean downMixAudio(@NonNull Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am.isBluetoothA2dpOn()) {
            Timber.i("Downmixing audio due to wired headset");
            return true;
        }

        return KoinJavaComponent.<UserPreferences>get(UserPreferences.class).get(UserPreferences.Companion.getAudioBehaviour()) == AudioBehavior.DOWNMIX_TO_STEREO;
    }

    public static long getSafeSeekPosition(long position, long duration) {
        if (position < 0 || duration < 0)
            return 0;
        if (position >= duration)
            return Math.max(duration - 1000, 0);
        return position;
    }

    public static boolean canManageRecordings(UserDto user) {
        return user != null && user.getPolicy().getEnableLiveTvManagement();
    }

    public static double lerp(double from, double to, double value, double minValue, double maxValue) {
        double clampedRange = maxValue - minValue;
        double clampedValue = Math.max(value - minValue, 0);
        return (from + (to - from) * Math.min((clampedValue / clampedRange), 1.0));
    }

    public static int lerp(int from, int to, int value, int minValue, int maxValue) {
        double clampedRange = maxValue - minValue;
        double clampedValue = Math.max(value - minValue, 0);
        return (int) Math.round(from + (to - from) * Math.min((clampedValue / clampedRange), 1.0));
    }

    public static void setViewPadding(@Nullable View view, Integer left, Integer top, Integer right, Integer bottom) {
        if (view == null)
            return;
        view.setPadding(
                left != null ? left : view.getPaddingLeft(),
                top != null ? top : view.getPaddingTop(),
                right != null ? right : view.getPaddingRight(),
                bottom != null ? bottom : view.getPaddingBottom()
        );
    }

    public static void setViewPaddingTop(@Nullable View view, int top) {
        if (view == null)
            return;
        view.setPadding(
                view.getPaddingLeft(),
                top,
                view.getPaddingRight(),
                view.getPaddingBottom()
        );
    }

    public static void setViewPaddingBottom(@Nullable View view, int bottom) {
        if (view == null)
            return;
        view.setPadding(
                view.getPaddingLeft(),
                view.getPaddingTop(),
                view.getPaddingRight(),
                bottom
        );
    }

    public static void setViewPaddingHorizontal(@Nullable View view, int hPadding) {
        if (view == null)
            return;
        view.setPadding(
                hPadding,
                view.getPaddingTop(),
                hPadding,
                view.getPaddingBottom()
        );
    }

    public static void setViewPaddingVertical(@Nullable View view, int vPadding) {
        if (view == null)
            return;
        view.setPadding(
                view.getPaddingLeft(),
                vPadding,
                view.getPaddingRight(),
                vPadding
        );
    }

    public static void setViewPaddingLeft(@Nullable View view, int left) {
        if (view == null)
            return;
        view.setPadding(
                left,
                view.getPaddingTop(),
                view.getPaddingRight(),
                view.getPaddingBottom()
        );
    }

    public static void setFrameLayoutMarginBottom(@Nullable View view, int marginBottom) {
        if (view == null)
            return;
        var lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (lp != null) {
            lp.bottomMargin = marginBottom;
            view.setLayoutParams(lp);
        }
    }

    public static void setConstrainedVerticalWeight(@Nullable View view, int weight) {
        if (view == null)
            return;
        var lp = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (lp != null) {
            lp.verticalWeight = weight;
            view.setLayoutParams(lp);
        }
    }

    public static void addConstrainedVerticalWeight(@Nullable View view, int addedWeight) {
        if (view == null)
            return;
        var lp = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (lp != null) {
            lp.verticalWeight = lp.verticalWeight + addedWeight;
            view.setLayoutParams(lp);
        }
    }

    public static double parseDimensionRatioString(@NonNull String dimensionRatio) {
        double dimensionRatioValue = Double.NaN;
        int dimensionRatioSide = ConstraintLayout.LayoutParams.UNSET;
        int len = dimensionRatio.length();
        int commaIndex = dimensionRatio.indexOf(',');
        if (commaIndex > 0 && commaIndex < len - 1) {
            String dimension = dimensionRatio.substring(0, commaIndex);
            if (dimension.equalsIgnoreCase("W")) {
                dimensionRatioSide = ConstraintLayout.LayoutParams.HORIZONTAL;
            } else if (dimension.equalsIgnoreCase("H")) {
                dimensionRatioSide = ConstraintLayout.LayoutParams.VERTICAL;
            }
            commaIndex++;
        } else {
            commaIndex = 0;
        }
        int colonIndex = dimensionRatio.indexOf(':');
        if (colonIndex >= 0 && colonIndex < len - 1) {
            String nominator = dimensionRatio.substring(commaIndex, colonIndex);
            String denominator = dimensionRatio.substring(colonIndex + 1);
            if (nominator.length() > 0 && denominator.length() > 0) {
                try {
                    double nominatorValue = Double.parseDouble(nominator);
                    double denominatorValue = Double.parseDouble(denominator);
                    if (nominatorValue > 0 && denominatorValue > 0) {
                        if (dimensionRatioSide == ConstraintLayout.LayoutParams.VERTICAL) {
                            dimensionRatioValue = Math.abs(denominatorValue / nominatorValue);
                        } else {
                            dimensionRatioValue = Math.abs(nominatorValue / denominatorValue);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        } else {
            String r = dimensionRatio.substring(commaIndex);
            if (r.length() > 0) {
                try {
                    dimensionRatioValue = Double.parseDouble(r);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return dimensionRatioValue;
    }

    public static int calcDiagonal(int width, int height) {
        if (height == 0 && width == 0)
            return 0;
        return (int) sqrt(pow(height, 2) + pow(width, 2));
    }

    public static double clamp(double value, double minValue, double maxValue) {
        return Math.max(Math.min(value, maxValue), minValue);
    }

    public static int clamp(int value, int minValue, int maxValue) {
        return Math.max(Math.min(value, maxValue), minValue);
    }

    public static boolean equalsDelta(double first, double second, double delta) {
        return Math.abs(first - second) < delta;
    }

    public static long getDeviceMemorySize(Context context) {
        var actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        var memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return memInfo.totalMem;
    }

    public static void breakPoint() {
        assert(Boolean.TRUE);
    }
}
