package com.youme.android.rtcdemo;

import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;

import com.youme.voiceengine.api;
import android.util.Log;

public class NormalActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private static String TAG = "YOUME";

    private TextView mTitleTV;
    private TextView mTipsTV;

    private SeekBar mMicSB;
    private TextView mVolumeTV;


    private Switch mSpeakerSwitch;
    private Switch mMicSwitch;
    private Switch mCspeakerSwitch;
    private Switch mCmicSwitch;
    private Switch mCavoidSwitch;
    private Switch mPauseSwitch;

    private String mRoomID;
    private String mUserID;
    private int mVolume = 100;
    private String strTips;

    private Spinner spinner = null;
    private ArrayAdapter<String> adapter = null;
    private String mCuserID = null;

    private void initComponent(){
        mTitleTV = (TextView)findViewById(R.id.titleTag3);
        String strTmp = api.getSdkInfo();
        String strTitle = mUserID+"进入普通房间"+mRoomID+strTmp;
        mTitleTV.setText(strTitle);
        mTipsTV = (TextView)findViewById(R.id.tipsTag3);


        mSpeakerSwitch = (Switch)findViewById(R.id.speakerSwitch3);
        mMicSwitch = (Switch)findViewById(R.id.micSwitch3);



        mSpeakerSwitch.setOnCheckedChangeListener(this);
        mMicSwitch.setOnCheckedChangeListener(this);


    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String [] userList = intent.getExtras().getStringArray("userList");

            adapter = new ArrayAdapter<String>(NormalActivity.this,android.R.layout.simple_spinner_item,userList);
            //设置下拉列表风格
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将适配器添加到spinner中去
            spinner.setAdapter(adapter);
        }
    };

    BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String strTmp = intent.getExtras().getString("status");
            mTipsTV.setText(strTmp);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        mRoomID = this.getIntent().getStringExtra("roomID");
        mUserID = this.getIntent().getStringExtra("userID");
        IntentFilter filter = new IntentFilter(MainActivity.action);
        registerReceiver(broadcastReceiver, filter);

        IntentFilter filter2 = new IntentFilter(MainActivity.action2);
        registerReceiver(broadcastReceiver2, filter2);

        initComponent();
        seekbarView();

        //普通房间模式状态初始化
        //扬声器打开
        api.setSpeakerMute(false);
        //麦克风打开
        api.setMicrophoneMute(false);
        //音量初始化为100
        api.setVolume(100);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.micSwitch3:
                if(compoundButton.isChecked()){
                    api.setMicrophoneMute(false);
                } else {
                    api.setMicrophoneMute(true);
                }
                //打印验证设置以及获取状态的接口
                strTips =(api.getMicrophoneMute()==true?"已关闭":"已打开") + "麦克风";
                mTipsTV.setText(strTips);
                break;
            case R.id.speakerSwitch3:
                if (compoundButton.isChecked()) {
                    api.setSpeakerMute(false);
                } else {
                    api.setSpeakerMute(true);
                }
                //打印验证设置以及获取状态的接口
                strTips = (api.getSpeakerMute() == true ? "已关闭" : "已打开") + "扬声器";
                mTipsTV.setText(strTips);
                break;
            case R.id.cMicSwitch3:
                //为防止被控制用户突然离开房间造成mCuserID失效
                String tempUserID = mCuserID;
                if ( tempUserID == null || tempUserID.isEmpty())
                {
                    //提示控制用户ID不能为空!!!!!!!!!!!
                    mTipsTV.setText("控制用户选择不能为空");
                    return;
                }
                if(compoundButton.isChecked()){
                    api.setOtherMicMute (tempUserID,false);
                } else {
                    api.setOtherMicMute (tempUserID,true);
                }
                break;
            case R.id.cSpeakerSwitch3:
                tempUserID = mCuserID;
                if (tempUserID == null || tempUserID.isEmpty())
                {
                    //提示控制用户ID不能为空!!!!!!!!!!!
                    mTipsTV.setText("控制用户选择不能为空");
                    return;
                }
                if(compoundButton.isChecked()){
                    api.setOtherSpeakerMute(tempUserID,false);
                } else {
                    api.setOtherSpeakerMute(tempUserID,true);
                }
                break;
            case R.id.cAvoidSwitch3:
                tempUserID = mCuserID;
                if (tempUserID == null || tempUserID.isEmpty())
                {
                    //提示控制用户ID不能为空!!!!!!!!!!!
                    mTipsTV.setText("控制用户选择不能为空");
                    return;
                }
                if(compoundButton.isChecked()){
                    //屏蔽
                    api.setListenOtherVoice(tempUserID,false);
                } else {
                    api.setListenOtherVoice(tempUserID,true);
                }
                break;
            case R.id.pauseSwitch3:
                if(compoundButton.isChecked()){
                    api.pauseChannel();
                    strTips = "pause 通话";
                    mTipsTV.setText(strTips);
                } else {
                    api.resumeChannel();
                    strTips = "resume 通话";
                    mTipsTV.setText(strTips);
                }
                break;
            default:
                break;

        }
    }

    // 监听Back键按下事件,退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            System.exit(0);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void seekbarView(){
        mMicSB = (SeekBar) findViewById(R.id.micSeekBar3);
        mVolumeTV = (TextView) findViewById(R.id.volumeTag3);

        mMicSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVolume = progress;
                mVolumeTV.setText(""+mVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                api.setVolume(mVolume);
                //打印验证设置以及获取状态的接口
                int volumeTmp = api.getVolume();
                mTipsTV.setText("当前音量为"+volumeTmp);

            }
        });
    }
    public void onLeaveRoom(View v)
    {
        api.leaveChannelAll();
        NormalActivity.this.finish();
    }
}
