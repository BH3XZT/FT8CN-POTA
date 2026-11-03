package com.bg7yoz.ft8cn.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.bg7yoz.ft8cn.GeneralVariables;
import com.bg7yoz.ft8cn.MainViewModel;
import com.bg7yoz.ft8cn.R;
import com.bg7yoz.ft8cn.ft8transmit.FunctionOfTransmit;
import com.bg7yoz.ft8cn.ft8transmit.TransmitCallsign;

import java.util.ArrayList;

/**
 * 呼叫控制条：合并界面里“呼叫控制条常驻底部”模式下使用。
 * 只保留发射相关的核心控件（目标呼号、发射频率、时序、发送/暂停/CQ、指令/自由文本、切换列表）。
 *
 * @author BH3XZT
 */
public class CallingControlBarFragment extends Fragment {

    private MainViewModel mainViewModel;
    private FunctionOrderSpinnerAdapter functionOrderSpinnerAdapter;

    private TextView toCallsignTextView;
    private TextView baseFrequencyTextView;
    private TextView sequentialTextView;
    private ImageButton setTransmitImageButton;
    private ImageButton pauseImageButton;
    private ImageButton resetToCQImageButton;
    private ImageButton showCallingListImageButton;
    private Spinner functionOrderSpinner;
    private EditText freeTextEdit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calling_control_bar, container, false);
        mainViewModel = MainViewModel.getInstance(this);

        toCallsignTextView = view.findViewById(R.id.barToCallsignTextView);
        baseFrequencyTextView = view.findViewById(R.id.barBaseFrequencyTextView);
        sequentialTextView = view.findViewById(R.id.barSequentialTextView);
        setTransmitImageButton = view.findViewById(R.id.barSetTransmitImageButton);
        pauseImageButton = view.findViewById(R.id.barPauseImageButton);
        resetToCQImageButton = view.findViewById(R.id.barResetToCQImageButton);
        showCallingListImageButton = view.findViewById(R.id.barShowCallingListImageButton);
        functionOrderSpinner = view.findViewById(R.id.barFunctionOrderSpinner);
        freeTextEdit = view.findViewById(R.id.barFreeTextEdit);

        functionOrderSpinnerAdapter = new FunctionOrderSpinnerAdapter(requireContext(), mainViewModel);
        functionOrderSpinner.setAdapter(functionOrderSpinnerAdapter);
        functionOrderSpinnerAdapter.notifyDataSetChanged();

        initObservers();
        initButtons();
        showFreeTextEdit();
        return view;
    }

    private void initObservers() {
        //发射状态
        Observer<Boolean> transmittingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (mainViewModel.ft8TransmitSignal.isTransmitting()) {
                    setTransmitImageButton.setImageResource(R.drawable.ic_baseline_send_red_48);
                    setTransmitImageButton.setAnimation(AnimationUtils.loadAnimation(
                            requireContext(), R.anim.view_blink));
                } else {
                    if (mainViewModel.ft8TransmitSignal.isActivated()
                            && mainViewModel.hamRecorder.isRunning()) {
                        setTransmitImageButton.setImageResource(R.drawable.ic_baseline_send_white_48);
                    } else {
                        setTransmitImageButton.setImageResource(
                                R.drawable.ic_baseline_cancel_schedule_send_off);
                    }
                    setTransmitImageButton.setAnimation(null);
                }
                if (mainViewModel.ft8TransmitSignal.isTransmitting()) {
                    pauseImageButton.setImageResource(
                            R.drawable.ic_baseline_pause_circle_outline_24);
                    pauseImageButton.setVisibility(View.VISIBLE);
                } else {
                    pauseImageButton.setVisibility(View.GONE);
                    pauseImageButton.setImageResource(
                            R.drawable.ic_baseline_pause_disable_circle_outline_24);
                }
            }
        };
        mainViewModel.ft8TransmitSignal.mutableIsTransmitting.observe(
                getViewLifecycleOwner(), transmittingObserver);
        mainViewModel.ft8TransmitSignal.mutableIsActivated.observe(
                getViewLifecycleOwner(), transmittingObserver);

        //发射频率
        GeneralVariables.mutableBaseFrequency.observe(getViewLifecycleOwner(),
                new Observer<Float>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onChanged(Float aFloat) {
                        baseFrequencyTextView.setText(String.format(
                                GeneralVariables.getStringFromResource(R.string.sound_frequency_is)
                                , aFloat));
                    }
                });

        //目标呼号
        mainViewModel.ft8TransmitSignal.mutableToCallsign.observe(getViewLifecycleOwner(),
                new Observer<TransmitCallsign>() {
                    @Override
                    public void onChanged(TransmitCallsign transmitCallsign) {
                        if (GeneralVariables.toModifier != null) {
                            toCallsignTextView.setText(String.format(
                                    GeneralVariables.getStringFromResource(R.string.target_callsign)
                                    , transmitCallsign.callsign + " " + GeneralVariables.toModifier));
                        } else {
                            toCallsignTextView.setText(String.format(
                                    GeneralVariables.getStringFromResource(R.string.target_callsign)
                                    , transmitCallsign.callsign));
                        }
                    }
                });

        //发射时序
        mainViewModel.ft8TransmitSignal.mutableSequential.observe(getViewLifecycleOwner(),
                new Observer<Integer>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onChanged(Integer integer) {
                        sequentialTextView.setText(String.format(
                                GeneralVariables.getStringFromResource(R.string.transmission_sequence)
                                , integer));
                    }
                });

        //指令程序
        mainViewModel.ft8TransmitSignal.mutableFunctions.observe(getViewLifecycleOwner(),
                new Observer<ArrayList<FunctionOfTransmit>>() {
                    @Override
                    public void onChanged(ArrayList<FunctionOfTransmit> functionOfTransmits) {
                        functionOrderSpinnerAdapter.notifyDataSetChanged();
                    }
                });
        mainViewModel.ft8TransmitSignal.mutableFunctionOrder.observe(getViewLifecycleOwner(),
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (mainViewModel.ft8TransmitSignal.functionList.size() < 6) {
                            functionOrderSpinner.setSelection(0);
                        } else {
                            functionOrderSpinner.setSelection(integer - 1);
                        }
                    }
                });
    }

    private void initButtons() {
        //发送/停止发送
        setTransmitImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mainViewModel.ft8TransmitSignal.isActivated()) {
                    mainViewModel.ft8TransmitSignal.restTransmitting();
                }
                mainViewModel.ft8TransmitSignal.setActivated(
                        !mainViewModel.ft8TransmitSignal.isActivated());
                GeneralVariables.resetLaunchSupervision();
            }
        });

        //暂停
        pauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.ft8TransmitSignal.setTransmitting(false);
                GeneralVariables.resetLaunchSupervision();
            }
        });

        //复位到CQ
        resetToCQImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.ft8TransmitSignal.resetToCQ();
                GeneralVariables.resetLaunchSupervision();
            }
        });
        resetToCQImageButton.setLongClickable(true);
        resetToCQImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mainViewModel.setTransmitIsFreeText(!mainViewModel.getTransitIsFreeText());
                showFreeTextEdit();
                return true;
            }
        });

        //切换解码列表 / 呼叫列表
        showCallingListImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getParentFragment() instanceof CombinedCallingFragment) {
                    ((CombinedCallingFragment) getParentFragment()).toggleCallingList();
                }
            }
        });

        //指令序号选择
        functionOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        //自由文本
        freeTextEdit.addTextChangedListener(new TextWatcher() {
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
    }

    private void showFreeTextEdit() {
        if (mainViewModel.getTransitIsFreeText()) {
            freeTextEdit.setVisibility(View.VISIBLE);
            functionOrderSpinner.setVisibility(View.GONE);
        } else {
            freeTextEdit.setVisibility(View.GONE);
            functionOrderSpinner.setVisibility(View.VISIBLE);
        }
    }
}
