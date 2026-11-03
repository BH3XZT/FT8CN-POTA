package com.bg7yoz.ft8cn.ui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 界面布局方式管理：
 * 0 = 合并界面 + 选项卡切换（默认）
 * 1 = 合并界面 + 两个列表上下同屏
 * 2 = 合并界面 + 呼叫控制条常驻底部
 * 3 = 原版界面（底部 5 个标签，解码/呼叫分开）
 *
 * @author BH3XZT
 */
public class UiStyleManager {
    private static final String PREF_NAME = "ft8cn_ui_style";
    private static final String KEY_STYLE = "ui_style";

    public static final int STYLE_TABS = 0;
    public static final int STYLE_STACKED = 1;
    public static final int STYLE_CONTROL_BAR = 2;
    public static final int STYLE_ORIGINAL = 3;

    private UiStyleManager() {
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static int getStyle(Context context) {
        int style = getPrefs(context).getInt(KEY_STYLE, STYLE_TABS);
        if (style < STYLE_TABS || style > STYLE_ORIGINAL) {
            style = STYLE_TABS;
        }
        return style;
    }

    public static void setStyle(Context context, int style) {
        if (style < STYLE_TABS || style > STYLE_ORIGINAL) {
            style = STYLE_TABS;
        }
        getPrefs(context).edit().putInt(KEY_STYLE, style).apply();
    }

    /**
     * 是否处于原版界面（底部 5 个标签，解码与呼叫分开）。
     */
    public static boolean isOriginal(Context context) {
        return getStyle(context) == STYLE_ORIGINAL;
    }
}
