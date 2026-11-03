package com.bg7yoz.ft8cn.ui;
/**
 * 呼叫界面。
 * @author BGY70Z
 * @date 2023-03-20
 */

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

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
import com.bg7yoz.ft8cn.databinding.FragmentMyCallingBinding;
import com.bg7yoz.ft8cn.ft8transmit.FunctionOfTransmit;
import com.bg7yoz.ft8cn.ft8transmit.TransmitCallsign;
import com.bg7yoz.ft8cn.timer.UtcTimer;

import java.util.ArrayList;


public class MyCallingFragment extends Fragment {
    private static final String TAG = "MyCallingFragment";
    private FragmentMyCallingBinding binding;
    private MainViewModel mainViewModel;

    private RecyclerView transmitRecycleView;

    private CallingListAdapter transmitCallListAdapter;

    private FunctionOrderSpinnerAdapter functionOrderSpinnerAdapter;


    static {
        System.loadLibrary("ft8cn");
    }


    /**
     * 马上对发起者呼叫
     *
     * @param message 消息
     */
    //@RequiresApi(api = Build.VERSION_CODES.N)
    private void doCallNow(Ft8Message message) {
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
    }


    /**
     * 菜单选项
     *
     * @param item 菜单
     * @return 是否选择
     */
    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        View actionView = item.getActionView();
        if (actionView == null || !isViewInside(actionView, transmitRecycleView)) {
            return super.onContextItemSelected(item);//不是本列表的菜单，交给其它Fragment处理
        }
        int position = (int) actionView.getTag();
        Ft8Message ft8Message = transmitCallListAdapter.getMessageByPosition(position);
        if (ft8Message == null) return super.onContextItemSelected(item);

        GeneralVariables.resetLaunchSupervision();//复位自动监管
        switch (item.getItemId()) {
            case 1://时序与发送者相反！！！
                Log.d(TAG, "呼叫：" + ft8Message.getCallsignTo());
                if (!mainViewModel.ft8TransmitSignal.isActivated()) {
                    mainViewModel.ft8TransmitSignal.setActivated(true);
                }
                mainViewModel.ft8TransmitSignal.setTransmit(ft8Message.getToCallTransmitCallsign()
                        , 1, ft8Message.extraInfo);
                mainViewModel.ft8TransmitSignal.transmitNow();
                break;

            case 3:
                Log.d(TAG, "呼叫：" + ft8Message.getCallsignFrom());
                doCallNow(ft8Message);
                //if (!mainViewModel.ft8TransmitSignal.isActivated()) {
                //    mainViewModel.ft8TransmitSignal.setActivated(true);
                // }
                // mainViewModel.ft8TransmitSignal.setTransmit(ft8Message.getFromCallTransmitCallsign()
                //        , 1, ft8Message.extraInfo);
                //mainViewModel.ft8TransmitSignal.transmitNow();
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
     * 查询QRZ信息
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = MainViewModel.getInstance(this);
        binding = FragmentMyCallingBinding.inflate(inflater, container, false);

        //当横屏时显示频谱图；上下同屏模式下底部区域较矮，隐藏频谱避免布局异常
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isStackedInCombined()) {
                binding.messageSpectrumView.setVisibility(View.GONE);
            } else {
                binding.messageSpectrumView.run(mainViewModel, this);
            }
        }


        //发射消息的列表
        functionOrderSpinnerAdapter = new FunctionOrderSpinnerAdapter(requireContext(), mainViewModel);
        binding.functionOrderSpinner.setAdapter(functionOrderSpinnerAdapter);
        functionOrderSpinnerAdapter.notifyDataSetChanged();


        //关注的消息列表
        transmitRecycleView = binding.transmitRecycleView;
        transmitCallListAdapter = new CallingListAdapter(this.getContext(), mainViewModel
                , GeneralVariables.transmitMessages, CallingListAdapter.ShowMode.MY_CALLING);
        transmitRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        transmitRecycleView.setAdapter(transmitCallListAdapter);


        transmitCallListAdapter.notifyDataSetChanged();


        //设置消息列表单击/双击动作（原来的左右滑动已取消）
        initListActions();
        //菜单
        requireActivity().registerForContextMenu(transmitRecycleView);

        //显示UTC时间
        mainViewModel.timerSec.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                binding.timerTextView.setText(UtcTimer.getTimeStr(aLong));
            }
        });
        //显示发射频率
        GeneralVariables.mutableBaseFrequency.observe(getViewLifecycleOwner(), new Observer<Float>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Float aFloat) {
                binding.baseFrequencyTextView.setText(String.format(
                        GeneralVariables.getStringFromResource(R.string.sound_frequency_is), aFloat));
            }
        });


        //观察发射状态按钮的变化
        Observer<Boolean> transmittingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (mainViewModel.ft8TransmitSignal.isTransmitting()) {
                    binding.setTransmitImageButton.setImageResource(R.drawable.ic_baseline_send_red_48);
                    binding.setTransmitImageButton.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.view_blink));
                } else {
                    //录音对象也要处于启动状态才可以有发射的状态
                    if (mainViewModel.ft8TransmitSignal.isActivated() && mainViewModel.hamRecorder.isRunning()) {
                        binding.setTransmitImageButton.setImageResource(R.drawable.ic_baseline_send_white_48);
                    } else {
                        binding.setTransmitImageButton.setImageResource(R.drawable.ic_baseline_cancel_schedule_send_off);
                    }
                    binding.setTransmitImageButton.setAnimation(null);
                }

                //暂停播放按键
                if (mainViewModel.ft8TransmitSignal.isTransmitting()) {
                    binding.pauseTransmittingImageButton.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    binding.pauseTransmittingImageButton.setVisibility(View.VISIBLE);
                } else {
                    binding.pauseTransmittingImageButton.setVisibility(View.GONE);
                    binding.pauseTransmittingImageButton.setImageResource(R.drawable.ic_baseline_pause_disable_circle_outline_24);
                }
            }
        };
        //显示发射状态
        mainViewModel.ft8TransmitSignal.mutableIsTransmitting.observe(getViewLifecycleOwner(), transmittingObserver);
        mainViewModel.ft8TransmitSignal.mutableIsActivated.observe(getViewLifecycleOwner(), transmittingObserver);

        //暂停按钮
        binding.pauseTransmittingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.ft8TransmitSignal.setTransmitting(false);
                GeneralVariables.resetLaunchSupervision();//复位自动监管
            }
        });

        //监视命令程序
        mainViewModel.ft8TransmitSignal.mutableFunctions.observe(getViewLifecycleOwner()
                , new Observer<ArrayList<FunctionOfTransmit>>() {
                    @Override
                    public void onChanged(ArrayList<FunctionOfTransmit> functionOfTransmits) {
                        functionOrderSpinnerAdapter.notifyDataSetChanged();
                    }
                });

        //观察指令序号的变化
        mainViewModel.ft8TransmitSignal.mutableFunctionOrder.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (mainViewModel.ft8TransmitSignal.functionList.size() < 6) {
                    binding.functionOrderSpinner.setSelection(0);
                } else {
                    binding.functionOrderSpinner.setSelection(integer - 1);
                }
            }
        });

        //设置当指令序号被选择的事件
        binding.functionOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mainViewModel.ft8TransmitSignal.functionList.size() > 1) {
                    mainViewModel.ft8TransmitSignal.setCurrentFunctionOrder(i + 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //显示当前目标呼号
        mainViewModel.ft8TransmitSignal.mutableToCallsign.observe(getViewLifecycleOwner(), new Observer<TransmitCallsign>() {
            @Override
            public void onChanged(TransmitCallsign transmitCallsign) {
                if (GeneralVariables.toModifier!=null) {
                    binding.toCallsignTextView.setText(String.format(
                            GeneralVariables.getStringFromResource(R.string.target_callsign)
                            , transmitCallsign.callsign+" "+GeneralVariables.toModifier));
                }else {
                    binding.toCallsignTextView.setText(String.format(
                            GeneralVariables.getStringFromResource(R.string.target_callsign)
                            , transmitCallsign.callsign));
                }
            }
        });

        //显示当前发射的时序
        mainViewModel.ft8TransmitSignal.mutableSequential.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Integer integer) {
                binding.transmittingSequentialTextView.setText(
                        String.format(GeneralVariables.getStringFromResource(R.string.transmission_sequence)
                                , integer));
            }
        });

        //设置发射按钮
        binding.setTransmitImageButton.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //如果
                if (!mainViewModel.ft8TransmitSignal.isActivated()) {
                    mainViewModel.ft8TransmitSignal.restTransmitting();
                }
                mainViewModel.ft8TransmitSignal.setActivated(!mainViewModel.ft8TransmitSignal.isActivated());
                GeneralVariables.resetLaunchSupervision();//复位自动监管
            }
        });

        //观察传输消息列表的变化
        //mainViewModel.mutableTransmitMessages.observe(getViewLifecycleOwner(), new Observer<ArrayList<Ft8Message>>() {
        mainViewModel.mutableTransmitMessagesCount.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Integer count) {
                binding.decoderCounterTextView.setText(String.format(
                        GeneralVariables.getStringFromResource(R.string.message_count)
                        , GeneralVariables.transmitMessages.size()));
                //if (count == 0) {
                transmitCallListAdapter.notifyDataSetChanged();
                //} else {
                //    transmitCallListAdapter.notifyItemInserted(
                //            GeneralVariables.transmitMessages.size() - count);
                //}

                //当列表下部稍微多出一些，自动上移
                if (transmitRecycleView.computeVerticalScrollRange()
                        - transmitRecycleView.computeVerticalScrollExtent()
                        - transmitRecycleView.computeVerticalScrollOffset() < 300) {
                    transmitRecycleView.scrollToPosition(transmitCallListAdapter.getItemCount() - 1);
                }
            }
        });

        //清除传输消息列表
        binding.clearMycallListImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.clearTransmittingMessage();
            }
        });

        //复位到CQ按键
        binding.resetToCQImageView.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                mainViewModel.ft8TransmitSignal.resetToCQ();
                GeneralVariables.resetLaunchSupervision();//复位自动监管
            }
        });
        //自由文本输入框的限定操作
        binding.transFreeTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mainViewModel.ft8TransmitSignal.setFreeText(editable.toString().toUpperCase());
            }
        });
        binding.resetToCQImageView.setLongClickable(true);
        binding.resetToCQImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mainViewModel.setTransmitIsFreeText(!mainViewModel.getTransitIsFreeText());
                showFreeTextEdit();
                return true;
            }
        });

        binding.mycallToolsBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralVariables.simpleCallItemMode=!GeneralVariables.simpleCallItemMode;
                transmitRecycleView.setAdapter(transmitCallListAdapter);
                transmitCallListAdapter.notifyDataSetChanged();
                transmitRecycleView.scrollToPosition(transmitCallListAdapter.getItemCount() - 1);
                if (GeneralVariables.simpleCallItemMode){
                    ToastMessage.show(GeneralVariables.getStringFromResource(R.string.message_list_simple_mode));
                }else {
                    ToastMessage.show(GeneralVariables.getStringFromResource(R.string.message_list_standard_mode));
                }
            }
        });


        showFreeTextEdit();
        return binding.getRoot();
    }

    /**
     * 是否处于“两个列表上下同屏”的合并界面。
     */
    private boolean isStackedInCombined() {
        Fragment parent = getParentFragment();
        return parent instanceof CombinedCallingFragment
                && UiStyleManager.getStyle(requireContext()) == UiStyleManager.STYLE_STACKED;
    }

    private void showFreeTextEdit() {
        if (mainViewModel.getTransitIsFreeText()) {
            binding.transFreeTextEdit.setVisibility(View.VISIBLE);
            binding.functionOrderSpinner.setVisibility(View.GONE);
        } else {
            binding.transFreeTextEdit.setVisibility(View.GONE);
            binding.functionOrderSpinner.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置列表单击/双击动作：单击呼叫，双击删除。
     * 原来的左右滑动交互已取消。
     */
    private void initListActions() {
        transmitCallListAdapter.setOnItemActionListener(new CallingListAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(Ft8Message message) {
                if (message == null) {
                    return;
                }
                //呼叫的目标不能是自己，遥测和自由文本不能呼叫
                if (!message.getCallsignFrom().equals("<...>")
                        && !GeneralVariables.checkIsMyCallsign(message.getCallsignFrom())
                        && !(message.i3 == 0 && message.n3 == 0)) {
                    doCallNow(message);
                }
            }

            @Override
            public void onItemDoubleClick(Ft8Message message) {
                if (transmitCallListAdapter.deleteMessage(message)) {
                    //刷新发射消息数量显示
                    mainViewModel.mutableTransmitMessagesCount.setValue(
                            GeneralVariables.transmitMessages.size());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //合并界面切换面板时视图会被销毁，注销长按菜单避免 Activity 持有旧视图
        if (transmitRecycleView != null && getActivity() != null) {
            getActivity().unregisterForContextMenu(transmitRecycleView);
            transmitRecycleView = null;
        }
        binding = null;
    }
}
