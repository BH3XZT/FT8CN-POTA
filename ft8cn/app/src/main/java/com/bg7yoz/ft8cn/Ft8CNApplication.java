package com.bg7yoz.ft8cn;

import android.app.Application;

import com.bg7yoz.ft8cn.ui.ThemeManager;

/**
 * 应用入口：在 Activity 创建之前应用保存的日夜模式，避免在 MainActivity.onCreate
 * 里调用 AppCompatDelegate 触发额外的重建/闪屏。
 *
 * @author BH3XZT
 */
public class Ft8CNApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.applyNightMode(this);
    }
}
