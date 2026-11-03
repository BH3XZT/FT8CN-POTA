package com.bg7yoz.ft8cn.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bg7yoz.ft8cn.R;
import com.google.android.material.tabs.TabLayout;

/**
 * 解码与呼叫合并界面。支持三种合并布局（见 {@link UiStyleManager}）：
 * 选项卡切换 / 两个列表上下同屏 / 呼叫控制条常驻底部。
 * 原版界面模式下这里只显示解码列表。
 *
 * @author BH3XZT
 */
public class CombinedCallingFragment extends Fragment {

    private static final String TAG_CALLING_LIST = "combined_calling_list";
    private static final String TAG_MY_CALLING = "combined_my_calling";
    private static final String TAG_CONTROL_BAR = "combined_control_bar";
    private static final String STATE_SELECTED_TAB = "combined_selected_tab";
    private static final String STATE_SHOW_CALLING_LIST = "combined_show_calling_list";

    private int uiStyle;
    private TabLayout tabLayout;
    private FrameLayout panelContainer;
    private FrameLayout bottomContainer;

    private CallingListFragment callingListFragment;
    private MyCallingFragment myCallingFragment;
    private CallingControlBarFragment controlBarFragment;
    private Bundle savedState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedState = savedInstanceState;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calling_combined, container, false);
        uiStyle = UiStyleManager.getStyle(requireContext());
        tabLayout = view.findViewById(R.id.call_tab_layout);
        panelContainer = view.findViewById(R.id.call_panel_container);
        bottomContainer = view.findViewById(R.id.call_bottom_container);

        FragmentManager fragmentManager = getChildFragmentManager();
        callingListFragment = (CallingListFragment) fragmentManager
                .findFragmentByTag(TAG_CALLING_LIST);
        myCallingFragment = (MyCallingFragment) fragmentManager
                .findFragmentByTag(TAG_MY_CALLING);
        controlBarFragment = (CallingControlBarFragment) fragmentManager
                .findFragmentByTag(TAG_CONTROL_BAR);

        switch (uiStyle) {
            case UiStyleManager.STYLE_ORIGINAL:
                setupOriginal(fragmentManager, savedState);
                break;
            case UiStyleManager.STYLE_STACKED:
                setupStacked(fragmentManager, savedState);
                break;
            case UiStyleManager.STYLE_CONTROL_BAR:
                setupControlBar(fragmentManager, savedState);
                break;
            default:
                setupTabs(fragmentManager, savedState);
                break;
        }
        return view;
    }

    //----------------------------------------------------------------- 原版
    private void setupOriginal(FragmentManager fragmentManager, Bundle savedInstanceState) {
        tabLayout.setVisibility(View.GONE);
        bottomContainer.setVisibility(View.GONE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (callingListFragment == null) {
            callingListFragment = new CallingListFragment();
            transaction.add(R.id.call_panel_container, callingListFragment, TAG_CALLING_LIST);
        }
        transaction.show(callingListFragment);
        transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.RESUMED);
        transaction.commitNowAllowingStateLoss();
    }

    //----------------------------------------------------------------- 上下同屏
    private void setupStacked(FragmentManager fragmentManager, Bundle savedInstanceState) {
        tabLayout.setVisibility(View.GONE);
        setPanelWeight(3f, 0);
        setBottomWeight(2f, 0);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (callingListFragment == null) {
            callingListFragment = new CallingListFragment();
            transaction.add(R.id.call_panel_container, callingListFragment, TAG_CALLING_LIST);
        }
        if (myCallingFragment == null) {
            myCallingFragment = new MyCallingFragment();
            transaction.add(R.id.call_bottom_container, myCallingFragment, TAG_MY_CALLING);
        }
        transaction.show(callingListFragment);
        transaction.show(myCallingFragment);
        transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.RESUMED);
        transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.RESUMED);
        transaction.commitNowAllowingStateLoss();
    }

    //----------------------------------------------------------------- 控制条常驻底部
    private void setupControlBar(FragmentManager fragmentManager, Bundle savedInstanceState) {
        tabLayout.setVisibility(View.GONE);
        setPanelWeight(1f, 0);
        setBottomWeight(0f, LinearLayout.LayoutParams.WRAP_CONTENT);

        boolean showCallingList = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_SHOW_CALLING_LIST, false);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (callingListFragment == null) {
            callingListFragment = new CallingListFragment();
            transaction.add(R.id.call_panel_container, callingListFragment, TAG_CALLING_LIST);
        }
        if (myCallingFragment == null) {
            myCallingFragment = new MyCallingFragment();
            transaction.add(R.id.call_panel_container, myCallingFragment, TAG_MY_CALLING);
        }
        if (controlBarFragment == null) {
            controlBarFragment = new CallingControlBarFragment();
            transaction.add(R.id.call_bottom_container, controlBarFragment, TAG_CONTROL_BAR);
        }
        if (showCallingList) {
            transaction.show(myCallingFragment);
            transaction.hide(callingListFragment);
            transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.CREATED);
            transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.RESUMED);
        } else {
            transaction.show(callingListFragment);
            transaction.hide(myCallingFragment);
            transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.CREATED);
            transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.RESUMED);
        }
        transaction.show(controlBarFragment);
        transaction.setMaxLifecycle(controlBarFragment, Lifecycle.State.RESUMED);
        transaction.commitNowAllowingStateLoss();
    }

    //----------------------------------------------------------------- 选项卡
    private void setupTabs(FragmentManager fragmentManager, Bundle savedInstanceState) {
        if (tabLayout.getTabCount() == 0) {
            tabLayout.addTab(tabLayout.newTab()
                    .setText(R.string.nav_menu_title_decode_list));
            tabLayout.addTab(tabLayout.newTab()
                    .setText(R.string.nav_menu_title_my_calling));
        }
        boolean restoreCalling = savedInstanceState != null
                && savedInstanceState.getInt(STATE_SELECTED_TAB, 0) == 1;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (callingListFragment == null) {
            callingListFragment = new CallingListFragment();
            transaction.add(R.id.call_panel_container, callingListFragment, TAG_CALLING_LIST);
        }
        if (myCallingFragment == null) {
            myCallingFragment = new MyCallingFragment();
            transaction.add(R.id.call_panel_container, myCallingFragment, TAG_MY_CALLING);
        }
        if (restoreCalling) {
            transaction.show(myCallingFragment);
            transaction.hide(callingListFragment);
            transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.CREATED);
            transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.RESUMED);
        } else {
            transaction.show(callingListFragment);
            transaction.hide(myCallingFragment);
            transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.CREATED);
            transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.RESUMED);
        }
        transaction.commitNowAllowingStateLoss();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showMergedPanel(tab.getPosition() == 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (restoreCalling && tabLayout.getTabAt(1) != null) {
            tabLayout.getTabAt(1).select();
        }
    }

    //----------------------------------------------------------------- 对外接口
    /**
     * 打开“呼叫”相关面板：
     * 选项卡模式下切到呼叫页；上下同屏模式无需切换；控制条模式把主区域切到呼叫列表；
     * 原版界面模式导航到独立的呼叫界面。
     */
    public void showCallingTab() {
        if (uiStyle == UiStyleManager.STYLE_ORIGINAL) {
            NavController navController = Navigation.findNavController(requireActivity()
                    , R.id.fragmentContainerView);
            navController.navigate(R.id.menu_nav_mycalling);
            return;
        }
        if (uiStyle == UiStyleManager.STYLE_STACKED) {
            return;//两个列表已经同屏显示
        }
        if (uiStyle == UiStyleManager.STYLE_CONTROL_BAR) {
            showMergedPanel(true);
            return;
        }
        if (tabLayout != null && tabLayout.getTabCount() > 1) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) {
                tab.select();
            }
        }
    }

    /**
     * 控制条模式：在“解码列表”和“呼叫列表”之间切换主区域。
     */
    public void toggleCallingList() {
        if (uiStyle != UiStyleManager.STYLE_CONTROL_BAR) {
            return;
        }
        boolean showingCalling = myCallingFragment != null && !myCallingFragment.isHidden();
        showMergedPanel(!showingCalling);
    }

    /**
     * 主区域显示解码列表或呼叫列表（选项卡模式/控制条模式共用）。
     */
    private void showMergedPanel(boolean showCalling) {
        if (callingListFragment == null || myCallingFragment == null) {
            return;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (showCalling) {
            transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.CREATED);
            transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.RESUMED);
            transaction.show(myCallingFragment);
            transaction.hide(callingListFragment);
        } else {
            transaction.setMaxLifecycle(myCallingFragment, Lifecycle.State.CREATED);
            transaction.setMaxLifecycle(callingListFragment, Lifecycle.State.RESUMED);
            transaction.show(callingListFragment);
            transaction.hide(myCallingFragment);
        }
        transaction.commitNowAllowingStateLoss();
    }

    private void setPanelWeight(float weight, int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) panelContainer.getLayoutParams();
        lp.weight = weight;
        lp.height = height;
        panelContainer.setLayoutParams(lp);
    }

    private void setBottomWeight(float weight, int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bottomContainer.getLayoutParams();
        lp.weight = weight;
        lp.height = height;
        bottomContainer.setLayoutParams(lp);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uiStyle == UiStyleManager.STYLE_TABS && tabLayout != null) {
            outState.putInt(STATE_SELECTED_TAB, tabLayout.getSelectedTabPosition());
        }
        if (uiStyle == UiStyleManager.STYLE_CONTROL_BAR && myCallingFragment != null) {
            outState.putBoolean(STATE_SHOW_CALLING_LIST, !myCallingFragment.isHidden());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabLayout = null;
        panelContainer = null;
        bottomContainer = null;
    }
}
