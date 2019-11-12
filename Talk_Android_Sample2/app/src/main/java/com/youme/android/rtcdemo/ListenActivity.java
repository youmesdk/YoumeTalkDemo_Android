package com.youme.android.rtcdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.youme.voiceengine.api;

public class ListenActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private TextView mTitleTV;
    private TextView mTipsTV;

    private SeekBar mMicSB;
    private TextView mVolumeTV;

    private Switch mSpeakerSwitch;
    private Switch mPauseSwitch;
    private Switch mMicSwitch;

    private String mRoomID;
    private String mUserID;
    private int mVolume = 100;
    private String strTips;

    private void initComponent(){
        mTitleTV = (TextView)findViewById(R.id.titleTag2);
//        String strTitle = "欢迎"+mUserID+"进入听众模式"+mRoomID;
//        mTitleTV.setText(strTitle);
        String strTmp = api.getSdkInfo();
        String strTitle = mUserID+"进入听众房间"+mRoomID+strTmp;
        mTitleTV.setText(strTitle);

        mTipsTV = (TextView)findViewById(R.id.tipsTag2);


        mSpeakerSwitch = (Switch)findViewById(R.id.speakerSwitch2);
        mPauseSwitch = (Switch)findViewById(R.id.pauseSwitch2);
        mMicSwitch = (Switch)findViewById(R.id.micSwitch2);


        mSpeakerSwitch.setOnCheckedChangeListener(this);
        mPauseSwitch.setOnCheckedChangeListener(this);
        mMicSwitch.setOnCheckedChangeListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        mRoomID = this.getIntent().getStringExtra("roomID");
        mUserID = this.getIntent().getStringExtra("userID");
        initComponent();
        seekbarView();

        //听众模式状态初始化
        //扬声器开启
        api.setSpeakerMute(false);
        //麦克风关闭
        api.setMicrophoneMute(true);
        //音量初始化为100
        api.setVolume(100);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.speakerSwitch2:
                if (compoundButton.isChecked()) {
                    api.setSpeakerMute(false);
                } else {
                    api.setSpeakerMute(true);
                }
                //打印验证设置以及获取状态的接口
                strTips = (api.getSpeakerMute() == true ? "已关闭" : "已打开") + "扬声器";
                mTipsTV.setText(strTips);
                break;
            case R.id.pauseSwitch2:
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
            case R.id.micSwitch2:
                if(compoundButton.isChecked()){
                    api.setMicrophoneMute(false);
                } else {
                    api.setMicrophoneMute(true);
                }
                //打印验证设置以及获取状态的接口
                strTips =(api.getMicrophoneMute()==true?"已关闭":"已打开") + "麦克风";
                mTipsTV.setText(strTips);
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
        mMicSB = (SeekBar) findViewById(R.id.micSeekBar2);
        mVolumeTV = (TextView) findViewById(R.id.volumeTag2);

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
        ListenActivity.this.finish();
    }
}
