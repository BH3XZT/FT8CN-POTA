package com.bg7yoz.ft8cn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bg7yoz.ft8cn.GeneralVariables;
import com.bg7yoz.ft8cn.MainViewModel;
import com.bg7yoz.ft8cn.R;

/**
 * 界面颜色设置对话框：日夜模式切换 + 日间配色选择。
 *
 * @author BH3XZT
 */
public class ThemeSettingsDialog extends Dialog {
    private static final String TAG = "ThemeSettingsDialog";

    private final Activity hostActivity;
    private final MainViewModel mainViewModel;
    private Button nightModeButton;
    private LinearLayout dayColorLinearLayout;

    public ThemeSettingsDialog(Context context, MainViewModel mainViewModel) {
        super(context, R.style.HelpDialog);
        //Dialog 会把 context 包成 ContextThemeWrapper，必须在包装前保存宿主 Activity 用于 recreate
        hostActivity = (context instanceof Activity) ? (Activity) context : null;
        this.mainViewModel = mainViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_dialog_layout);

        nightModeButton = (Button) findViewById(R.id.nightModeButton);
        dayColorLinearLayout = (LinearLayout) findViewById(R.id.dayColorLinearLayout);
        Button doneButton = (Button) findViewById(R.id.themeDoneButton);

        updateNightModeButton();
        //切换日夜模式
        nightModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean night = ThemeManager.isNightMode(getContext());
                dismiss();
                ThemeManager.setMode(getContext()
                        , night ? ThemeManager.MODE_DAY : ThemeManager.MODE_NIGHT);
                //设置 AppCompat 默认模式后，Activity 会自动按新模式重建
                ThemeManager.applyNightMode(getContext());
            }
        });

        buildDayColorSwatches();

        //界面布局方式
        RadioGroup uiStyleRadioGroup = (RadioGroup) findViewById(R.id.uiStyleRadioGroup);
        int style = UiStyleManager.getStyle(getContext());
        uiStyleRadioGroup.check(style == UiStyleManager.STYLE_STACKED ? R.id.uiStyleStacked
                : style == UiStyleManager.STYLE_CONTROL_BAR ? R.id.uiStyleControlBar
                : style == UiStyleManager.STYLE_ORIGINAL ? R.id.uiStyleOriginal
                : R.id.uiStyleTabs);
        uiStyleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                int newStyle;
                if (checkedId == R.id.uiStyleStacked) {
                    newStyle = UiStyleManager.STYLE_STACKED;
                } else if (checkedId == R.id.uiStyleControlBar) {
                    newStyle = UiStyleManager.STYLE_CONTROL_BAR;
                } else if (checkedId == R.id.uiStyleOriginal) {
                    newStyle = UiStyleManager.STYLE_ORIGINAL;
                } else {
                    newStyle = UiStyleManager.STYLE_TABS;
                }
                if (newStyle == UiStyleManager.getStyle(getContext())) {
                    return;
                }
                UiStyleManager.setStyle(getContext(), newStyle);
                dismiss();
                if (hostActivity != null && !hostActivity.isFinishing()) {
                    hostActivity.recreate();
                }
            }
        });

        //一键恢复原版：布局、配色、POTA 编号全部复位（0.93.3）
        Button restoreButton = (Button) findViewById(R.id.restoreOriginalButton);
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                UiStyleManager.setStyle(context, UiStyleManager.STYLE_ORIGINAL);
                ThemeManager.setMode(context, ThemeManager.MODE_FOLLOW_SYSTEM);
                ThemeManager.setDayColorIndex(context, 0);
                ThemeManager.applyNightMode(context);
                GeneralVariables.setPotaPark("");
                if (mainViewModel != null) {
                    mainViewModel.databaseOpr.writeConfig("potaPark", "", null);
                }
                Toast.makeText(context, R.string.restore_done, Toast.LENGTH_LONG).show();
                dismiss();
                if (hostActivity != null && !hostActivity.isFinishing()) {
                    hostActivity.recreate();
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        params.width = (int) (width * 0.85f);
        getWindow().setAttributes(params);
    }

    /**
     * 刷新日夜模式按钮上的文字。
     */
    private void updateNightModeButton() {
        boolean night = ThemeManager.isNightMode(getContext());
        nightModeButton.setText(String.format("%s：%s"
                , getContext().getString(R.string.theme_mode_button)
                , getContext().getString(night ? R.string.theme_mode_night : R.string.theme_mode_day)));
    }

    /**
     * 生成日间配色色块。
     */
    private void buildDayColorSwatches() {
        Context context = getContext();
        dayColorLinearLayout.removeAllViews();
        final int selected = ThemeManager.getDayColorIndex(context);
        int size = dpToPx(context, 40);
        int margin = dpToPx(context, 5);
        for (int i = 0; i < ThemeManager.COLOR_COUNT; i++) {
            View swatch = new View(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.setMargins(margin, margin, margin, margin);
            swatch.setLayoutParams(layoutParams);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(ContextCompat.getColor(context, ThemeManager.DAY_SWATCH_COLORS[i]));
            drawable.setStroke(dpToPx(context, selected == i ? 4 : 1)
                    , ContextCompat.getColor(context
                            , selected == i ? R.color.white : R.color.black));
            swatch.setBackground(drawable);
            swatch.setContentDescription(context.getString(ThemeManager.DAY_COLOR_NAMES[i]));

            final int index = i;
            swatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectDayColor(index);
                }
            });
            dayColorLinearLayout.addView(swatch);
        }
    }

    /**
     * 选择日间配色：保存后重建 Activity，让新的 ThemeOverlay 生效。
     */
    private void selectDayColor(int index) {
        Context context = getContext();
        ThemeManager.setDayColorIndex(context, index);
        buildDayColorSwatches();
        if (ThemeManager.isNightMode(context)) {
            Toast.makeText(context, R.string.theme_color_saved_night_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        ThemeManager.setMode(context, ThemeManager.MODE_DAY);
        //同步 AppCompat 默认模式；配置未变化时它不会自动重建，所以下面再显式重建一次
        ThemeManager.applyNightMode(context);
        dismiss();
        if (hostActivity != null && !hostActivity.isFinishing()) {
            hostActivity.recreate();
        }
    }

    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
