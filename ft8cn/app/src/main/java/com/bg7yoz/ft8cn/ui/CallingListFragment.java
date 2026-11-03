package com.bg7yoz.ft8cn.ui;
/**
 * 解码界面
 * @author BGY70Z
 * @date 2023-03-20
 */

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bg7yoz.ft8cn.Ft8Message;
import com.bg7yoz.ft8cn.GeneralVariables;
import com.bg7yoz.ft8cn.MainViewModel;
import com.bg7yoz.ft8cn.R;
import com.bg7yoz.ft8cn.databinding.FragmentCallingListBinding;
import com.bg7yoz.ft8cn.timer.UtcTimer;

import java.util.ArrayList;

public class CallingListFragment extends Fragment {
    private static final String TAG = "CallingListFragment";

    private FragmentCallingListBinding binding;
    private RecyclerView callListRecyclerView;
    private CallingListAdapter callingListAdapter;
    private MainViewModel mainViewModel;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainViewModel = MainViewModel.getInstance(this);
        binding = FragmentCallingListBinding.inflate(inflater, container, false);
        callListRecyclerView = binding.callingListRecyclerView;

        callingListAdapter = new CallingListAdapter(this.getContext(), mainViewModel
                , mainViewModel.ft8Messages, CallingListAdapter.ShowMode.CALLING_LIST);
        callListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        callListRecyclerView.setAdapter(callingListAdapter);
        callingListAdapter.notifyDataSetChanged();
        callListRecyclerView.scrollToPosition(callingListAdapter.getItemCount() - 1);


        requireActivity().registerForContextMenu(callListRecyclerView);//注册菜单

        //当横屏时显示频谱图
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            assert binding.spectrumView != null;
            binding.spectrumView.run(mainViewModel, this);
        }
        //设置列表单击/双击动作（原来的左右滑动已取消）
        initListActions();

        //监听按钮
        binding.timerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainViewModel.hamRecorder.isRunning()) {
                    mainViewModel.hamRecorder.stopRecord();
                    mainViewModel.ft8TransmitSignal.setActivated(false);
                } else {
                    mainViewModel.hamRecorder.startRecord();
                }
            }
        });
        //清除按钮
        binding.clearCallingListImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.clearFt8MessageList();
                callingListAdapter.notifyDataSetChanged();
                mainViewModel.mutable_Decoded_Counter.setValue(0);
            }
        });
        //观察解码数量
        mainViewModel.mutable_Decoded_Counter.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Integer integer) {
                binding.decoderCounterTextView.setText(
                        String.format(GeneralVariables.getStringFromResource(R.string.message_count_count)
                                , mainViewModel.currentDecodeCount, mainViewModel.ft8Messages.size()));


            }
        });

        mainViewModel.mutableFt8MessageList.observe(getViewLifecycleOwner(), new Observer<ArrayList<Ft8Message>>() {
            @Override
            public void onChanged(ArrayList<Ft8Message> messages) {
                callingListAdapter.notifyDataSetChanged();
                //当列表下部稍微多出一些，自动上移
                if (callListRecyclerView.computeVerticalScrollRange()
                        - callListRecyclerView.computeVerticalScrollExtent()
                        - callListRecyclerView.computeVerticalScrollOffset() < 500) {
                    callListRecyclerView.scrollToPosition(callingListAdapter.getItemCount() - 1);
                }
            }
        });

        //观察UTC时间
        mainViewModel.timerSec.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                binding.timerTextView.setText(UtcTimer.getTimeStr(aLong));
            }
        });

        //观察时间偏移
        mainViewModel.mutableTimerOffset.observe(getViewLifecycleOwner(), new Observer<Float>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Float aFloat) {
                binding.timeOffsetTextView.setText(String.format(
                        getString(R.string.average_offset_seconds), aFloat));
            }
        });

        //显示梅登海德网格与 POTA 公园编号（0.93.2）
        GeneralVariables.mutableMyMaidenheadGrid.observe(getViewLifecycleOwner()
                , new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        updateGridAndPark();
                    }
                });
        GeneralVariables.mutablePotaPark.observe(getViewLifecycleOwner()
                , new Observer<String>() {
                    @Override
                    public void onChanged(String park) {
                        updateGridAndPark();
                    }
                });
        updateGridAndPark();

        //观察是否处于解码状态
        mainViewModel.mutableIsDecoding.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.isDecodingTextView.setText(getString(R.string.decoding));
                }
            }
        });

        //观察解码的时长
        mainViewModel.ft8SignalListener.decodeTimeSec.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Long aLong) {
                binding.isDecodingTextView.setText(String.format(
                        getString(R.string.decoding_takes_milliseconds), aLong));
            }
        });

        //以闪烁动画的方式显示录音状态
        mainViewModel.mutableIsRecording.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.timerImageButton.setImageResource(R.drawable.ic_baseline_mic_red_48);
                    binding.timerImageButton.setAnimation(AnimationUtils.loadAnimation(getContext()
                            , R.anim.view_blink));
                } else {
                    if (mainViewModel.hamRecorder.isRunning()) {
                        binding.timerImageButton.setImageResource(R.drawable.ic_baseline_mic_48);
                    } else {
                        binding.timerImageButton.setImageResource(R.drawable.ic_baseline_mic_off_48);
                    }
                    binding.timerImageButton.setAnimation(null);
                }
            }
        });

        //切换精简模式和标准模式
        binding.callingListToolsBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralVariables.simpleCallItemMode=!GeneralVariables.simpleCallItemMode;
                callListRecyclerView.setAdapter(callingListAdapter);
                callingListAdapter.notifyDataSetChanged();
                callListRecyclerView.scrollToPosition(callingListAdapter.getItemCount() - 1);
                if (GeneralVariables.simpleCallItemMode){
                    ToastMessage.show(GeneralVariables.getStringFromResource(R.string.message_list_simple_mode));
                }else {
                    ToastMessage.show(GeneralVariables.getStringFromResource(R.string.message_list_standard_mode));
                }
            }
        });

        return binding.getRoot();
    }

    /**
     * 刷新网格与 POTA 公园编号的显示。
     */
    private void updateGridAndPark() {
        String grid = String.format(getString(R.string.my_grid)
                , GeneralVariables.getMyMaidenheadGrid());
        String park = GeneralVariables.getPotaParkStr();
        if (park.length() > 0) {
            binding.maidenheadTextView.setText(grid + "  ·  " + park);
        } else {
            binding.maidenheadTextView.setText(grid);
        }
    }

    /**
     * 设置列表单击/双击动作：单击呼叫，双击删除。
     * 原来的左右滑动交互已取消。
     */
    private void initListActions() {
        callingListAdapter.setOnItemActionListener(new CallingListAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(Ft8Message message) {
                if (message == null) {
                    return;
                }
                //呼叫的目标不能是自己，遥测和自由文本不能呼叫
                if (!message.getCallsignFrom().equals("<...>")
                        && !GeneralVariables.checkIsMyCallsign(message.getCallsignFrom())
                        && !(message.i3 == 0 && (message.n3 == 0 || message.n3 == 5))) {
                    doCallNow(message);
                }
            }

            @Override
            public void onItemDoubleClick(Ft8Message message) {
                if (callingListAdapter.deleteMessage(message)) {
                    //刷新解码数量显示
                    mainViewModel.mutable_Decoded_Counter.setValue(mainViewModel.currentDecodeCount);
                }
            }
        });
    }

    /**
     * 马上对发起者呼叫
     *
     * @param message 消息
     */
    //@RequiresApi(api = Build.VERSION_CODES.N)
    private boolean doCallNow(Ft8Message message) {

        mainViewModel.addFollowCallsign(message.getCallsignFrom());
        if (!mainViewModel.ft8TransmitSignal.isActivated()) {
            mainViewModel.ft8TransmitSignal.setActivated(true);
            GeneralVariables.transmitMessages.add(message);//把消息添加到关注列表中
        }
        //呼叫发启者
        mainViewModel.ft8TransmitSignal.setTransmit(message.getFromCallTransmitCallsign()
                , 1, message.extraInfo);
        mainViewModel.ft8TransmitSignal.transmitNow();
        GeneralVariables.resetLaunchSupervision();//复位自动监管
        switchToCallingPanel();//切换到“呼叫”面板
        return true;
    }

    /**
     * 跳转到日志查询界面
     * @param callsign 呼号
     */
    private void navigateToLogFragment(String callsign){
        mainViewModel.queryKey=callsign;//把呼号作为关键字提交
        NavController navController = Navigation.findNavController(requireActivity()
                , R.id.fragmentContainerView);
        navController.navigate(R.id.menu_nav_history);//跳转到日志
    }

    /**
     * 打开呼叫面板：合并界面内部切换，原版界面导航到独立的呼叫页面。
     */
    private void switchToCallingPanel() {
        Fragment parent = getParentFragment();
        if (parent instanceof CombinedCallingFragment) {
            ((CombinedCallingFragment) parent).showCallingTab();
            return;
        }
        NavController navController = Navigation.findNavController(requireActivity()
                , R.id.fragmentContainerView);
        navController.navigate(R.id.menu_nav_mycalling);
    }

    /**
     * 菜单选项
     *
     * @param item 菜单
     * @return 是否
     */
    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        View actionView = item.getActionView();
        if (actionView == null || !isViewInside(actionView, callListRecyclerView)) {
            return super.onContextItemSelected(item);//不是本列表的菜单，交给其它Fragment处理
        }
        int position = (int) actionView.getTag();
        Ft8Message ft8Message = callingListAdapter.getMessageByPosition(position);
        if (ft8Message == null) return super.onContextItemSelected(item);

        switch (item.getItemId()) {
            case 0:
                Log.d(TAG, "关注：" + ft8Message.getCallsignTo());
                mainViewModel.addFollowCallsign(ft8Message.getCallsignTo());
                GeneralVariables.transmitMessages.add(ft8Message);//把消息添加到关注列表中
                break;
            case 1://时序与发送者相反！！！
                Log.d(TAG, "呼叫：" + ft8Message.getCallsignTo());
                mainViewModel.addFollowCallsign(ft8Message.getCallsignTo());
                if (!mainViewModel.ft8TransmitSignal.isActivated()) {
                    mainViewModel.ft8TransmitSignal.setActivated(true);
                    GeneralVariables.transmitMessages.add(ft8Message);//把消息添加到关注列表中
                    GeneralVariables.resetLaunchSupervision();//复位自动监管
                }
                //呼叫被呼叫对象
                mainViewModel.ft8TransmitSignal.setTransmit(ft8Message.getToCallTransmitCallsign()
                        , 1, ft8Message.extraInfo);
                mainViewModel.ft8TransmitSignal.transmitNow();

                switchToCallingPanel();//切换到“呼叫”面板
                break;
            case 2:
                Log.d(TAG, "关注：" + ft8Message.getCallsignFrom());
                mainViewModel.addFollowCallsign(ft8Message.getCallsignFrom());
                GeneralVariables.transmitMessages.add(ft8Message);//把消息添加到关注列表中
                break;
            case 3:
                Log.d(TAG, "呼叫：" + ft8Message.getCallsignFrom());
                doCallNow(ft8Message);
                break;

            case 4://回复
                Log.d(TAG, "回复：" + ft8Message.getCallsignFrom());
                mainViewModel.addFollowCallsign(ft8Message.getCallsignFrom());
                if (!mainViewModel.ft8TransmitSignal.isActivated()) {
                    mainViewModel.ft8TransmitSignal.setActivated(true);
                    GeneralVariables.transmitMessages.add(ft8Message);//把消息添加到关注列表中
                }
                //呼叫发启者
                mainViewModel.ft8TransmitSignal.setTransmit(ft8Message.getFromCallTransmitCallsign()
                        , -1, ft8Message.extraInfo);
                mainViewModel.ft8TransmitSignal.transmitNow();
                GeneralVariables.resetLaunchSupervision();//复位自动监管
                switchToCallingPanel();//切换到“呼叫”面板
                break;
            case 5://to 的QRZ
                showQrzFragment(ft8Message.getCallsignTo());
                break;
            case 6://from 的QRZ
                showQrzFragment(ft8Message.getCallsignFrom());
                break;
            case 7://查to的日志
                navigateToLogFragment(ft8Message.getCallsignTo());
                break;
            case 8://查from的日志
                navigateToLogFragment(ft8Message.getCallsignFrom());
                break;

        }

        return true;
    }

    /**
     * 判断某个View是否属于指定容器（用于区分合并界面里两个列表的长按菜单归属）。
     */
    private static boolean isViewInside(View child, View ancestor) {
        if (child == null || ancestor == null) {
            return false;
        }
        ViewParent parent = child.getParent();
        while (parent != null) {
            if (parent == ancestor) {
                return true;
            }
            if (!(parent instanceof View)) {
                return false;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * 显示QRZ查询界面
     *
     * @param callsign 呼号
     */
    private void showQrzFragment(String callsign) {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        assert navHostFragment != null;//断言不为空
        Bundle bundle = new Bundle();
        bundle.putString(QRZ_Fragment.CALLSIGN_PARAM, callsign);
        navHostFragment.getNavController().navigate(R.id.QRZ_Fragment, bundle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //合并界面切换面板时视图会被销毁，注销长按菜单避免 Activity 持有旧视图
        if (callListRecyclerView != null && getActivity() != null) {
            getActivity().unregisterForContextMenu(callListRecyclerView);
            callListRecyclerView = null;
        }
        binding = null;
    }
}