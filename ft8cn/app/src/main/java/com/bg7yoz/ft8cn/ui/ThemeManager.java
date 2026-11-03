package com.bg7yoz.ft8cn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.bg7yoz.ft8cn.R;

/**
 * 界面颜色管理：日夜模式切换与日间配色选择。
 * <p>
 * 夜间模式使用 values-night 资源，固定为深蓝色系；
 * 日间模式可选 6 种配色，通过 ThemeOverlay 在 Activity 创建时应用到主题上。
 *
 * @author BH3XZT
 */
public class ThemeManager {
    private static final String PREF_NAME = "ft8cn_theme";
    private static final String KEY_MODE = "theme_mode";//0=跟随系统，1=日间，2=夜间
    private static final String KEY_DAY_COLOR = "theme_day_color";//0..5

    public static final int MODE_FOLLOW_SYSTEM = 0;
    public static final int MODE_DAY = 1;
    public static final int MODE_NIGHT = 2;

    public static final int COLOR_COUNT = 6;

    /**
     * 日间配色的名称资源，顺序与 {@link #DAY_OVERLAYS}、{@link #DAY_SWATCH_COLORS} 一一对应。
     */
    public static final int[] DAY_COLOR_NAMES = new int[]{
            R.string.theme_color_purple,
            R.string.theme_color_blue,
            R.string.theme_color_teal,
            R.string.theme_color_green,
            R.string.theme_color_orange,
            R.string.theme_color_red
    };

    /**
     * 日间配色的色块颜色（用于设置对话框里的圆形色块）。
     */
    public static final int[] DAY_SWATCH_COLORS = new int[]{
            R.color.day_bar_purple,
            R.color.day_bar_blue,
            R.color.day_bar_teal,
            R.color.day_bar_green,
            R.color.day_bar_orange,
            R.color.day_bar_red
    };

    /**
     * 日间配色对应的主题覆盖样式。
     */
    private static final int[] DAY_OVERLAYS = new int[]{
            R.style.ThemeOverlay_Ft8CN_DayPurple,
            R.style.ThemeOverlay_Ft8CN_DayBlue,
            R.style.ThemeOverlay_Ft8CN_DayTeal,
            R.style.ThemeOverlay_Ft8CN_DayGreen,
            R.style.ThemeOverlay_Ft8CN_DayOrange,
            R.style.ThemeOverlay_Ft8CN_DayRed
    };

    private ThemeManager() {
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 读取日夜模式设置。
     */
    public static int getMode(Context context) {
        return getPrefs(context).getInt(KEY_MODE, MODE_FOLLOW_SYSTEM);
    }

    /**
     * 保存日夜模式设置（下次创建 Activity 时生效）。
     */
    public static void setMode(Context context, int mode) {
        getPrefs(context).edit().putInt(KEY_MODE, mode).apply();
    }

    /**
     * 读取日间配色序号。
     */
    public static int getDayColorIndex(Context context) {
        int index = getPrefs(context).getInt(KEY_DAY_COLOR, 0);
        if (index < 0 || index >= COLOR_COUNT) {
            index = 0;
        }
        return index;
    }

    /**
     * 保存日间配色序号。
     */
    public static void setDayColorIndex(Context context, int index) {
        if (index < 0 || index >= COLOR_COUNT) {
            index = 0;
        }
        getPrefs(context).edit().putInt(KEY_DAY_COLOR, index).apply();
    }

    /**
     * 当前是否处于夜间模式（以资源实际解析结果为准）。
     */
    public static boolean isNight(Context context) {
        int uiMode = context.getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 按“保存的模式 + 系统当前配置”判断是否应该使用夜间配色。
     * 用于在 Activity 创建早期可靠判断，不依赖本 Activity 的资源是否已经切换。
     */
    public static boolean isNightMode(Context context) {
        int mode = getMode(context);
        if (mode == MODE_DAY) {
            return false;
        }
        if (mode == MODE_NIGHT) {
            return true;
        }
        int systemUiMode = context.getApplicationContext().getResources()
                .getConfiguration().uiMode;
        return (systemUiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 把保存的日夜模式同步给 AppCompat。应在 Activity 创建的最开始调用。
     */
    public static void applyNightMode(Context context) {
        switch (getMode(context)) {
            case MODE_DAY:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_NIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * 把用户选择的日间配色覆盖到 Activity 主题上。
     * 夜间模式（含跟随系统且系统为深色）不覆盖，固定使用 values-night 的深蓝色。
     * 必须在 setContentView（或 inflate 布局）之前调用。
     */
    public static void applyDayOverlay(Activity activity) {
        if (isNightMode(activity)) {
            return;
        }
        activity.getTheme().applyStyle(DAY_OVERLAYS[getDayColorIndex(activity)], true);
    }

    /**
     * 应用主题：日夜模式 + 日间配色。
     */
    public static void applyTheme(Activity activity) {
        applyNightMode(activity);
        applyDayOverlay(activity);
    }
}
